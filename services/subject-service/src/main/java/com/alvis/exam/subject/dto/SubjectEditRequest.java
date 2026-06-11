package com.alvis.exam.subject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectEditRequest {
    private Integer id;
    @NotBlank(message = "name is required")
    private String name;
    private Integer level;
    private String levelName;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
}
