package com.alvis.exam.runtime.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradingCompleteEvent {
    private Integer examPaperAnswerId;
    private Integer userId;
    private Integer score;
}
