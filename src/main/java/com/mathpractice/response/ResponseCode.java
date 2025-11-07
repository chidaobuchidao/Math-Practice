package com.mathpractice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    USERNAME_EXISTS(1001, "用户名已存在"),

    // 新增的响应码
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    VALIDATION_ERROR(1002, "参数验证失败"),
    USER_NOT_FOUND(1003, "用户不存在"),
    PASSWORD_ERROR(1004, "密码错误"),

    QUESTION_NOT_ENOUGH(4003, "题目数量不足"),
    PAPER_GENERATE_FAILED(4004, "试卷生成失败");

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;
}
