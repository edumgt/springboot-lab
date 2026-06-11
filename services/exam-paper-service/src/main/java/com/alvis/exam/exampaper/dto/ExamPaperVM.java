package com.alvis.exam.exampaper.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ExamPaperVM {
    private Integer id;
    private String name;
    private Integer questionCount;
    private Integer score;
    private Integer subjectId;
    private Integer paperType;
    private Integer suggestTime;
    private Date limitStartTime;
    private Date limitEndTime;
    private Integer gradeLevel;
    private List<TitleVM> titleItems = new ArrayList<>();

    @Data
    public static class TitleVM {
        private String name;
        private List<QuestionVM> questionItems = new ArrayList<>();
    }

    @Data
    public static class QuestionVM {
        private Integer id;
        private Integer itemOrder;
        private Integer questionType;
        private Integer subjectId;
        private Integer score;
        private Integer difficult;
        private Integer gradeLevel;
        private String correct;
        private String title;
        private String analyze;
        private List<String> correctArray;
        private List<ItemVM> items = new ArrayList<>();
    }

    @Data
    public static class ItemVM {
        private String prefix;
        private String content;
        private Integer score;
    }
}
