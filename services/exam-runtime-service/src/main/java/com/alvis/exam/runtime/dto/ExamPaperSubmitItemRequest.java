package com.alvis.exam.runtime.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExamPaperSubmitItemRequest {
    private Integer questionId;
    private String content;
    private List<String> contentArray = new ArrayList<>();
}
