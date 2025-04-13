package tv.toner.listener;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class ProcessedAngleEvent extends ApplicationEvent {

    private final Map<String, Double> processedAngleData;

    public ProcessedAngleEvent(Object source, Map<String, Double> processedAngleData) {
        super(source);
        this.processedAngleData = processedAngleData;
    }

    public Double getProcessedAngleWithKey(String key) {
        return processedAngleData.get(key);
    }
}
