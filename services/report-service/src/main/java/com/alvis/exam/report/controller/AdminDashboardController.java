package com.alvis.exam.report.controller;

import com.alvis.exam.common.model.RestResponse;
import com.alvis.exam.report.dto.AdminDashboardResponse;
import com.alvis.exam.report.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    private final ReportService reportService;

    public AdminDashboardController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<AdminDashboardResponse> dashboard() {
        return RestResponse.ok(reportService.getAdminDashboard());
    }
}
