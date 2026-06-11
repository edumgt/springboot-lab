package com.alvis.exam.question.dto;

import lombok.Data;

@Data
public class QuestionPageRequest {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Integer id;
    private Integer subjectId;
    private Integer questionType;
}
