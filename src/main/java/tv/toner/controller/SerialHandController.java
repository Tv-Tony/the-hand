package tv.toner.controller;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tv.toner.entity.Mpu6050;
import tv.toner.filter.DigitalSmoothFilter;
import tv.toner.listener.GloveEvent;
import tv.toner.manager.SensorManager;
import tv.toner.utils.TiltCalculator;

@Component
public class SerialHandController implements ApplicationListener<GloveEvent> {

    private static final Logger log = LogManager.getLogger(SerialHandController.class);

    private static final String PORT_NAME = "/dev/ttyUSB2";

    private final SensorManager sensorManager;

    private final DigitalSmoothFilter fingerOneFilter;
    private final DigitalSmoothFilter fingerTwoFilter;

    private SerialPort serialPort;

    @Autowired
    public SerialHandController(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        // Sensor filters (1 per sensor)
        this.fingerOneFilter = new DigitalSmoothFilter(20, 10);
        this.fingerTwoFilter = new DigitalSmoothFilter(20, 10);

        this.serialPort = new SerialPort(PORT_NAME);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
            System.out.println("Serial connection to robotic hand established.");
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {

        Mpu6050 mpuOne = sensorManager.getLatestData("0");
        Mpu6050 mpuTwo = sensorManager.getLatestData("1");

        Mpu6050 filteredMpuOne = fingerOneFilter.filter(mpuOne);
        Mpu6050 filteredMpuTwo = fingerTwoFilter.filter(mpuTwo);

        Double angleFingerOne = -TiltCalculator.calculateTiltAngles(filteredMpuOne).getRoll();
        Double angleFingerTwo = -TiltCalculator.calculateTiltAngles(filteredMpuTwo).getRoll();

        int indexServoAngle = mapRollToServo(angleFingerOne);
        int middleServoAngle = mapRollToServo(angleFingerTwo);

        String command = String.format("I:%03d;M:%03d\n", indexServoAngle, middleServoAngle);

        try {
            serialPort.writeString(command);
            log.info("Sent to Arduino: {}", command.trim());
        } catch (SerialPortException e) {
            log.error("Failed to send command to Arduino", e);
        }
    }

    public static int mapRollToServo(double roll) {
        double clamped = Math.max(0, Math.min(90, roll));
        double normalized = clamped / 90.0;
        return (int) Math.round(180 * (1.0 - normalized));
    }
}
