package com.alvis.exam.runtime.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExamPaperSubmitRequest {
    @NotNull(message = "examPaperId is required")
    private Integer examPaperId;
    private Integer doTime;
    private List<ExamPaperSubmitItemRequest> answerItems = new ArrayList<>();
}
