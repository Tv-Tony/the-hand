package tv.toner.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import tv.toner.entity.Mpu6050;
import tv.toner.listener.GloveEvent;

@Component
@Aspect
public class Mpu6050Aspect {

    private static final Logger log = LogManager.getLogger(Mpu6050Aspect.class);

    private int logCount = 0;

    @After("execution(* org.springframework.context.ApplicationEventPublisher.publishEvent(..)) && args(event)")
    public void logAfterPublishEvent(JoinPoint joinPoint, Object event) {
        if (event instanceof GloveEvent) {
            logCount++;
            GloveEvent gloveEvent = (GloveEvent) event;
            if (logCount % 10 == 0) {
                gloveEvent
                        .getAllSensorData()
                        .values()
                        .forEach(this::logMpu6050);
            }
        }
    }

    // Todo make this better
    private void logMpu6050(Mpu6050 data) {
        log.info("\n" +
                        "\u001B[36m📟 MPU6050 Data Received\u001B[0m\n" +
                        "🔹 Bit Address     : \u001B[33m{}\u001B[0m\n" +
                        "📈 Accelerometer  : ax=\u001B[32m{}\u001B[0m, ay=\u001B[32m{}\u001B[0m, az=\u001B[32m{}\u001B[0m\n" +
                        "🎯 Gyroscope      : gx=\u001B[34m{}\u001B[0m, gy=\u001B[34m{}\u001B[0m, gz=\u001B[34m{}\u001B[0m\n" +
                        "🧭 Orientation    : pitch=\u001B[35m{}\u001B[0m°, roll=\u001B[35m{}\u001B[0m°, yaw=\u001B[35m{}\u001B[0m°\n" +
                        "⏱️ Timestamp      : \u001B[90m{}\u001B[0m",
                data.getBitAddress(),
                data.getAx(), data.getAy(), data.getAz(),
                data.getGx(), data.getGy(), data.getGz(),
                String.format("%.2f", data.getPitch()),
                String.format("%.2f", data.getRoll()),
                String.format("%.2f", data.getYaw()),
                data.getTimestamp()
        );
    }
}
