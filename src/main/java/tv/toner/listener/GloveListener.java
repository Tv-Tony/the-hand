package tv.toner.listener;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import lombok.Setter;
import tv.toner.TheHand;
import tv.toner.entity.Mpu6050;

/**
 * Serial Port Listener, used to establish connection with Arduino Glove
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 * Todo in the future we need to implement reconnection implementation if arduino is disconnected
 */
@Component
@Setter
public class GloveListener implements SerialPortEventListener {

    private static final Logger log = LogManager.getLogger(TheHand.class);
    private final static String PORT_NAME = "COM8";
    private final static Long RECONNECT_DELAY = 10000L;
    private static final int BAUD_RATE = 38400;

    private final SerialPort serialPort;
    private final ScheduledExecutorService scheduler;
    private final ApplicationEventPublisher eventPublisher;

    private Mpu6050 lastValidMpuData = null;

    @Value("${serial.port.refresh-rate:16}")
    private int refreshRate;

    public GloveListener(ApplicationEventPublisher eventPublisher) {
        this.serialPort = new SerialPort(PORT_NAME);
        scheduler = Executors.newScheduledThreadPool(1);
        this.eventPublisher = eventPublisher;
    }

    /**
     * After this object is created via Spring Boot and all dependencies are injected, it starts a scheduled task
     * called listenForData() at a fixed interval
     */
    @PostConstruct
    public void startListening() {
        scheduler.scheduleAtFixedRate(this::listenForData, 0, refreshRate, TimeUnit.MILLISECONDS);
    }

    /**
     * Method safely closes the serial port if its open on application closure
     */
    @PreDestroy
    public void stopListening() {
        scheduler.shutdown();  // Shut down the scheduler when stopping
        try {
            if (serialPort.isOpened()) {
                serialPort.closePort();
                log.info("Serial port closed: {}", serialPort.getPortName());
            }
        } catch (SerialPortException e) {
            log.error("Error closing serial port: {}", serialPort.getPortName(), e);
        }
    }

    /**
     * This method is responsible for checking the status of the serial port, if not open it tries to connect, if
     * there is an error it will retry after a fixed interval
     */
    private void listenForData() {
        try {
            if (!serialPort.isOpened()) {
                serialPort.openPort();
                serialPort.setParams(BAUD_RATE, 8, 1, 0);
                serialPort.addEventListener(this);
                log.info("Listening on Serial Port {}", serialPort.getPortName());
            }
        } catch (SerialPortException e) {
            log.warn("Unable to establish connection with serial port: {}, retrying in {} seconds...", serialPort.getPortName(), (RECONNECT_DELAY / 1000));
            sleepThread();
            scheduler.schedule(this::listenForData, 0, TimeUnit.SECONDS);  // Retry connection after a delay
        }
    }

    /**
     * This is a basic method to retry the connection after certain time if unable to establish connection
     */
    public void sleepThread() {
        try {
            Thread.sleep(RECONNECT_DELAY); // Wait before retrying
        } catch (InterruptedException f) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This event is triggered when connection is established with the serial port, we expect the arduino to send us
     * data in complete lines, therefore this method reads by newline characters. Furthermore, this method will get the
     * raw data for us to process.
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                // Read binary data from serial port
                byte[] data = serialPort.readBytes();

                if (data != null && data.length >= 12) {  // Check if we have enough data (12 bytes = 6 values * 2 bytes each)
                    byte address = data[0];
                    ByteBuffer buffer = ByteBuffer.wrap(data, 1, data.length - 1); //A byte buffer is used to improve performance when writing a stream of data

                    Mpu6050 mpuData = readFromBytes(buffer, address);
                    lastValidMpuData = mpuData;

                    onDataReceived(mpuData);

                }
            } catch (SerialPortException e) {
                log.error("Error reading from serial port", e);
            }
        }
    }

    public void onDataReceived(Mpu6050 data) {
        log.debug("Publishing Data {}", data);
        GloveEvent event = new GloveEvent(this, data);
        eventPublisher.publishEvent(event);
    }

    private Mpu6050 readFromBytes(ByteBuffer buffer, byte address) {
        try {

            if (buffer.remaining() < 12) {  // 6 values * 2 bytes each
                log.warn("[MPU6050] Insufficient data in buffer. Expected 12 bytes, but got {}", buffer.remaining());
                return getLastValidMpuData(address);  // Return the previous valid data
            }
            int ax = buffer.getShort();
            int ay = buffer.getShort();
            int az = buffer.getShort();
            int gx = buffer.getShort();
            int gy = buffer.getShort();
            int gz = buffer.getShort();

            return new Mpu6050(
                    Integer.toHexString(address & 0xFF),  // MPU5060 ADDRESS
                    ax, ay, az, gx, gy, gz,
                    LocalDateTime.now()
            );
        } catch (BufferUnderflowException e) {
            log.error("[MPU6050] Buffer underflow error while reading sensor data: {}", e.getMessage(), e);
            return getLastValidMpuData(address);  // Return the last valid data
        }
    }

    private Mpu6050 getLastValidMpuData(byte address) {
        if (lastValidMpuData != null) {
            return lastValidMpuData;
        } else {
            return new Mpu6050(
                    Integer.toHexString(address & 0xFF),
                    0, 0, 0, 0, 0, 0,
                    LocalDateTime.now()
            );
        }
    }
}
