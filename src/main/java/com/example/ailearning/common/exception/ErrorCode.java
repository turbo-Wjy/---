package com.example.ailearning.common.exception;

public enum ErrorCode {
    PARAM_ERROR(40000, "请求参数错误"),
    UNAUTHORIZED(40100, "未登录或 Token 无效"),
    FORBIDDEN(40300, "无权限"),
    DATA_SCOPE_FORBIDDEN(40310, "无数据范围权限"),
    NOT_FOUND(40400, "数据不存在"),
    CONFLICT(40900, "数据冲突"),
    AI_SERVICE_ERROR(50200, "AI 服务调用失败"),
    SYSTEM_ERROR(50000, "系统异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
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
