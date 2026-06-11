package com.alvis.exam.exampaper.service;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.exampaper.dto.*;

import java.util.List;

public interface ExamPaperService {
    RestPage<ExamPaperResponse> page(ExamPaperPageRequest request);
    RestPage<ExamPaperResponse> studentPage(ExamPaperPageRequest request);
    ExamPaperResponse getById(Integer id);
    ExamPaperVM getVm(Integer id);
    ExamPaperResponse create(ExamPaperEditRequest request, Integer userId);
    ExamPaperResponse update(Integer id, ExamPaperEditRequest request);
    Integer count();
    List<Integer> monthly();
}
