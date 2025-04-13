package tv.toner.utils;

import lombok.Getter;
import tv.toner.entity.Mpu6050;

/**
 * Utility class for processing raw X-axis data from an MPU6050 sensor
 * and converting it to angle in degrees.
 */
@Getter
public class TiltCalculator {

    /**
     * Holds roll and pitch angles in degrees.
     */
    public static class TiltAngles {
        private final double roll;   // Rotation around X-axis
        private final double pitch;  // Rotation around Y-axis

        public TiltAngles(double roll, double pitch) {
            this.roll = roll;
            this.pitch = pitch;
        }

        public double getRoll() {
            return roll;
        }

        public double getPitch() {
            return pitch;
        }

        @Override
        public String toString() {
            return String.format("TiltAngles{roll=%.2f°, pitch=%.2f°}", roll, pitch);
        }
    }

    /**
     * Calculates roll and pitch angles from accelerometer data.
     *
     * @param sensor the Mpu6050 object with ax, ay, az
     * @return TiltAngles in degrees
     */
    public static TiltAngles calculateTiltAngles(Mpu6050 sensor) {
        // Get raw accelerometer values
        double ax = sensor.getAx();
        double ay = sensor.getAy();
        double az = sensor.getAz();

        // Calculate roll using arctangent of ay / az
        double rollRad = Math.atan2(ay, az);

        // Calculate pitch using arctangent of -ax over sqrt(ay^2 + az^2)
        double pitchRad = Math.atan2(-ax, Math.sqrt(ay * ay + az * az));

        // Convert radians to degrees
        double rollDeg = Math.toDegrees(rollRad);
        double pitchDeg = Math.toDegrees(pitchRad);

        return new TiltAngles(rollDeg, pitchDeg);
    }
}

