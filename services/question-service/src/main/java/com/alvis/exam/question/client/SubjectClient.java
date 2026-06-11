package com.alvis.exam.question.client;

import com.alvis.exam.common.model.RestResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.time.Duration;

@Slf4j
@Component
public class SubjectClient {

    private final WebClient webClient;

    public SubjectClient(WebClient.Builder builder,
            @Value("${services.subject.url:http://localhost:8083}") String baseUrl) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(5))
                ))
                .build();
    }

    @CircuitBreaker(name = "subject-service", fallbackMethod = "getLevelFallback")
    public Integer getLevel(Integer subjectId) {
        log.debug("Calling subject-service getLevel for subjectId={}", subjectId);
        RestResponse<Integer> response = webClient.get()
                .uri("/api/subjects/{id}/level", subjectId)
                .headers(h -> {
                    String token = currentBearerToken();
                    if (token != null) h.set(HttpHeaders.AUTHORIZATION, token);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestResponse<Integer>>() {})
                .timeout(Duration.ofSeconds(5))
                .block();
        return response == null ? null : response.getResponse();
    }

    @SuppressWarnings("unused")
    private Integer getLevelFallback(Integer subjectId, Exception ex) {
        log.warn("Circuit breaker fallback: subject-service getLevel({}) — {}", subjectId, ex.getMessage());
        return null;
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
}
