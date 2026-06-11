package com.alvis.exam.report.controller;

import com.alvis.exam.common.model.RestResponse;
import com.alvis.exam.report.dto.StudentDashboardResponse;
import com.alvis.exam.report.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/dashboard")
public class StudentDashboardController {
    private final ReportService reportService;

    public StudentDashboardController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public RestResponse<StudentDashboardResponse> dashboard(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("jwtUserId");
        return RestResponse.ok(reportService.getStudentDashboard(userId == null ? 0 : userId));
    }
}
