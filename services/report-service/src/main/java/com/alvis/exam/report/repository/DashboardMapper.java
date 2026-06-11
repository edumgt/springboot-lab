package com.alvis.exam.report.repository;

import com.alvis.exam.report.domain.KeyValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DashboardMapper {
    Integer selectUserCount();
    Integer selectQuestionCount();
    Integer selectExamPaperCount();
    Integer selectExamAnswerCount();
    List<KeyValue> selectQuestionCountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
    List<KeyValue> selectExamPaperCountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
    List<KeyValue> selectExamAnswerCountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
    Integer selectStudentAnswerCount(@Param("userId") Integer userId);
    Integer selectStudentCompleteCount(@Param("userId") Integer userId);
    Integer selectStudentAverageScore(@Param("userId") Integer userId);
}
