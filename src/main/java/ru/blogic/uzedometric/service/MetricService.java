package ru.blogic.uzedometric.service;

import ru.blogic.uzedometric.data.Project;

public interface MetricService {

    void offer(String metric, Project project);

    String poll(Project project);

    String peek(Project project);
}
