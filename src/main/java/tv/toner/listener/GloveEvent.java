package tv.toner.listener;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import tv.toner.entity.Mpu6050;

@Getter
public class GloveEvent extends ApplicationEvent {

    private final Mpu6050 data;

    public GloveEvent(Object source, Mpu6050 data) {
        super(source);
        this.data = data;
    }
}
