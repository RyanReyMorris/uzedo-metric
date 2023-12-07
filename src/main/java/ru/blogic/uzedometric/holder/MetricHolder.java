package ru.blogic.uzedometric.holder;

import ru.blogic.uzedometric.data.Project;

public interface MetricHolder {

    void offer(String metric);

    String peek();

    String poll();

    Project getProject();
}
