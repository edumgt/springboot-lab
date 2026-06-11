package com.alvis.exam.runtime.client;

import com.alvis.exam.common.model.RestResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ExamPaperClient {
    private final WebClient webClient;

    public ExamPaperClient(WebClient.Builder builder, @Value("${services.exam-paper.url:http://localhost:8085}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public ExamPaperVm getExamPaperVm(Integer id) {
        RestResponse<ExamPaperVm> response = webClient.get()
                .uri("/api/exam-papers/{id}/vm", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestResponse<ExamPaperVm>>() {})
                .block();
        return response == null ? null : response.getResponse();
    }

    @Data
    public static class ExamPaperVm {
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
        private List<TitleVm> titleItems = new ArrayList<>();
    }

    @Data
    public static class TitleVm {
        private String name;
        private List<QuestionVm> questionItems = new ArrayList<>();
    }

    @Data
    public static class QuestionVm {
        private Integer id;
        private Integer itemOrder;
        private Integer questionType;
        private Integer subjectId;
        private Integer score;
        private String correct;
        private List<String> correctArray;
    }
}
