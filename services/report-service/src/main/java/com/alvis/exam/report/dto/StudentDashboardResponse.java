package com.alvis.exam.report.dto;

import lombok.Data;

@Data
public class StudentDashboardResponse {
    private Integer answerCount;
    private Integer completeCount;
    private Integer averageScore;
}
