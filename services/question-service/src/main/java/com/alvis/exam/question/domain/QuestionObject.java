package com.alvis.exam.question.domain;

import lombok.Data;

import java.util.List;

@Data
public class QuestionObject {
    private String titleContent;
    private String analyze;
    private List<QuestionItemObject> questionItemObjects;
}
