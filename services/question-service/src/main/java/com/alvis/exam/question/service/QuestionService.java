package com.alvis.exam.question.service;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.question.dto.QuestionEditRequest;
import com.alvis.exam.question.dto.QuestionPageRequest;
import com.alvis.exam.question.dto.QuestionResponse;

import java.util.List;

public interface QuestionService {
    RestPage<QuestionResponse> page(QuestionPageRequest request);
    QuestionResponse getById(Integer id);
    QuestionResponse create(QuestionEditRequest request, Integer userId);
    QuestionResponse update(Integer id, QuestionEditRequest request);
    List<QuestionResponse> getByIds(List<Integer> ids);
}
