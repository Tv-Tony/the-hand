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
import tv.toner.utils.ServoUtil;
import tv.toner.utils.TiltCalculator;

@Component
public class SerialHandController implements ApplicationListener<GloveEvent> {

    private static final Logger log = LogManager.getLogger(SerialHandController.class);

    private static final String PORT_NAME = "/dev/ttyUSB1";

    private final SensorManager sensorManager;

    private final DigitalSmoothFilter fingerOneFilter;
    private final DigitalSmoothFilter fingerTwoFilter;
    private final DigitalSmoothFilter fingerThreeFilter;

    private final SerialPort serialPort;

    @Autowired
    public SerialHandController(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        // Sensor filters (1 per sensor)
        this.fingerOneFilter = new DigitalSmoothFilter(20, 10);
        this.fingerTwoFilter = new DigitalSmoothFilter(20, 10);
        this.fingerThreeFilter =  new DigitalSmoothFilter(20, 10);

        this.serialPort = new SerialPort(PORT_NAME);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
            log.info("Serial connection to robotic hand established on port: {}", serialPort.getPortName());
        } catch (SerialPortException e) {
            log.warn("Unable to connect to robotic hand on port {}. Is the device connected? Retrying will continue...", serialPort.getPortName());
        }
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {

        Mpu6050 mpuOne = sensorManager.getLatestData("0");
        Mpu6050 mpuTwo = sensorManager.getLatestData("1");
        Mpu6050 mpuThree = sensorManager.getLatestData("2");

        Mpu6050 filteredMpuOne = fingerOneFilter.filter(mpuOne);
        Mpu6050 filteredMpuTwo = fingerTwoFilter.filter(mpuTwo);
        Mpu6050 filteredMpuThree = fingerThreeFilter.filter(mpuThree);

        Double angleFingerOne = -TiltCalculator.calculateTiltAngles(filteredMpuOne).getRoll();
        Double angleFingerTwo = -TiltCalculator.calculateTiltAngles(filteredMpuTwo).getRoll();
        Double angleFingerThree = -TiltCalculator.calculateTiltAngles(filteredMpuThree).getRoll();


        int indexServoAngle = ServoUtil.mapRollToServo(angleFingerOne);
        int middleServoAngle = ServoUtil.mapRollToServo(angleFingerTwo);
        int ringServoAngle = ServoUtil.mapRollToServo(angleFingerThree);

        String command = String.format("I:%03d;M:%03d;R:%03d\n", indexServoAngle, middleServoAngle, ringServoAngle);

        try {
            serialPort.writeString(command);
            log.info("Sent to Arduino: {}", command.trim());
        } catch (SerialPortException e) {
            log.debug("Failed to send command to Arduino");
        }
    }
}
