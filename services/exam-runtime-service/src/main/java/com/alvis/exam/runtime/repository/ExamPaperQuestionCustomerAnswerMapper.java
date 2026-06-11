package com.alvis.exam.runtime.repository;

import com.alvis.exam.runtime.domain.ExamPaperQuestionCustomerAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamPaperQuestionCustomerAnswerMapper {
    int insertList(@Param("list") List<ExamPaperQuestionCustomerAnswer> list);
    List<ExamPaperQuestionCustomerAnswer> selectListByPaperAnswerId(@Param("id") Integer id);
}
