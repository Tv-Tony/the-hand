package tv.toner.utils;

import org.javatuples.Triplet;
import org.javatuples.Tuple;

import tv.toner.dto.Mpu6050;

public class MpuUtils {

    private final static int MIN_VALUE = -1024;
    private final static int MAX_VALUE = 512;

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

        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("Z: " + z);

        return Triplet.with(x, y, z);
    }

    // Implementation of the map function from Arduino
    public static int map(int value, int inMin, int inMax, int outMin, int outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
}
