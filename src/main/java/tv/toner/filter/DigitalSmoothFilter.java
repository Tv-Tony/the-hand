package tv.toner.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tv.toner.entity.Mpu6050;

/**
 * This filter was inspired by the link
 *
 * <a href="https://thecavepearlproject.org/2015/05/22/calibrating-any-compass-or-accelerometer-for-arduino/">...</a>
 *
 * The trim percentage removes the highest and lowest percent of it
 * The window has a rolling average, allowing for smoothing of the data
 *
 * @author Antonin Vychodil <a_vychodil [at] utb.cz>
 *
 */
public class DigitalSmoothFilter {

    private final int windowSize;
    private final int trimPercent;

    // Rolling buffers for each accelerometer axis:
    private final List<Double> windowAx;
    private final List<Double> windowAy;
    private final List<Double> windowAz;

    /**
     * Check some values to make sure the digital filter is a valid one
     */
    public DigitalSmoothFilter(int windowSize, int trimPercent) {
        if (windowSize < 3) {
            throw new IllegalArgumentException("windowSize must be at least 3.");
        }
        if (trimPercent < 0 || trimPercent >= 50) {
            throw new IllegalArgumentException("trimPercent must be between 0 and 49.");
        }
        this.windowSize = windowSize;
        this.trimPercent = trimPercent;
        // Initialize rolling windows for each axis
        this.windowAx = new ArrayList<>(windowSize);
        this.windowAy = new ArrayList<>(windowSize);
        this.windowAz = new ArrayList<>(windowSize);
    }

    /**
     * New readings received from the MPU6050 are sent here via event listener, then its processed
     */
    public Mpu6050 filter(Mpu6050 reading) {

        double ax = reading.getAx();
        double ay = reading.getAy();
        double az = reading.getAz();

        // Update rolling window for X-axis
        if (windowAx.size() >= windowSize) {
            windowAx.remove(0); // remove the oldest if at capacity
        }
        windowAx.add(ax);

        // Update rolling window for Y-axis
        if (windowAy.size() >= windowSize) {
            windowAy.remove(0);
        }
        windowAy.add(ay);

        // Update rolling window for Z-axis
        if (windowAz.size() >= windowSize) {
            windowAz.remove(0);
        }
        windowAz.add(az);

        // Compute smoothed values for each axis
        double smoothX = computeSmoothValue(windowAx);
        double smoothY = computeSmoothValue(windowAy);
        double smoothZ = computeSmoothValue(windowAz);

        // Update the Mpu6050 object with filtered results
        reading.setAx((int) smoothX);
        reading.setAy((int) smoothY);
        reading.setAz((int) smoothZ);

        return reading;
    }

    /**
     * Helper method to compute the trimmed-average of the values in the window.
     * Follows Paul Badger's digitalSmooth algorithm to drop extremes and average the rest.
     * @param window List of recent values for one axis.
     * @return The filtered (smoothed) value.
     */
    private double computeSmoothValue(List<Double> window) {
        int size = window.size();
        if (size == 0) {
            return 0.0;  // no data to smooth
        }
        // Copy and sort the values in this window
        List<Double> sorted = new ArrayList<>(window);
        Collections.sort(sorted);

        // Determine how many samples to trim from each end (based on trimPercent)
        int trimCount = (size * trimPercent) / 100;  // integer division (floor)
        if (trimCount < 1 && size > 2) {
            // Ensure at least one value to trim from each end if possible (when enough data)
            trimCount = 1;
        }

        // Determine indices for averaging
        int startIndex = trimCount;           // start after the lowest 'trimCount' values
        int endIndex = size - trimCount;      // end before the highest 'trimCount' values
        if (endIndex <= startIndex) {
            // Not enough data to trim (or trimming would remove all data)
            // Just average all values in this case.
            startIndex = 0;
            endIndex = size;
        }

        // Sum up values from startIndex (inclusive) to endIndex (exclusive)
        double total = 0.0;
        int count = 0;
        for (int i = startIndex; i < endIndex; i++) {
            total += sorted.get(i);
            count++;
        }
        // In normal cases, count should be (size - 2*trimCount). Handle scenario count=0 just in case.
        return (count > 0) ? (total / count) : sorted.get(size / 2);
        // If count is 0 (which would only happen if size was 0 or 1), return middle element as fallback.
    }
}
