package ru.blogic.uzedometric.job;

import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.blogic.uzedometric.data.Project;
import ru.blogic.uzedometric.service.MetricService;
import ru.blogic.uzedometric.storage.LastFileLineInfo;
import ru.blogic.uzedometric.storage.LogFileReaderStorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

@DisallowConcurrentExecution
@Component
public class LogFileReaderJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(LogFileReaderJob.class);

    @Value("${metrics.logs.directory}")
    private String commonLogsDirectory;
    @Autowired
    private MetricService metricService;
    @Autowired
    private LogFileReaderStorage logFileReaderStorage;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Project project = Project.valueOf(dataMap.getString("project"));
        String logsDirectory = commonLogsDirectory + project.getFolder();
        File logsFolder = new File(logsDirectory);
        if (!logsFolder.exists()) {
            logger.error("Указанная папка не существует:{}", logsDirectory);
            return;
        }
        File[] files = logsFolder.listFiles((dir, name) -> name.startsWith("uzedo."));
        if (files == null || files.length == 0) {
            return;
        }
        LastFileLineInfo lastFileLineInfo = logFileReaderStorage.get(project);
        List<File> logFiles = getUnreadFiles(files, lastFileLineInfo);
        for (File logFile : logFiles) {
            String logFileName = logFile.getName();
            if (lastFileLineInfo == null || !lastFileLineInfo.getFile().equals(logFileName)) {
                lastFileLineInfo = new LastFileLineInfo(logFileName, null);
            }
            Long filePointer = lastFileLineInfo.getFilePointer();
            filePointer = processLogFile(logFile, project, filePointer);
            lastFileLineInfo.setFilePointer(filePointer);
            logFileReaderStorage.put(project, lastFileLineInfo);
        }
    }

    private List<File> getUnreadFiles(File[] files, LastFileLineInfo lastFileLineInfo) {
        List<File> sortedFiles = Arrays.stream(files).sorted(new FileComparator()).collect(toCollection(ArrayList::new));
        if (lastFileLineInfo == null) {
            return sortedFiles;
        }
        int index = IntStream.range(0, sortedFiles.size())
                .filter(i -> sortedFiles.get(i).getName().equals(lastFileLineInfo.getFile()))
                .findFirst()
                .orElse(-1);
        sortedFiles.subList(0, index).clear();
        return sortedFiles;
    }

    private Long processLogFile(File logFile, Project project, Long filePointer) {
        try {
            RandomAccessFile file = new RandomAccessFile(logFile, "r");
            long fileLength = logFile.length();
            if (filePointer == null || fileLength < filePointer) {
                file = new RandomAccessFile(logFile, "r");
                filePointer = 0L;
            }
            if (fileLength > filePointer) {
                file.seek(filePointer);
                String line = file.readLine();
                while (line != null) {
                    if (line.contains("PrometheusMetricScrapper")) {
                        LogFileReaderJob.this.metricService.offer(extractMetricFromLogLine(line), project);
                    }
                    line = file.readLine();
                }
                filePointer = file.getFilePointer();
            }
            file.close();
            return filePointer;
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла с логами", e);
            return null;
        }
    }

    public static String extractMetricFromLogLine(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.getString("message");
    }

    private static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            Date o1Date = extractDate(o1.getName());
            Date o2Date = extractDate(o2.getName());
            int dateComparing = o1Date.compareTo(o2Date);
            if (dateComparing != 0) {
                return dateComparing;
            }
            Integer o1umber = extractOrderNumber(o1.getName());
            Integer o2umber = extractOrderNumber(o2.getName());
            return o1umber.compareTo(o2umber);
        }

        private Date extractDate(String input) {
            String dateString = input.split("\\.")[1];
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                return null;
            }
        }

        private Integer extractOrderNumber(String input) {
            String orderNumberString = input.split("\\.")[2];
            return Integer.parseInt(orderNumberString);
        }
    }
}
