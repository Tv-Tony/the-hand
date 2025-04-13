package tv.toner.controller;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDef;
import tv.toner.entity.Mpu6050;
import tv.toner.filter.DigitalSmoothFilter;
import tv.toner.listener.ProcessedAngleEvent;
import tv.toner.manager.ChartManager;
import tv.toner.utils.ChartUtil;
import tv.toner.utils.ServoUtil;

@Controller
@Lazy // So that this is Initialize after the charts are created in java fx
public class SerialHandController implements ApplicationListener<ProcessedAngleEvent> {

    private static final Logger log = LogManager.getLogger(SerialHandController.class);

    private static final String PORT_NAME = "/dev/ttyUSB1";

    private final SerialPort serialPort;

    private final ChartUtil servoChartUtil;

    private final DigitalSmoothFilter fingerOneFilter;
    private final DigitalSmoothFilter fingerTwoFilter;
    private final DigitalSmoothFilter fingerThreeFilter;

    @Autowired
    public SerialHandController(ChartManager chartManager) {
        // Sensor filters (1 per sensor)
        this.fingerOneFilter = new DigitalSmoothFilter(3, 10);
        this.fingerTwoFilter = new DigitalSmoothFilter(3, 10);
        this.fingerThreeFilter = new DigitalSmoothFilter(3, 10);

        this.servoChartUtil = chartManager.getChartUtil(ChartKey.SERVO_CHART);
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
    public void onApplicationEvent(ProcessedAngleEvent event) {

        double rollOneFiltered   = event.getProcessedAngleWithKey("0");
        double rollTwoFiltered   = event.getProcessedAngleWithKey("1");
        double rollThreeFiltered = event.getProcessedAngleWithKey("2");



        int indexServoAngle = ServoUtil.mapRollToServo(rollOneFiltered);
        int middleServoAngle = ServoUtil.mapRollToServo(rollTwoFiltered);
        int ringServoAngle = ServoUtil.mapRollToServo(rollThreeFiltered);

        servoChartUtil.updateChartData(SeriesDef.FINGER_ONE_SERVO, indexServoAngle);
        servoChartUtil.updateChartData(SeriesDef.FINGER_TWO_SERVO, middleServoAngle);
        servoChartUtil.updateChartData(SeriesDef.FINGER_THREE_SERVO, ringServoAngle);

        int indexServoAngleFiltered = fingerOneFilter.filter(new Mpu6050(indexServoAngle)).getAy();
        int middleServoAngleFiltered = fingerTwoFilter.filter(new Mpu6050(middleServoAngle)).getAy();
        int ringServoAngleFiltered   = fingerThreeFilter.filter(new Mpu6050(ringServoAngle)).getAy();

        servoChartUtil.updateChartData(SeriesDef.FINGER_ONE_SERVO_FILTERED, indexServoAngleFiltered);
        servoChartUtil.updateChartData(SeriesDef.FINGER_TWO_SERVO_FILTERED, middleServoAngleFiltered);
        servoChartUtil.updateChartData(SeriesDef.FINGER_THREE_SERVO_FILTERED, ringServoAngleFiltered);


        String command = String.format("I:%03d;M:%03d;R:%03d\n", indexServoAngle, middleServoAngle, ringServoAngle);

        try {
            serialPort.writeString(command);
            log.debug("Sent to Arduino: {}", command.trim());
        } catch (SerialPortException e) {
            log.debug("Failed to send command to Arduino");
        }
    }
}
