package tv.toner.utils;

public class ServoUtil {
    public static int mapRollToServo(double roll) {
        if (roll <= 0) {
            return 180;
        } else if (roll >= 90) {
            return 0;
        } else {
            // Map from (0, 90) → (180, 0)
            double normalized = roll / 90.0;
            return (int) Math.round(180 * (1.0 - normalized));
        }
    }
}
