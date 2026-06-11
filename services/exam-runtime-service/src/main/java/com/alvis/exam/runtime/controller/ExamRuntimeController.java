package com.alvis.exam.runtime.controller;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.common.model.RestResponse;
import com.alvis.exam.runtime.dto.AnswerPageRequest;
import com.alvis.exam.runtime.dto.AnswerResponse;
import com.alvis.exam.runtime.dto.ExamPaperSubmitRequest;
import com.alvis.exam.runtime.service.ExamRuntimeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam-runtime")
public class ExamRuntimeController {
    private final ExamRuntimeService examRuntimeService;

    public ExamRuntimeController(ExamRuntimeService examRuntimeService) {
        this.examRuntimeService = examRuntimeService;
    }

    @PostMapping("/answer/submit")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public RestResponse<AnswerResponse> submit(@Valid @RequestBody ExamPaperSubmitRequest request, HttpServletRequest servletRequest) {
        Integer userId = (Integer) servletRequest.getAttribute("jwtUserId");
        return RestResponse.ok(examRuntimeService.calculateAndSave(request, userId == null ? 0 : userId));
    }

    @PostMapping("/answer/page")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public RestResponse<RestPage<AnswerResponse>> page(@RequestBody AnswerPageRequest request, HttpServletRequest servletRequest) {
        Integer userId = (Integer) servletRequest.getAttribute("jwtUserId");
        request.setCreateUser(userId == null ? 0 : userId);
        return RestResponse.ok(examRuntimeService.getAnswerPage(request));
    }

    @GetMapping("/answer/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public RestResponse<AnswerResponse> detail(@PathVariable Integer id) {
        return RestResponse.ok(examRuntimeService.getAnswerDetail(id));
    }
}
