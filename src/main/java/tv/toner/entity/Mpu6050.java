package tv.toner.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Mpu6050 {

    /**
     * BIT address of specific accelerometer that will correspond with a joint
     */
    private String bitAddress;

    /**
     * Accelerometer Data
     */
    private int ax, ay, az;

    /**
     * Gyroscope Data
     */
    private int gx, gy, gz;

    /**
     * Calculated Pitch Roll Yaw
     */
    private double pitch, roll, yaw;

    /**
     * Timestamp of when data was collected
     */
    private LocalDateTime timestamp;

    public Mpu6050(String bitAddress, int ax, int ay, int az, int gx, int gy, int gz, LocalDateTime timestamp) {
        this.gz = gz;
        this.bitAddress = bitAddress;
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.gx = gx;
        this.gy = gy;
        this.timestamp = timestamp;
    }

    public Mpu6050(int ay) {
        this.ay = ay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mpu6050 mpu6050 = (Mpu6050) o;
        return Objects.equals(timestamp, mpu6050.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitAddress, timestamp);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("bitAddress", bitAddress)
                .append("ax", ax)
                .append("ay", ay)
                .append("az", az)
                .append("gx", gx)
                .append("gy", gy)
                .append("gz", gz)
                .append("timestamp", timestamp)
                .toString();
    }
}
