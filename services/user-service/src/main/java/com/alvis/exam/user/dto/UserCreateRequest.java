package com.alvis.exam.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class UserCreateRequest {
    private Integer id;
    @NotBlank(message = "userName is required")
    private String userName;
    @NotBlank(message = "password is required")
    private String password;
    @NotBlank(message = "realName is required")
    private String realName;
    private Integer age;
    private Integer sex;
    private Date birthDay;
    private String phone;
    private Integer role;
    private Integer status;
    private Integer userLevel;
    private String imagePath;
}
