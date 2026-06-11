package com.alvis.exam.runtime.service;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.runtime.dto.AnswerPageRequest;
import com.alvis.exam.runtime.dto.AnswerResponse;
import com.alvis.exam.runtime.dto.ExamPaperSubmitRequest;

public interface ExamRuntimeService {
    AnswerResponse calculateAndSave(ExamPaperSubmitRequest submitRequest, Integer userId);
    RestPage<AnswerResponse> getAnswerPage(AnswerPageRequest pageRequest);
    AnswerResponse getAnswerDetail(Integer id);
}
