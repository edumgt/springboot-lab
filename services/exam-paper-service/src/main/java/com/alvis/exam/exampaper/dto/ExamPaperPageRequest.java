package com.alvis.exam.exampaper.dto;

import lombok.Data;

@Data
public class ExamPaperPageRequest {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Integer id;
    private Integer subjectId;
    private Integer paperType;
}
