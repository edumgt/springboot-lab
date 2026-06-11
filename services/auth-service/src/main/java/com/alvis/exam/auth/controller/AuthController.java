package com.alvis.exam.auth.controller;

import com.alvis.exam.auth.dto.LoginRequest;
import com.alvis.exam.auth.dto.LoginResponse;
import com.alvis.exam.auth.dto.RefreshRequest;
import com.alvis.exam.auth.dto.UserPrincipal;
import com.alvis.exam.auth.service.AuthService;
import com.alvis.exam.common.model.RestResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public RestResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return RestResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public RestResponse<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return RestResponse.ok(authService.refreshToken(request.getToken()));
    }

    @GetMapping("/validate")
    public RestResponse<UserPrincipal> validate(@RequestHeader("Authorization") String authorization) {
        return RestResponse.ok(authService.validate(authorization.replace("Bearer ", "")));
    }
}
