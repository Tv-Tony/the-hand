package tv.toner.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDef;
import tv.toner.entity.Mpu6050;
import tv.toner.filter.AngleFilter;
import tv.toner.filter.DigitalSmoothFilter;
import tv.toner.manager.ChartManager;
import tv.toner.manager.SensorManager;
import tv.toner.utils.ChartUtil;
import tv.toner.utils.TiltCalculator;

/**
 * This class has an event listener that on new data received by the GloveListener class, we can plot the data,
 * right now its autowired, in the future for multiple map instances this will need to be refactored
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Slf4j
@Component
@Lazy // So that this is Initialize after the charts are created in java fx
public class ChartUpdater implements ApplicationListener<GloveEvent> {

    private final SensorManager sensorManager;

    private final DigitalSmoothFilter fingerOneFilter;
    private final DigitalSmoothFilter fingerTwoFilter;
    private final DigitalSmoothFilter fingerThreeFilter;

    private final AngleFilter fingerOneAngleFilter;
    private final AngleFilter fingerTwoAngleFilter;
    private final AngleFilter fingerThreeAngleFilter;

    private final ChartUtil rawChartUtil;
    private final ChartUtil angleChartDataUtil;

    @Autowired
    public ChartUpdater(ChartManager chartManager, SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        // Sensor filters (1 per sensor)
        this.fingerOneFilter = new DigitalSmoothFilter(20, 10);
        this.fingerTwoFilter = new DigitalSmoothFilter(20, 10);
        this.fingerThreeFilter = new DigitalSmoothFilter(20, 10);

        // Chart utilities
        this.rawChartUtil = chartManager.getChartUtil(ChartKey.DATA_CHART);
        this.angleChartDataUtil = chartManager.getChartUtil(ChartKey.ANGLE_CHART);

        this.fingerOneAngleFilter = new AngleFilter(20);
        this.fingerTwoAngleFilter = new AngleFilter(20);
        this.fingerThreeAngleFilter = new AngleFilter(20);
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {
        // Retrieve sensor data for each finger.
        Mpu6050 mpuOne = sensorManager.getLatestData("0");
        Mpu6050 mpuTwo = sensorManager.getLatestData("1");
        Mpu6050 mpuThree = sensorManager.getLatestData("2");

        // If any sensor data is null, exit early.
        if (mpuOne == null || mpuTwo == null || mpuThree == null) {
            return;
        }

        rawChartUtil.updateChartData(SeriesDef.FINGER_ONE, mpuOne.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_TWO, mpuTwo.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_THREE, mpuThree.getAx());

        Mpu6050 filteredMpuOne = fingerOneFilter.filter(mpuOne);
        Mpu6050 filteredMpuTwo = fingerTwoFilter.filter(mpuTwo);
        Mpu6050 filteredMpuThree = fingerThreeFilter.filter(mpuThree);

        rawChartUtil.updateChartData(SeriesDef.FINGER_ONE_FILTERED, filteredMpuOne.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_TWO_FILTERED, filteredMpuTwo.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_THREE_FILTERED, filteredMpuThree.getAx());

        // Finger One
        double rollOneRaw = -TiltCalculator.calculateTiltAngles(filteredMpuOne).getRoll();
        double rollOneFiltered = fingerOneAngleFilter.filterAngle(rollOneRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_ONE, rollOneRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_ONE_FILTERED, rollOneFiltered);

        // Finger Two
        double rollTwoRaw = -TiltCalculator.calculateTiltAngles(filteredMpuTwo).getRoll();
        double rollTwoFiltered = fingerTwoAngleFilter.filterAngle(rollTwoRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_TWO, rollTwoRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_TWO_FILTERED, rollTwoFiltered);

        // Finger Three
        double rollThreeRaw = -TiltCalculator.calculateTiltAngles(filteredMpuThree).getRoll();
        double rollThreeFiltered = fingerThreeAngleFilter.filterAngle(rollThreeRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_THREE, rollThreeRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_THREE_FILTERED, rollThreeFiltered);
    }
}
