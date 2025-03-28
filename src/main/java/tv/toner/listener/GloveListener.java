package tv.toner.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import tv.toner.TheHand;

/**
 * Serial Port Listener, used to establish connection with Arduino Glove
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 * Todo in the future we need to implement reconnection implementation if arduino is disconnected
 */
@Service
public class GloveListener implements SerialPortEventListener {

    private static final Logger log = LogManager.getLogger(TheHand.class);
    private final static String PORT_NAME = "COM8";
    private final static Long RECONNECT_DELAY = 10000L;
    private static final int BAUD_RATE = 38400;

    private final SerialPort serialPort;
    private final ScheduledExecutorService scheduler;
    private final StringBuilder buffer = new StringBuilder();


    @Value("${serial.port.refresh-rate:16}")
    private int refreshRate;

    public GloveListener() {
        this.serialPort = new SerialPort(PORT_NAME);
        scheduler = Executors.newScheduledThreadPool(1);
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
                String data = serialPort.readString();
                if (data != null) {
                    buffer.append(data);
                    int newlineIndex = buffer.indexOf("\n"); // Check if we have received a complete line
                    while (newlineIndex != -1) {
                        String line = buffer.substring(0, newlineIndex).trim(); // Extract the full line until the newline character
                        buffer.delete(0, newlineIndex + 1); // Remove the processed line from the buffer

                        log.info("Received line: {}", line);

                        processData(line);

                        // Check if there's more data left in the buffer
                        newlineIndex = buffer.indexOf("\n");
                    }
                }
            } catch (SerialPortException e) {
                log.error("Error reading from serial port", e);
            }
        }
    }

    /**
     * This is a mock processing method that will be implemented in the future
     */
    private void processData(String line) {
        log.info("MOCK PROCESSING: {}", line);
    }
}
