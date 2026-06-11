package com.alvis.exam.exampaper.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ExamPaper {
    private Integer id;
    private String name;
    private Integer questionCount;
    private Integer score;
    private Date createTime;
    private Integer createUser;
    private Integer subjectId;
    private Integer paperType;
    private Integer frameTextContentId;
    private Integer suggestTime;
    private Date limitStartTime;
    private Date limitEndTime;
    private Integer gradeLevel;
}
