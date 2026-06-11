package com.alvis.exam.report.service.impl;

import com.alvis.exam.common.util.DateTimeUtil;
import com.alvis.exam.report.domain.KeyValue;
import com.alvis.exam.report.dto.AdminDashboardResponse;
import com.alvis.exam.report.dto.StudentDashboardResponse;
import com.alvis.exam.report.repository.DashboardMapper;
import com.alvis.exam.report.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final DashboardMapper dashboardMapper;

    public ReportServiceImpl(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public AdminDashboardResponse getAdminDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();
        response.setUserCount(dashboardMapper.selectUserCount());
        response.setQuestionCount(dashboardMapper.selectQuestionCount());
        response.setExamPaperCount(dashboardMapper.selectExamPaperCount());
        response.setExamAnswerCount(dashboardMapper.selectExamAnswerCount());
        Date start = DateTimeUtil.getMonthStartDay();
        Date end = DateTimeUtil.getMonthEndDay();
        response.setQuestionMonthly(toSeries(dashboardMapper.selectQuestionCountByDate(start, end)));
        response.setExamPaperMonthly(toSeries(dashboardMapper.selectExamPaperCountByDate(start, end)));
        response.setAnswerMonthly(toSeries(dashboardMapper.selectExamAnswerCountByDate(start, end)));
        return response;
    }

    @Override
    public StudentDashboardResponse getStudentDashboard(Integer userId) {
        StudentDashboardResponse response = new StudentDashboardResponse();
        response.setAnswerCount(dashboardMapper.selectStudentAnswerCount(userId));
        response.setCompleteCount(dashboardMapper.selectStudentCompleteCount(userId));
        response.setAverageScore(dashboardMapper.selectStudentAverageScore(userId));
        return response;
    }

    private List<Integer> toSeries(List<KeyValue> values) {
        return DateTimeUtil.MothStartToNowFormat().stream().map(day -> values.stream().filter(v -> v.getName().equals(day)).findFirst().map(KeyValue::getValue).orElse(0)).toList();
    }
}
