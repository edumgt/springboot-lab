package com.alvis.exam.question.repository;

import com.alvis.exam.question.domain.TextContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TextContentMapper {
    TextContent selectById(@Param("id") Integer id);
    int insertSelective(TextContent textContent);
    int updateByPrimaryKeySelective(TextContent textContent);
}
