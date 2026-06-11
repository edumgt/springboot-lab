package com.alvis.exam.exampaper.client;

import com.alvis.exam.common.model.RestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SubjectClient {
    private final WebClient webClient;

    public SubjectClient(WebClient.Builder builder, @Value("${services.subject.url:http://localhost:8083}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Integer getLevel(Integer subjectId) {
        RestResponse<Integer> response = webClient.get()
                .uri("/api/subjects/{id}/level", subjectId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestResponse<Integer>>() {})
                .block();
        return response == null ? null : response.getResponse();
    }
}
