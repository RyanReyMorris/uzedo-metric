package ru.blogic.uzedometric.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.blogic.uzedometric.data.Project;
import ru.blogic.uzedometric.service.MetricService;

@Controller
@RequestMapping("/metrics")
public class ReportController {

    @Autowired
    private MetricService metricService;

    @RequestMapping(value = "/gpn", method = RequestMethod.GET, produces = "text/plain; version=0.0.4; charset=utf-8")
    @ResponseBody
    public ResponseEntity<String> getGpnMetrics() {
        return ResponseEntity.ok(metricService.poll(Project.GPN));
    }

    @RequestMapping(value = "/vtbl", method = RequestMethod.GET, produces = "text/plain; version=0.0.4; charset=utf-8")
    @ResponseBody
    public ResponseEntity<String> getVtblMetrics() {
        return ResponseEntity.ok(metricService.poll(Project.VTBL));
    }

    @RequestMapping(value = "/rn", method = RequestMethod.GET, produces = "text/plain; version=0.0.4; charset=utf-8")
    @ResponseBody
    public ResponseEntity<String> getRnMetrics() {
        return ResponseEntity.ok(metricService.poll(Project.RN));
    }

    @RequestMapping(value = "/vankor", method = RequestMethod.GET, produces = "text/plain; version=0.0.4; charset=utf-8")
    @ResponseBody
    public ResponseEntity<String> getVankorMetrics() {
        return ResponseEntity.ok(metricService.poll(Project.VANKOR));
    }
}
