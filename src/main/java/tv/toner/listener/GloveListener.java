package tv.toner.listener;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


public class GloveListener implements SerialPortEventListener, Runnable {

    private final static String PORT_NAME = "COM8";

    private SerialPort serialPort;

    private boolean keepRunning = true;

    public GloveListener() {
        this.serialPort = new SerialPort(PORT_NAME);
    }

    public void startListening() {
        new Thread(this).start();
    }

    public void stopListening() {
        keepRunning = false;
        try {
            if (serialPort.isOpened()) {
                serialPort.closePort();
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            serialPort.openPort();
            serialPort.setParams(38400, 8, 1, 0);
            serialPort.addEventListener(this);
            System.out.println("Listening on " + serialPort.getPortName());

            // Keep the thread alive
            while (keepRunning) {
                Thread.sleep(1000);
            }

            serialPort.closePort(); // Close when stopping
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) { // Data is available
            try {
                String data = serialPort.readString();
                if (data != null) {
                    System.out.println("Received: " + data.trim());

                    // Example: Parse data (split by commas)
                    String[] parsedData = data.trim().split(",");
                    for (String value : parsedData) {
                        System.out.println("Parsed: " + value);
                    }
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
