package com.alvis.exam.question.dto;

import com.alvis.exam.question.domain.QuestionItemObject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionEditRequest {
    private Integer id;
    @NotNull(message = "questionType is required")
    private Integer questionType;
    @NotNull(message = "subjectId is required")
    private Integer subjectId;
    @NotNull(message = "score is required")
    private Integer score;
    private Integer difficult;
    private String title;
    private String analyze;
    private String correct;
    private List<String> correctArray = new ArrayList<>();
    private List<QuestionItemObject> items = new ArrayList<>();
}
