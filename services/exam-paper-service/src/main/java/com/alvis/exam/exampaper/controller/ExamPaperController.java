package com.alvis.exam.exampaper.controller;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.common.model.RestResponse;
import com.alvis.exam.exampaper.dto.*;
import com.alvis.exam.exampaper.service.ExamPaperService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-papers")
public class ExamPaperController {
    private final ExamPaperService examPaperService;

    public ExamPaperController(ExamPaperService examPaperService) {
        this.examPaperService = examPaperService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<RestPage<ExamPaperResponse>> page(@RequestBody ExamPaperPageRequest request) {
        return RestResponse.ok(examPaperService.page(request));
    }

    @PostMapping("/student-page")
    public RestResponse<RestPage<ExamPaperResponse>> studentPage(@RequestBody ExamPaperPageRequest request) {
        return RestResponse.ok(examPaperService.studentPage(request));
    }

    @GetMapping("/{id}")
    public RestResponse<ExamPaperResponse> getById(@PathVariable Integer id) {
        return RestResponse.ok(examPaperService.getById(id));
    }

    @GetMapping("/{id}/vm")
    public RestResponse<ExamPaperVM> getVm(@PathVariable Integer id) {
        return RestResponse.ok(examPaperService.getVm(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<ExamPaperResponse> create(@Valid @RequestBody ExamPaperEditRequest request, HttpServletRequest servletRequest) {
        Integer userId = (Integer) servletRequest.getAttribute("jwtUserId");
        return RestResponse.ok(examPaperService.create(request, userId == null ? 0 : userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<ExamPaperResponse> update(@PathVariable Integer id, @Valid @RequestBody ExamPaperEditRequest request) {
        return RestResponse.ok(examPaperService.update(id, request));
    }

    @GetMapping("/dashboard/count")
    public RestResponse<Integer> count() {
        return RestResponse.ok(examPaperService.count());
    }

    @GetMapping("/dashboard/monthly")
    public RestResponse<List<Integer>> monthly() {
        return RestResponse.ok(examPaperService.monthly());
    }
}
