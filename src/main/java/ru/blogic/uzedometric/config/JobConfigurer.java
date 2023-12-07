package ru.blogic.uzedometric.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.blogic.uzedometric.data.Project;
import ru.blogic.uzedometric.job.LogFileReaderJob;

@Component
public class JobConfigurer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(JobConfigurer.class);

    @Value("${metrics.logs.read.cron}")
    private String metricsReadCron;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (Project project : Project.values()) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("project", project.name());
            createJob(project.name(), jobDataMap, LogFileReaderJob.class, metricsReadCron);
        }
    }

    public void createJob(String jobName, JobDataMap jobDataMap, Class<? extends Job> jobClass, String cronExpression) {
        try {
            JobDetail jobDetail = JobBuilder
                    .newJob(jobClass)
                    .withIdentity(jobName)
                    .usingJobData(jobDataMap)
                    .build();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();
            scheduler.scheduleJob(jobDetail, cronTrigger);
            logger.info("Джоб {} успешно запущен", jobName);
        } catch (SchedulerException e) {
            logger.error("Произошла ошибка при регистрации джоба {}", jobName);
        }
    }
}
