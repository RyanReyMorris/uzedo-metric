package ru.blogic.uzedometric.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.blogic.uzedometric.data.Project;
import ru.blogic.uzedometric.holder.MetricHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
public class MetricConfig {

    @Autowired
    private List<MetricHolder> holders;

    @Bean
    public Map<Project, MetricHolder> metricHolders() {
        Map<Project, MetricHolder> metricHolderMap = new HashMap<>();
        for (MetricHolder metricHolder : holders) {
            metricHolderMap.put(metricHolder.getProject(), metricHolder);
        }
        return metricHolderMap;
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }
}
