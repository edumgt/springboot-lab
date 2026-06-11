package com.alvis.exam.subject.service;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.subject.dto.SubjectEditRequest;
import com.alvis.exam.subject.dto.SubjectResponse;

import java.util.List;

public interface SubjectService {
    RestPage<SubjectResponse> page(Integer id, Integer level, Integer pageIndex, Integer pageSize);
    List<SubjectResponse> findAll();
    SubjectResponse getById(Integer id);
    Integer level(Integer id);
    SubjectResponse create(SubjectEditRequest request);
    SubjectResponse update(Integer id, SubjectEditRequest request);
}
