package tv.toner.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * AngleFilter class:
 * This filter smooths a series of angle readings (in degrees) using the circular mean.
 * It maintains a rolling window of the most recent values to compute the average.
 *
 * The circular mean is computed by converting each angle to its sine and cosine components,
 * averaging these, and converting the average back into an angle. This approach effectively
 * handles the wrap-around issue (e.g., transitioning from 359° to 0°).
 */
public class AngleFilter {

    private final int windowSize;
    private final List<Double> angleWindow;

    /**
     * Constructs an AngleFilter with a specified window size.
     * @param windowSize the number of most recent readings to average; must be at least 1.
     */
    public AngleFilter(int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be at least 1.");
        }
        this.windowSize = windowSize;
        this.angleWindow = new ArrayList<>(windowSize);
    }

    /**
     * Adds a new angle reading and returns the smoothed (filtered) angle.
     * It uses circular averaging to prevent issues with angle wrap-around.
     *
     * @param angle the new angle reading in degrees.
     * @return the filtered and smoothed angle (in degrees).
     */
    public double filterAngle(double angle) {
        // Keep the window size by removing the oldest entry if necessary
        if (angleWindow.size() >= windowSize) {
            angleWindow.remove(0);
        }
        angleWindow.add(angle);

        // Compute sums for sine and cosine components for circular averaging.
        double sumSin = 0.0;
        double sumCos = 0.0;
        for (Double a : angleWindow) {
            // Convert angle to radians for trigonometric calculations.
            double radians = Math.toRadians(a);
            sumSin += Math.sin(radians);
            sumCos += Math.cos(radians);
        }
        // Compute the average using atan2.
        double avgRadians = Math.atan2(sumSin / angleWindow.size(), sumCos / angleWindow.size());
        double avgDegrees = Math.toDegrees(avgRadians);

        // Ensure the angle is in the range [0, 360).
        if (avgDegrees < 0) {
            avgDegrees += 360;
        }
        return avgDegrees;
    }
}

