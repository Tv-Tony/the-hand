package tv.toner.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import tv.toner.entity.Mpu6050;
import tv.toner.filter.DigitalSmoothFilter;
import tv.toner.manager.SensorManager;
import tv.toner.utils.AngleChartDataUtil;
import tv.toner.utils.RawChartDataUtil;
import tv.toner.utils.MPU6050Utility;

/**
 * This class has an event listener that on new data received by the GloveListener class, we can plot the data,
 * right now its autowired, in the future for multiple map instances this will need to be refactored
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Component
public class ChartUpdater implements ApplicationListener<GloveEvent> {

    private final RawChartDataUtil rawRawChartDataUtil;
    private final AngleChartDataUtil angleChartDataUtil;

    private final DigitalSmoothFilter rawDigitalSmoothFilter;
    private final SensorManager sensorManager;

    private final MPU6050Utility mpuUtility;

    @Autowired
    public ChartUpdater(RawChartDataUtil rawRawChartDataUtil, AngleChartDataUtil angleChartDataUtil, SensorManager sensorManager) {
        this.rawRawChartDataUtil = rawRawChartDataUtil;
        this.angleChartDataUtil = angleChartDataUtil;
        this.rawDigitalSmoothFilter = new DigitalSmoothFilter(10, 10);
        mpuUtility = new MPU6050Utility();
        mpuUtility.setCalibrationPoints(33000, 22000, 6000);
        this.sensorManager = sensorManager;
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {
        Mpu6050 mpu = sensorManager.getLatestData("0");
        if (mpu == null)
            return;
        rawRawChartDataUtil.updateChartData(mpu.getAx());
        Mpu6050 smoothReading = rawDigitalSmoothFilter.filter(mpu);
        rawRawChartDataUtil.updateFilteredChartData(smoothReading.getAx());

        if (smoothReading == null)
            return;

        angleChartDataUtil.updateChartData(mpuUtility.getXAngleFromRaw(mpu.getAx()));
        angleChartDataUtil.updateFilteredChartData(mpuUtility.getXAngleFromRaw(smoothReading.getAx()));
    }
}
