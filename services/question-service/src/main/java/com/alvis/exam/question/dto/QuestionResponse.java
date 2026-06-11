package com.alvis.exam.question.dto;

import com.alvis.exam.question.domain.QuestionItemObject;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QuestionResponse {
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
    private String title;
    private String analyze;
    private String shortTitle;
    private List<String> correctArray;
    private List<QuestionItemObject> items;
}
