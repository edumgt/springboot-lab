package com.alvis.exam.report.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminDashboardResponse {
    private Integer userCount;
    private Integer questionCount;
    private Integer examPaperCount;
    private Integer examAnswerCount;
    private List<Integer> questionMonthly = new ArrayList<>();
    private List<Integer> examPaperMonthly = new ArrayList<>();
    private List<Integer> answerMonthly = new ArrayList<>();
}
