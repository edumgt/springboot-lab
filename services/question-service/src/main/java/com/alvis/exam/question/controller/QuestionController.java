package com.alvis.exam.question.controller;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.common.model.RestResponse;
import com.alvis.exam.question.dto.QuestionEditRequest;
import com.alvis.exam.question.dto.QuestionPageRequest;
import com.alvis.exam.question.dto.QuestionResponse;
import com.alvis.exam.question.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<RestPage<QuestionResponse>> page(@RequestBody QuestionPageRequest request) {
        return RestResponse.ok(questionService.page(request));
    }

    @GetMapping("/{id}")
    public RestResponse<QuestionResponse> getById(@PathVariable Integer id) {
        return RestResponse.ok(questionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<QuestionResponse> create(@Valid @RequestBody QuestionEditRequest request, HttpServletRequest servletRequest) {
        Integer userId = (Integer) servletRequest.getAttribute("jwtUserId");
        return RestResponse.ok(questionService.create(request, userId == null ? 0 : userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<QuestionResponse> update(@PathVariable Integer id, @Valid @RequestBody QuestionEditRequest request) {
        return RestResponse.ok(questionService.update(id, request));
    }

    @GetMapping("/by-ids")
    public RestResponse<List<QuestionResponse>> getByIds(@RequestParam String ids) {
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
        return RestResponse.ok(questionService.getByIds(idList));
    }
}
