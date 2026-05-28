package com.alvis.exam.viewmodel.student.education;

import com.alvis.exam.viewmodel.BaseVM;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SubjectEditRequestVM extends BaseVM {

    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    private Integer level;

    @NotBlank
    private String levelName;

}
