package tv.toner.utils;

import tv.toner.entity.Mpu6050;

public class EMAFilter {
    // Method to apply the EMA filter to a stream of sensor readings
    public static Mpu6050 applyFilter(Mpu6050 mpu6050, double alpha, Mpu6050 lastFilteredData) {
        if (lastFilteredData == null)
            return mpu6050;
        // Filter the accelerometer data (ax, ay, az)
        int filteredAx = (int) applyEMA(mpu6050.getAx(), alpha, lastFilteredData.getAx());
        int filteredAy = (int) applyEMA(mpu6050.getAy(), alpha, lastFilteredData.getAy());
        int filteredAz = (int) applyEMA(mpu6050.getAz(), alpha, lastFilteredData.getAz());

        // Filter the gyroscope data (gx, gy, gz)
        int filteredGx = (int) applyEMA(mpu6050.getGx(), alpha, lastFilteredData.getGx());
        int filteredGy = (int) applyEMA(mpu6050.getGy(), alpha, lastFilteredData.getGy());
        int filteredGz = (int) applyEMA(mpu6050.getGz(), alpha, lastFilteredData.getGz());

        // Create a new Mpu6050 object with filtered values
        return new Mpu6050(
                mpu6050.getBitAddress(),
                filteredAx, filteredAy, filteredAz,
                filteredGx, filteredGy, filteredGz,
                mpu6050.getTimestamp()
        );
    }

    // Helper method to apply the EMA filter for a single value
    private static double applyEMA(double newValue, double alpha, double lastEMA) {
        return alpha * newValue + (1 - alpha) * lastEMA;
    }
}
