package tv.toner.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;

import tv.toner.TheHand;
import tv.toner.entity.Mpu6050;

public class MpuUtils {

    private static final Logger log = LogManager.getLogger(MpuUtils.class);

    private final static int MIN_VALUE = -1024;
    private final static int MAX_VALUE = 512;

    /**
     * Triplet of angles
     */
    public static Triplet<Double, Double, Double> angles(Mpu6050 mpu) {
        int acX = mpu.getAx();
        int acY = mpu.getAy();
        int acZ = mpu.getAz();

        int xAng = map(acX, MIN_VALUE, MAX_VALUE, -90, 90);
        int yAng = map(acY, MIN_VALUE, MAX_VALUE, -90, 90);
        int zAng = map(acZ, MIN_VALUE, MAX_VALUE, -90, 90);

        double x = Math.toDegrees(Math.atan2(-yAng, -zAng) + Math.PI);
        double y = Math.toDegrees(Math.atan2(-xAng, -zAng) + Math.PI);
        double z = Math.toDegrees(Math.atan2(-yAng, -xAng) + Math.PI);

        logSensorData(mpu, xAng, yAng, zAng, x, y, z);

        return Triplet.with(x, y, z);
    }

    // Implementation of the map function from Arduino
    public static int map(int value, int inMin, int inMax, int outMin, int outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    private static void logSensorData(Mpu6050 mpu, int xAng, int yAng, int zAng, double x, double y, double z) {
        log.info("[MPU6050] Raw: Ax={}, Ay={}, Az={} Gx={}, Gy={}, Gz={} | Mapped: X={}°, Y={}°, Z={}° | Angles: Roll={}, Pitch={}, Yaw={}",
                mpu.getAx(), mpu.getAy(), mpu.getAz(),
                mpu.getGx(), mpu.getGy(), mpu.getGz(),
                xAng, yAng, zAng,
                String.format("%.2f", x), String.format("%.2f", y), String.format("%.2f", z));
    }
}
