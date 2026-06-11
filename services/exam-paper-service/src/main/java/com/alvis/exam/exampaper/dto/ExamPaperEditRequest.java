package com.alvis.exam.exampaper.dto;

import com.alvis.exam.exampaper.domain.exam.ExamPaperTitleItemObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ExamPaperEditRequest {
    private Integer id;
    @NotBlank(message = "name is required")
    private String name;
    @NotNull(message = "subjectId is required")
    private Integer subjectId;
    @NotNull(message = "paperType is required")
    private Integer paperType;
    private Integer suggestTime;
    private Date limitStartTime;
    private Date limitEndTime;
    private List<ExamPaperTitleItemObject> titleItems = new ArrayList<>();
}
