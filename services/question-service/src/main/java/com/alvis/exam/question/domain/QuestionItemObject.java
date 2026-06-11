package com.alvis.exam.question.domain;

import lombok.Data;

@Data
public class QuestionItemObject {
    private String prefix;
    private String content;
    private Integer score;
}
