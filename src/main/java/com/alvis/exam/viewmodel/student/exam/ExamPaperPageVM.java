package com.alvis.exam.viewmodel.student.exam;

import com.alvis.exam.base.BasePage;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ExamPaperPageVM extends BasePage {
    @NotNull
    private Integer paperType;
    private Integer subjectId;
}
