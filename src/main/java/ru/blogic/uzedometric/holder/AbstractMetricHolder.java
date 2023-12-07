package ru.blogic.uzedometric.holder;

import java.util.Queue;

public abstract class AbstractMetricHolder implements MetricHolder {

    @Override
    public void offer(String metric) {
        getHolder().offer(metric);
    }

    @Override
    public String peek() {
        return getHolder().peek();
    }

    @Override
    public String poll() {
        return getHolder().poll();
    }

    protected abstract Queue<String> getHolder();
}
