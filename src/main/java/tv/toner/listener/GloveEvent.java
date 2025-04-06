package tv.toner.listener;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import tv.toner.entity.Mpu6050;

@Getter
public class GloveEvent extends ApplicationEvent {

    private final Map<String, Mpu6050> allSensorData;

    public GloveEvent(Object source, Map<String, Mpu6050> allSensorData) {
        super(source);
        this.allSensorData = allSensorData;
    }

    public Map<String, Mpu6050> getAllSensorData() {
        return allSensorData;
    }
}
