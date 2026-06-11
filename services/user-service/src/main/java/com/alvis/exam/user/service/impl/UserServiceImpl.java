package com.alvis.exam.user.service.impl;

import com.alvis.exam.common.exception.BusinessException;
import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.user.domain.User;
import com.alvis.exam.user.repository.UserMapper;
import com.alvis.exam.user.service.UserService;
import com.alvis.exam.user.dto.UserCreateRequest;
import com.alvis.exam.user.dto.UserPageRequest;
import com.alvis.exam.user.dto.UserResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public RestPage<UserResponse> page(UserPageRequest request) {
        PageInfo<User> pageInfo = PageHelper.startPage(request.getPageIndex(), request.getPageSize(), "id desc")
                .doSelectPageInfo(() -> userMapper.page(request));
        return new RestPage<>(pageInfo.getList().stream().map(this::toResponse).toList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    public UserResponse getById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        return toResponse(user);
    }

    @Override
    public UserResponse getByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        return toResponse(user);
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        User user = new User();
        apply(request, user);
        user.setUserUuid(UUID.randomUUID().toString());
        user.setCreateTime(new Date());
        user.setModifyTime(new Date());
        userMapper.insertSelective(user);
        return toResponse(user);
    }

    @Override
    public UserResponse update(Integer id, UserCreateRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        apply(request, user);
        user.setId(id);
        user.setModifyTime(new Date());
        userMapper.updateByPrimaryKeySelective(user);
        return toResponse(userMapper.selectById(id));
    }

    @Override
    public void delete(List<Integer> ids) {
        userMapper.deleteByIds(ids);
    }

    private void apply(UserCreateRequest request, User user) {
        user.setUserName(request.getUserName());
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        user.setRealName(request.getRealName());
        user.setAge(request.getAge());
        user.setSex(request.getSex());
        user.setBirthDay(request.getBirthDay());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setUserLevel(request.getUserLevel());
        user.setImagePath(request.getImagePath());
        user.setLastActiveTime(new Date());
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUserUuid(user.getUserUuid());
        response.setUserName(user.getUserName());
        response.setRealName(user.getRealName());
        response.setAge(user.getAge());
        response.setSex(user.getSex());
        response.setBirthDay(user.getBirthDay());
        response.setPhone(user.getPhone());
        response.setLastActiveTime(user.getLastActiveTime());
        response.setCreateTime(user.getCreateTime());
        response.setModifyTime(user.getModifyTime());
        response.setRole(user.getRole());
        response.setImagePath(user.getImagePath());
        response.setStatus(user.getStatus());
        response.setUserLevel(user.getUserLevel());
        return response;
    }
}
