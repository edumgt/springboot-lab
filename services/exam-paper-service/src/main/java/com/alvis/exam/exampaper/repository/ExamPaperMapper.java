package com.alvis.exam.exampaper.repository;

import com.alvis.exam.exampaper.domain.ExamPaper;
import com.alvis.exam.exampaper.domain.KeyValue;
import com.alvis.exam.exampaper.dto.ExamPaperPageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ExamPaperMapper {
    List<ExamPaper> page(ExamPaperPageRequest request);
    List<ExamPaper> studentPage(ExamPaperPageRequest request);
    ExamPaper selectById(@Param("id") Integer id);
    int insertSelective(ExamPaper examPaper);
    int updateByPrimaryKeySelective(ExamPaper examPaper);
    Integer selectAllCount();
    List<KeyValue> selectCountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
