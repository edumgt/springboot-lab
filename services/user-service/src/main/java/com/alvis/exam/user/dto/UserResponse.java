package com.alvis.exam.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserResponse {
    private Integer id;
    private String userUuid;
    private String userName;
    private String realName;
    private Integer age;
    private Integer sex;
    private Date birthDay;
    private String phone;
    private Date lastActiveTime;
    private Date createTime;
    private Date modifyTime;
    private Integer role;
    private String imagePath;
    private Integer status;
    private Integer userLevel;
}
