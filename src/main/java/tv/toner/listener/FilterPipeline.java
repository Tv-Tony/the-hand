package tv.toner.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class FilterPipeline implements ApplicationListener<GloveEvent> {

    private final SensorManager sensorManager;

    private final DigitalSmoothFilter fingerOneFilter;
    private final DigitalSmoothFilter fingerTwoFilter;
    private final DigitalSmoothFilter fingerThreeFilter;

    private final AngleFilter fingerOneAngleFilter;
    private final AngleFilter fingerTwoAngleFilter;
    private final AngleFilter fingerThreeAngleFilter;

    private final ChartUtil rawChartUtil;
    private final ChartUtil angleChartDataUtil;

    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, Double> pendingData = new ConcurrentHashMap<>();

    @Autowired
    public FilterPipeline(ChartManager chartManager, SensorManager sensorManager, ApplicationEventPublisher eventPublisher) {
        this.sensorManager = sensorManager;

        // Sensor filters (1 per sensor)
        this.fingerOneFilter = new DigitalSmoothFilter(9, 10);
        this.fingerTwoFilter = new DigitalSmoothFilter(9, 10);
        this.fingerThreeFilter = new DigitalSmoothFilter(9, 10);

        // Chart utilities
        this.rawChartUtil = chartManager.getChartUtil(ChartKey.DATA_CHART);
        this.angleChartDataUtil = chartManager.getChartUtil(ChartKey.ANGLE_CHART);

        this.fingerOneAngleFilter = new AngleFilter(3);
        this.fingerTwoAngleFilter = new AngleFilter(3);
        this.fingerThreeAngleFilter = new AngleFilter(3);

        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {
        // ───────────────────────────────────────────────────────
        // STEP 1: Get Raw Sensor Data
        // ───────────────────────────────────────────────────────
        Mpu6050 mpuOne   = sensorManager.getLatestData("0");
        Mpu6050 mpuTwo   = sensorManager.getLatestData("1");
        Mpu6050 mpuThree = sensorManager.getLatestData("2");

        if (mpuOne == null || mpuTwo == null || mpuThree == null) {
            return;
        }

        // ───────────────────────────────────────────────────────
        // STEP 2: Plot Raw Data
        // ───────────────────────────────────────────────────────
        rawChartUtil.updateChartData(SeriesDef.FINGER_ONE, mpuOne.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_TWO, mpuTwo.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_THREE, mpuThree.getAx());

        // ───────────────────────────────────────────────────────
        // STEP 3: Apply Filters to Raw Data
        // ───────────────────────────────────────────────────────
        Mpu6050 filteredOne   = fingerOneFilter.filter(mpuOne);
        Mpu6050 filteredTwo   = fingerTwoFilter.filter(mpuTwo);
        Mpu6050 filteredThree = fingerThreeFilter.filter(mpuThree);

        rawChartUtil.updateChartData(SeriesDef.FINGER_ONE_FILTERED, filteredOne.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_TWO_FILTERED, filteredTwo.getAy());
        rawChartUtil.updateChartData(SeriesDef.FINGER_THREE_FILTERED, filteredThree.getAx());

        // ───────────────────────────────────────────────────────
        // STEP 4: Calculate Raw Angles from Filtered Accel Data
        // ───────────────────────────────────────────────────────
        double rollOneRaw   = -TiltCalculator.calculateTiltAngles(filteredOne).getRoll();
        double rollTwoRaw   = -TiltCalculator.calculateTiltAngles(filteredTwo).getRoll();
        double rollThreeRaw = -TiltCalculator.calculateTiltAngles(filteredThree).getRoll();

        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_ONE, rollOneRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_TWO, rollTwoRaw);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_THREE, rollThreeRaw);

        // ───────────────────────────────────────────────────────
        // STEP 5: Apply Smoothing Filter to Angles
        // ───────────────────────────────────────────────────────
        double rollOneFiltered   = fingerOneAngleFilter.filterAngle(rollOneRaw);
        double rollTwoFiltered   = fingerTwoAngleFilter.filterAngle(rollTwoRaw);
        double rollThreeFiltered = fingerThreeAngleFilter.filterAngle(rollThreeRaw);

        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_ONE_FILTERED, rollOneFiltered);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_TWO_FILTERED, rollTwoFiltered);
        angleChartDataUtil.updateChartData(SeriesDef.ANGLE_DATA_FINGER_THREE_FILTERED, rollThreeFiltered);

        pendingData.put(mpuOne.getBitAddress(), rollOneFiltered);
        pendingData.put(mpuTwo.getBitAddress(), rollTwoFiltered);
        pendingData.put(mpuThree.getBitAddress(), rollThreeFiltered);

        ProcessedAngleEvent processedAngleEvent = new ProcessedAngleEvent(this, new HashMap<>(pendingData));

        eventPublisher.publishEvent(processedAngleEvent);

        pendingData.clear();
    }
}
