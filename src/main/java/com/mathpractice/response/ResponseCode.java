package com.mathpractice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    USERNAME_EXISTS(1001, "用户名已存在");

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;
}
