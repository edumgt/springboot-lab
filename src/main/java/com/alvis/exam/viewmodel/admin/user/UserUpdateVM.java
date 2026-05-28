package com.alvis.exam.viewmodel.admin.user;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserUpdateVM {

    @NotBlank
    private String realName;

    @NotBlank
    private String phone;

}
