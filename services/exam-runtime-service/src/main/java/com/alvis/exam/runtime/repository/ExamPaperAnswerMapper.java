package com.alvis.exam.runtime.repository;

import com.alvis.exam.runtime.domain.ExamPaperAnswer;
import com.alvis.exam.runtime.dto.AnswerPageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamPaperAnswerMapper {
    List<ExamPaperAnswer> studentPage(AnswerPageRequest request);
    ExamPaperAnswer selectById(@Param("id") Integer id);
    int insertSelective(ExamPaperAnswer answer);
}
