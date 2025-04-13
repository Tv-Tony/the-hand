package tv.toner.listener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import tv.toner.entity.Mpu6050;
import tv.toner.manager.SensorManager;

/**
 * Serial Port Listener, used to establish connection with Arduino Glove
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 * Todo in the future we need to implement reconnection implementation if arduino is disconnected
 */
@Component
@Setter
public class GloveListener implements SerialPortEventListener {

    private static final Logger log = LogManager.getLogger(GloveListener.class);

    /**
     * For Windows "COM8"
     */
    private final static String PORT_NAME = "/dev/ttyUSB0";
    private final static Long RECONNECT_DELAY = 10000L;
    private static final int BAUD_RATE = 115200;

    private final int sensorCount = 2; // Todo application properties variable
    private final Map<String, Mpu6050> pendingData = new ConcurrentHashMap<>();

    private static final Pattern LINE_PATTERN = Pattern.compile(
            "\\$\\s*ID:\\s*(\\d+)\\s*\\|\\s*Tms:\\s*(\\d+)\\s*\\|\\s*acc:\\s*(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)\\s*\\|\\s*rot:\\s*(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)\\s*\\|\\s*rpy:\\s*(-?\\d+(?:\\.\\d+)?),\\s*(-?\\d+(?:\\.\\d+)?),\\s*(-?\\d+(?:\\.\\d+)?)"
    );
    private final StringBuilder buffer = new StringBuilder();

    private final SerialPort serialPort;
    private final ScheduledExecutorService scheduler;
    private final ApplicationEventPublisher eventPublisher;

    private final SensorManager sensorManager;

    @Value("${serial.port.refresh-rate:16}")
    private int refreshRate;

    public GloveListener(ApplicationEventPublisher eventPublisher, SensorManager sensorManager) {
        this.serialPort = new SerialPort(PORT_NAME);
        scheduler = Executors.newScheduledThreadPool(1);
        this.eventPublisher = eventPublisher;
        this.sensorManager = sensorManager;
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
                byte[] data = serialPort.readBytes();
                if (data != null) {
                    for (byte b : data) {
                        char c = (char) b;
                        if (c == '\n') {
                            String line = buffer.toString().trim();
                            buffer.setLength(0);
                            processLine(line);
                        } else {
                            buffer.append(c);
                        }
                    }
                }
            } catch (SerialPortException e) {
                log.error("Error reading from serial port", e);
            }
        }
    }

    public void onDataReceived(Mpu6050 data) {
        log.debug("Received Data {}", data);

        // ✅ Store in SensorManager immediately
        sensorManager.updateSensorData(data.getBitAddress(), data);

        // ✅ Store in temporary map for sync
        pendingData.put(data.getBitAddress(), data);

        // ✅ Check if we received all sensor updates
        if (pendingData.size() == sensorCount) {
            log.debug("Publishing GloveEvent for {} sensors", sensorCount);

            // Create GloveEvent with a snapshot of the current sensor data
            GloveEvent event = new GloveEvent(this, new HashMap<>(pendingData));

            // Publish the event
            eventPublisher.publishEvent(event);

            // Clear pending buffer
            pendingData.clear();
        }
    }

    private void processLine(String line) {
        Matcher matcher = LINE_PATTERN.matcher(line);
        if (matcher.matches()) {

            Mpu6050 mpu = new Mpu6050(
                    matcher.group(1),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)),
                    Integer.parseInt(matcher.group(6)),
                    Integer.parseInt(matcher.group(7)),
                    Integer.parseInt(matcher.group(8)),
                    Float.parseFloat(matcher.group(9)),
                    Float.parseFloat(matcher.group(10)),
                    Float.parseFloat(matcher.group(11)),
                    LocalDateTime.now()
            );
            onDataReceived(mpu);
        } else {
            log.warn("Invalid line format: {}", line);
        }
    }
}
