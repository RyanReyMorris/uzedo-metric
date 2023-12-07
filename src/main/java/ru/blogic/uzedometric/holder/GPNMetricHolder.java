package ru.blogic.uzedometric.holder;

import org.springframework.stereotype.Component;
import ru.blogic.uzedometric.data.Project;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class GPNMetricHolder extends AbstractMetricHolder {

    private static final Queue<String> metricQueue = new LinkedList<>();

    @Override
    protected Queue<String> getHolder() {
        return metricQueue;
    }

    @Override
    public Project getProject() {
        return Project.GPN;
    }
}
