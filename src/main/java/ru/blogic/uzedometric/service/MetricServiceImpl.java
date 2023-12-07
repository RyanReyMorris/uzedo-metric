package ru.blogic.uzedometric.service;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.blogic.uzedometric.data.Project;
import ru.blogic.uzedometric.holder.MetricHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Service
public class MetricServiceImpl implements MetricService {

    private static final Logger logger = LoggerFactory.getLogger(MetricServiceImpl.class);

    @Autowired
    private Map<Project, MetricHolder> metricHolders;

    @Override
    public void offer(String metric, Project project) {
        metricHolders.get(project).offer(metric);
    }

    @Override
    public String poll(Project project) {
        try {
            return doubleUncompressString(metricHolders.get(project).poll());
        } catch (IOException e) {
            logger.error("Не удалось распарсить метрики прометеуса", e);
            return null;
        }
    }

    @Override
    public String peek(Project project) {
        try {
            return doubleUncompressString(metricHolders.get(project).peek());
        } catch (IOException e) {
            logger.error("Не удалось распарсить метрики прометеуса", e);
            return null;
        }
    }

    /**
     * Вначале переводим base64 в массив байт, затем разархивируем данные. Повторяем операцию дважды
     */
    private static String doubleUncompressString(String zippedBase64Str) throws IOException {
        if (zippedBase64Str == null) {
            return null;
        }
        String uncompressedString = zippedBase64Str;
        for (int i = 0; i < 2; i++) {
            byte[] bytes = Base64.decodeBase64(uncompressedString);
            try (final GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
                uncompressedString = new String(IOUtils.toByteArray(inputStream), StandardCharsets.UTF_8);
            }
        }
        return uncompressedString;
    }
}
