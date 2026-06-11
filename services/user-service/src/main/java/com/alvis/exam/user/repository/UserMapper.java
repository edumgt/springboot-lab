package com.alvis.exam.user.repository;

import com.alvis.exam.user.domain.User;
import com.alvis.exam.user.dto.UserPageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> page(UserPageRequest request);
    User selectById(@Param("id") Integer id);
    User selectByUsername(@Param("username") String username);
    int insertSelective(User user);
    int updateByPrimaryKeySelective(User user);
    int deleteByIds(@Param("ids") List<Integer> ids);
}
