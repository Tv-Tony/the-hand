package tv.toner.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import tv.toner.entity.Mpu6050;
import tv.toner.filter.DigitalSmoothFilter;
import tv.toner.utils.ChartUtil;

/**
 * This class has an event listener that on new data received by the GloveListener class, we can plot the data,
 * right now its autowired, in the future for multiple map instances this will need to be refactored
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
@Component
public class ChartUpdater implements ApplicationListener<GloveEvent> {

    private final ChartUtil chartUtil;

    DigitalSmoothFilter digitalSmoothFilter;

    @Autowired
    public ChartUpdater(ChartUtil chartUtil) {
        this.chartUtil = chartUtil;
        this.digitalSmoothFilter = new DigitalSmoothFilter(10, 10);
    }

    @Override
    public void onApplicationEvent(GloveEvent event) {
        Mpu6050 mpu = event.getData();
        if (mpu == null)
            return;
        chartUtil.updateChartData(mpu.getAx());

        Mpu6050 smoothReading = digitalSmoothFilter.filter(mpu);
        chartUtil.updateFilteredChartData(smoothReading.getAx());
    }
}
