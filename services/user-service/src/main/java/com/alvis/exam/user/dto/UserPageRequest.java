package com.alvis.exam.user.dto;

import lombok.Data;

@Data
public class UserPageRequest {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private String userName;
    private Integer role;
}
