package tv.toner.utils;

public class ServoUtil {
    public static int mapRollToServo(double roll) {
        double clamped = Math.max(0, Math.min(90, roll));
        double normalized = clamped / 90.0;
        return (int) Math.round(180 * (1.0 - normalized));
    }
}
