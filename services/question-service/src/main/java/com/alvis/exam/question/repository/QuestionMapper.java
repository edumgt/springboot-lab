package com.alvis.exam.question.repository;

import com.alvis.exam.question.domain.KeyValue;
import com.alvis.exam.question.domain.Question;
import com.alvis.exam.question.dto.QuestionPageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface QuestionMapper {
    List<Question> page(QuestionPageRequest request);
    Question selectById(@Param("id") Integer id);
    List<Question> selectByIds(@Param("ids") List<Integer> ids);
    int insertSelective(Question question);
    int updateByPrimaryKeySelective(Question question);
    Integer selectAllCount();
    List<KeyValue> selectCountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
