package com.alvis.exam.user.controller;

import com.alvis.exam.common.model.RestPage;
import com.alvis.exam.common.model.RestResponse;
import com.alvis.exam.user.dto.UserCreateRequest;
import com.alvis.exam.user.dto.UserPageRequest;
import com.alvis.exam.user.dto.UserResponse;
import com.alvis.exam.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<RestPage<UserResponse>> page(UserPageRequest request) {
        return RestResponse.ok(userService.page(request));
    }

    @GetMapping("/{id}")
    public RestResponse<UserResponse> getById(@PathVariable Integer id) {
        return RestResponse.ok(userService.getById(id));
    }

    @GetMapping("/by-username/{username}")
    public RestResponse<UserResponse> getByUsername(@PathVariable String username) {
        return RestResponse.ok(userService.getByUsername(username));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return RestResponse.ok(userService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<UserResponse> update(@PathVariable Integer id, @Valid @RequestBody UserCreateRequest request) {
        return RestResponse.ok(userService.update(id, request));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse<Void> delete(@PathVariable String ids) {
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
        userService.delete(idList);
        return RestResponse.ok();
    }
}
