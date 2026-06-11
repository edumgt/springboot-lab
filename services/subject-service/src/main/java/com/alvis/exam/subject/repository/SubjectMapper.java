package com.alvis.exam.subject.repository;

import com.alvis.exam.subject.domain.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubjectMapper {
    List<Subject> page(@Param("id") Integer id, @Param("level") Integer level);
    List<Subject> findAll();
    Subject selectById(@Param("id") Integer id);
    int insertSelective(Subject subject);
    int updateByPrimaryKeySelective(Subject subject);
}
