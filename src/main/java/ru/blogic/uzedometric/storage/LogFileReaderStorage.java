package ru.blogic.uzedometric.storage;

import org.springframework.stereotype.Component;
import ru.blogic.uzedometric.data.Project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LogFileReaderStorage {

    private static final Map<Project, LastFileLineInfo> logsReaderStorage = new ConcurrentHashMap<>();

    public void put(Project project, LastFileLineInfo lastFileLineInfo) {
        logsReaderStorage.put(project, lastFileLineInfo);
    }

    public LastFileLineInfo get(Project project) {
        return logsReaderStorage.get(project);
    }
}
