package com.alvis.exam.runtime.client;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ExamPaperClient {

    private final WebClient webClient;

    public ExamPaperClient(WebClient.Builder builder,
            @Value("${services.exam-paper.url:http://localhost:8085}") String baseUrl) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(5))
                ))
                .build();
    }

    @CircuitBreaker(name = "exam-paper-service", fallbackMethod = "getExamPaperVmFallback")
    public ExamPaperVm getExamPaperVm(Integer id) {
        log.debug("Calling exam-paper-service getExamPaperVm for id={}", id);
        RestResponse<ExamPaperVm> response = webClient.get()
                .uri("/api/exam-papers/{id}/vm", id)
                .headers(h -> {
                    String token = currentBearerToken();
                    if (token != null) h.set(HttpHeaders.AUTHORIZATION, token);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestResponse<ExamPaperVm>>() {})
                .timeout(Duration.ofSeconds(5))
                .block();
        return response == null ? null : response.getResponse();
    }

    @SuppressWarnings("unused")
    private ExamPaperVm getExamPaperVmFallback(Integer id, Exception ex) {
        log.warn("Circuit breaker fallback: exam-paper-service getExamPaperVm({}) — {}", id, ex.getMessage());
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
