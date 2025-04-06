package tv.toner.manager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import tv.toner.entity.Mpu6050;

@Component
public class SensorManager {

    private final ConcurrentHashMap<String, Mpu6050> latestData = new ConcurrentHashMap<>();

    public void updateSensorData(String id, Mpu6050 data) {
        latestData.put(id, data);
    }

    public Mpu6050 getLatestData(String id) {
        return latestData.get(id);
    }

    public Collection<Mpu6050> getAllSensorData() {
        return latestData.values();
    }
}
