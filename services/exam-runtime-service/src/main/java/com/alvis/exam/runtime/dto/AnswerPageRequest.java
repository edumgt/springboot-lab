package com.alvis.exam.runtime.dto;

import lombok.Data;

@Data
public class AnswerPageRequest {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Integer subjectId;
    private Integer createUser;
}
