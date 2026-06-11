package com.alvis.exam.auth.service.impl;

import com.alvis.exam.auth.domain.User;
import com.alvis.exam.auth.domain.enums.RoleEnum;
import com.alvis.exam.auth.dto.LoginRequest;
import com.alvis.exam.auth.dto.LoginResponse;
import com.alvis.exam.auth.dto.UserPrincipal;
import com.alvis.exam.auth.repository.UserAuthMapper;
import com.alvis.exam.auth.service.AuthService;
import com.alvis.exam.auth.util.AuthUtil;
import com.alvis.exam.common.exception.BusinessException;
import com.alvis.exam.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserAuthMapper userAuthMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserAuthMapper userAuthMapper, JwtTokenProvider jwtTokenProvider) {
        this.userAuthMapper = userAuthMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String password = AuthUtil.md5Encode(request.getPassword());
        User user = userAuthMapper.findByUsernamePwd(request.getUsername(), password);
        if (user == null) {
            throw new BusinessException("Invalid username or password");
        }
        return buildResponse(user);
    }

    @Override
    public LoginResponse refreshToken(String token) {
        UserPrincipal principal = validate(token);
        User user = userAuthMapper.findByUsername(principal.getUsername());
        if (user == null) {
            throw new BusinessException("User not found");
        }
        return buildResponse(user);
    }

    @Override
    public UserPrincipal validate(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(401, "Invalid token");
        }
        return new UserPrincipal(
                jwtTokenProvider.getUserIdFromToken(token),
                jwtTokenProvider.getUsernameFromToken(token),
                jwtTokenProvider.getRoleFromToken(token)
        );
    }

    private LoginResponse buildResponse(User user) {
        String role = RoleEnum.fromCode(user.getRole()).getName();
        String token = jwtTokenProvider.generateToken(user.getUserName(), role, user.getId());
        return new LoginResponse(token, user.getUserName(), role, user.getId());
    }
}
