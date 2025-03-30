package tv.toner.utils;

/**
 * Utility class for processing raw X-axis data from an MPU6050 sensor
 * and converting it to angle in degrees.
 */
public class MPU6050Utility {

    private int rawAtNeg90;
    private int rawAtZero;
    private int rawAtPos90;

    private boolean calibrated = false;

    /**
     * Calibrates the utility using known raw X-axis values at -90°, 0°, and 90°.
     *
     * @param rawAtNeg90 Raw value when the sensor is at -90°.
     * @param rawAtZero Raw value when the sensor is at 0°.
     * @param rawAtPos90 Raw value when the sensor is at 90°.
     */
    public void setCalibrationPoints(int rawAtNeg90, int rawAtZero, int rawAtPos90) {
        this.rawAtNeg90 = rawAtNeg90;
        this.rawAtZero = rawAtZero;
        this.rawAtPos90 = rawAtPos90;
        this.calibrated = true;
    }

    /**
     * Converts a raw X-axis value into an angle in degrees using linear interpolation.
     *
     * @param rawX Raw X-axis accelerometer value.
     * @return Angle in degrees (clamped between -90° and 90°).
     * @throws IllegalStateException If the utility has not been calibrated.
     */
    public double getXAngleFromRaw(int rawX) {
        if (!calibrated) {
            throw new IllegalStateException("MPU6050Utility is not calibrated. Call setCalibrationPoints() first.");
        }

        if (rawX >= rawAtNeg90) {
            return -90.0;
        } else if (rawX <= rawAtPos90) {
            return 90.0;
        } else if (rawX >= rawAtZero) {
            // Interpolate between 0° and -90°
            return interpolate(rawX, rawAtZero, rawAtNeg90, 0.0, -90.0);
        } else {
            // Interpolate between 90° and 0°
            return interpolate(rawX, rawAtPos90, rawAtZero, 90.0, 0.0);
        }
    }

    /**
     * Linearly interpolates a value between two points.
     *
     * @param x The input value to interpolate.
     * @param x0 The lower bound raw value.
     * @param x1 The upper bound raw value.
     * @param y0 The angle at x0.
     * @param y1 The angle at x1.
     * @return The interpolated angle.
     */
    private double interpolate(int x, int x0, int x1, double y0, double y1) {
        double fraction = (double)(x - x0) / (x1 - x0);
        return y0 + fraction * (y1 - y0);
    }
}

