package com.alvis.exam.question.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Question {
    private Integer id;
    private Integer questionType;
    private Date createTime;
    private Integer subjectId;
    private Integer createUser;
    private Integer score;
    private Integer status;
    private String correct;
    private Integer difficult;
    private Integer infoTextContentId;
    private Integer gradeLevel;
}
