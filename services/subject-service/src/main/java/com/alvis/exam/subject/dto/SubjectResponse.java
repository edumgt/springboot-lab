package com.alvis.exam.subject.dto;

import lombok.Data;

@Data
public class SubjectResponse {
    private Integer id;
    private String name;
    private Integer level;
    private String levelName;
}
