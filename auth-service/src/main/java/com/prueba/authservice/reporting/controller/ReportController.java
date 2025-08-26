package com.prueba.authservice.reporting.controller;

import com.prueba.authservice.reporting.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/sensitive")
    public String sensitive() {
        return service.generateSensitiveReport();
    }

    @GetMapping("/user")
    public String userReport() {
        return service.generateUserReport();
    }

    @GetMapping("/user/ultraSensible")
    public String ultraSensibleReport() { return service.generateUserReportRead(); }
}
