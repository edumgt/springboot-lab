package com.alvis.exam.user.service;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.user.dto.UserCreateRequest;
import com.alvis.exam.user.dto.UserPageRequest;
import com.alvis.exam.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    RestPage<UserResponse> page(UserPageRequest request);
    UserResponse getById(Integer id);
    UserResponse getByUsername(String username);
    UserResponse create(UserCreateRequest request);
    UserResponse update(Integer id, UserCreateRequest request);
    void delete(List<Integer> ids);
}
