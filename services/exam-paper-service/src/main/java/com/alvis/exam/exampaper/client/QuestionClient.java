package com.alvis.exam.exampaper.client;

import com.alvis.exam.common.model.RestResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QuestionClient {

    private final WebClient webClient;

    public QuestionClient(WebClient.Builder builder,
            @Value("${services.question.url:http://localhost:8084}") String baseUrl) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(5))
                ))
                .build();
    }

    @CircuitBreaker(name = "question-service", fallbackMethod = "getByIdsFallback")
    public List<QuestionPayload> getByIds(List<Integer> ids) {
        log.debug("Calling question-service getByIds count={}", ids.size());
        RestResponse<List<QuestionPayload>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/questions/by-ids")
                        .queryParam("ids", ids.stream().map(String::valueOf).collect(Collectors.joining(",")))
                        .build())
                .headers(h -> {
                    String token = currentBearerToken();
                    if (token != null) h.set(HttpHeaders.AUTHORIZATION, token);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestResponse<List<QuestionPayload>>>() {})
                .timeout(Duration.ofSeconds(5))
                .block();
        return response == null ? List.of() : response.getResponse();
    }

    @SuppressWarnings("unused")
    private List<QuestionPayload> getByIdsFallback(List<Integer> ids, Exception ex) {
        log.warn("Circuit breaker fallback: question-service getByIds({} ids) — {}", ids.size(), ex.getMessage());
        return List.of();
    }

    private String currentBearerToken() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        } catch (Exception e) {
            return null;
        }
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
