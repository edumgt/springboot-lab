package com.alvis.exam.report.service;

import com.alvis.exam.report.dto.AdminDashboardResponse;
import com.alvis.exam.report.dto.StudentDashboardResponse;

public interface ReportService {
    AdminDashboardResponse getAdminDashboard();
    StudentDashboardResponse getStudentDashboard(Integer userId);
}
