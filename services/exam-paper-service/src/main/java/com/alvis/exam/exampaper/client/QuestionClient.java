package com.alvis.exam.exampaper.client;

import com.alvis.exam.common.model.RestResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionClient {
    private final WebClient webClient;

    public QuestionClient(WebClient.Builder builder, @Value("${services.question.url:http://localhost:8084}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public List<QuestionPayload> getByIds(List<Integer> ids) {
        RestResponse<List<QuestionPayload>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/questions/by-ids").queryParam("ids", ids.stream().map(String::valueOf).collect(Collectors.joining(","))).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestResponse<List<QuestionPayload>>>() {})
                .block();
        return response == null ? List.of() : response.getResponse();
    }

    @Data
    public static class QuestionPayload {
        private Integer id;
        private Integer questionType;
        private Integer subjectId;
        private Integer score;
        private Integer difficult;
        private Integer gradeLevel;
        private String correct;
        private String title;
        private String analyze;
        private List<String> correctArray;
        private List<QuestionItemPayload> items;
    }

    @Data
    public static class QuestionItemPayload {
        private String prefix;
        private String content;
        private Integer score;
    }
}
