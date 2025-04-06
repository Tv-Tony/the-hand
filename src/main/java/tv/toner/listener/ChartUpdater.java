package tv.toner.listener;

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
import tv.toner.utils.MPU6050Utility;

/**
 * This class has an event listener that on new data received by the GloveListener class, we can plot the data,
 * right now its autowired, in the future for multiple map instances this will need to be refactored
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Component
@Lazy // So that this is Initialize after the charts are created in java fx
public class ChartUpdater implements ApplicationListener<GloveEvent> {

    private final DigitalSmoothFilter rawDigitalSmoothFilter;
    private final SensorManager sensorManager;

    private final MPU6050Utility mpuUtility;

    private final ChartUtil rawChartUtil;
    private final ChartUtil angleChartDataUtil;

    @Autowired
    public ChartUpdater(ChartManager chartManager, SensorManager sensorManager) {
        this.rawDigitalSmoothFilter = new DigitalSmoothFilter(10, 10);
        mpuUtility = new MPU6050Utility();
        mpuUtility.setCalibrationPoints(33000, 22000, 6000);
        this.sensorManager = sensorManager;

        // Initialize the charts
        this.rawChartUtil = chartManager.getChartUtil(ChartKey.AX_DATA_CHART);
        this.angleChartDataUtil = chartManager.getChartUtil(ChartKey.X_ANGLE_CHART);
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {
        Mpu6050 mpuOne = sensorManager.getLatestData("0");
        Mpu6050 mpuTwo = sensorManager.getLatestData("1");
        Mpu6050 mpuThree = sensorManager.getLatestData("2");
        if (mpuOne == null)
            return;
        if (mpuTwo == null)
            return;
        if (mpuThree == null)
            return;
        rawChartUtil.updateChartData(SeriesDefs.FINGER_ONE, mpuOne.getAx());
        rawChartUtil.updateChartData(SeriesDefs.FINGER_TWO, mpuTwo.getAx());
        rawChartUtil.updateChartData(SeriesDefs.FINGER_THREE, mpuThree.getAx());

        // Todo lets implement these smooth readings later
        Mpu6050 smoothReading = rawDigitalSmoothFilter.filter(mpuOne);
        if (smoothReading == null)
            return;

        angleChartDataUtil.updateChartData(SeriesDefs.ANGLE_DATA_FINGER_ONE, mpuUtility.getXAngleFromRaw(mpuOne.getAx()));
        angleChartDataUtil.updateChartData(SeriesDefs.ANGLE_DATA_FINGER_TWO, mpuUtility.getXAngleFromRaw(mpuTwo.getAx()));
        angleChartDataUtil.updateChartData(SeriesDefs.ANGLE_DATA_FINGER_THREE, mpuUtility.getXAngleFromRaw(mpuThree.getAx()));
    }
}
