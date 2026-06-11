package com.alvis.exam.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {
    private int code;
    private String message;
    private T response;

    public static <T> RestResponse<T> ok(T response) {
        return new RestResponse<>(1, "success", response);
    }

    public static RestResponse<Void> ok() {
        return new RestResponse<>(1, "success", null);
    }

    public static <T> RestResponse<T> fail(int code, String message) {
        return new RestResponse<>(code, message, null);
    }
}
