package com.alvis.exam.base;

/**
 * @author alvis
 */
public enum SystemCode {
    /**
     * OK
     */
    OK(1, "Success"),
    /**
     * AccessTokenError
     */
    AccessTokenError(400, "用户登录令牌失效"),
    /**
     * UNAUTHORIZED
     */
    UNAUTHORIZED(401, "用户未登录"),
    /**
     * UNAUTHORIZED
     */
    AuthError(402, "用户名或密码错误"),
    /**
     * InnerError
     */
    InnerError(500, "일시적 오류 입니다. (Code: 500)"),
    /**
     * ParameterValidError
     */
    ParameterValidError(501, "参数验证错误");

    int code;
    String message;

    SystemCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
