package com.alvis.exam.runtime.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class AnswerResponse {
    private Integer id;
    private Integer examPaperId;
    private Integer createUser;
    private Date createTime;
    private Integer userScore;
    private Integer subjectId;
    private Integer questionCount;
    private Integer questionCorrect;
    private Integer paperScore;
    private Integer doTime;
    private Integer paperType;
    private Integer systemScore;
    private Integer status;
    private String paperName;
    private List<ExamPaperSubmitItemRequest> answerItems = new ArrayList<>();
}
