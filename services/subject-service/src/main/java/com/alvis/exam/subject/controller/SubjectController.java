package com.alvis.exam.subject.controller;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.common.model.RestResponse;
import com.alvis.exam.subject.dto.SubjectEditRequest;
import com.alvis.exam.subject.dto.SubjectResponse;
import com.alvis.exam.subject.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public RestResponse<?> list(@RequestParam(required = false) Integer id,
                                @RequestParam(required = false) Integer level,
                                @RequestParam(required = false) Integer pageIndex,
                                @RequestParam(required = false) Integer pageSize) {
        if (pageIndex == null || pageSize == null) {
            List<SubjectResponse> subjects = subjectService.findAll();
            return RestResponse.ok(subjects);
        }
        RestPage<SubjectResponse> page = subjectService.page(id, level, pageIndex, pageSize);
        return RestResponse.ok(page);
    }

    @GetMapping("/{id}")
    public RestResponse<SubjectResponse> getById(@PathVariable Integer id) {
        return RestResponse.ok(subjectService.getById(id));
    }

    @GetMapping("/{id}/level")
    public RestResponse<Integer> level(@PathVariable Integer id) {
        return RestResponse.ok(subjectService.level(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<SubjectResponse> create(@Valid @RequestBody SubjectEditRequest request) {
        return RestResponse.ok(subjectService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<SubjectResponse> update(@PathVariable Integer id, @Valid @RequestBody SubjectEditRequest request) {
        return RestResponse.ok(subjectService.update(id, request));
    }
}
