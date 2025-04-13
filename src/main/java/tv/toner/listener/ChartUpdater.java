package tv.toner.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import tv.toner.defs.ChartKey;
import tv.toner.defs.SeriesDefs;
import tv.toner.entity.Mpu6050;
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

    private final ChartUtil rawChartUtil;
    private final ChartUtil angleChartDataUtil;

    @Autowired
    public ChartUpdater(ChartManager chartManager, SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        // Sensor filters (1 per sensor)
        this.fingerOneFilter = new DigitalSmoothFilter(20, 10);
        this.fingerTwoFilter = new DigitalSmoothFilter(20, 10);

        // Chart utilities
        this.rawChartUtil = chartManager.getChartUtil(ChartKey.AX_DATA_CHART);
        this.angleChartDataUtil = chartManager.getChartUtil(ChartKey.X_ANGLE_CHART);
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {
        Mpu6050 mpuOne = sensorManager.getLatestData("0");
        Mpu6050 mpuTwo = sensorManager.getLatestData("1");
//        Mpu6050 mpuThree = sensorManager.getLatestData("2");
        if (mpuOne == null)
            return;
        if (mpuTwo == null)
            return;
//        if (mpuThree == null)
//            return;
        rawChartUtil.updateChartData(SeriesDefs.FINGER_ONE, mpuOne.getAy());
        rawChartUtil.updateChartData(SeriesDefs.FINGER_ONE_FILTERED, fingerOneFilter.filter(mpuOne).getAy());
        rawChartUtil.updateChartData(SeriesDefs.FINGER_TWO, mpuTwo.getAy());
        rawChartUtil.updateChartData(SeriesDefs.FINGER_TWO_FILTERED, fingerTwoFilter.filter(mpuTwo).getAy());
//        rawChartUtil.updateChartData(SeriesDefs.FINGER_THREE, mpuThree.getAx());

        angleChartDataUtil.updateChartData(SeriesDefs.ANGLE_DATA_FINGER_ONE, TiltCalculator.calculateTiltAngles(mpuOne).getRoll());
        angleChartDataUtil.updateChartData(SeriesDefs.ANGLE_DATA_FINGER_TWO, TiltCalculator.calculateTiltAngles(mpuTwo).getRoll());
//        angleChartDataUtil.updateChartData(SeriesDefs.ANGLE_DATA_FINGER_THREE, mpuUtility.getXAngleFromRaw(mpuThree.getAx()));
    }
}
