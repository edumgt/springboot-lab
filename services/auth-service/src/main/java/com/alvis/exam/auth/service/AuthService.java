package com.alvis.exam.auth.service;

import com.alvis.exam.auth.dto.LoginRequest;
import com.alvis.exam.auth.dto.LoginResponse;
import com.alvis.exam.auth.dto.UserPrincipal;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(String token);
    UserPrincipal validate(String token);
}
