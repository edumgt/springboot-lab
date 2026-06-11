package com.alvis.exam.exampaper.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextContent {
    private Integer id;
    private String content;
    private Date createTime;

    public TextContent(String content, Date createTime) {
        this.content = content;
        this.createTime = createTime;
    }
}
