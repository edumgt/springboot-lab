package com.alvis.exam.auth.repository;

import com.alvis.exam.auth.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAuthMapper {
    User findByUsername(@Param("username") String username);
    User findByUsernamePwd(@Param("username") String username, @Param("pwd") String pwd);
}
