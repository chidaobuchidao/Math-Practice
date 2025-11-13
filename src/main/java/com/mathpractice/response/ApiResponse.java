package com.mathpractice.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    /**
     * 响应码
     */
    private final Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应数据(泛型)
     */
    private T data;

    private  ApiResponse(Integer code) {
        this.code = code;
    }

    private ApiResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), message, data);
    }


    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ResponseCode.ERROR.getCode(), message);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message);
    }

    public static <T> ApiResponse<T> unauthorized() {
        return new ApiResponse<>(ResponseCode.UNAUTHORIZED.getCode(), ResponseCode.UNAUTHORIZED.getMessage());
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(ResponseCode.UNAUTHORIZED.getCode(), message);
    }

    public static <T> ApiResponse<T> forbidden() {
        return new ApiResponse<>(ResponseCode.FORBIDDEN.getCode(), ResponseCode.FORBIDDEN.getMessage());
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(ResponseCode.FORBIDDEN.getCode(), message);
    }

    public static <T> ApiResponse<T> notFound() {
        return new ApiResponse<>(ResponseCode.NOT_FOUND.getCode(), ResponseCode.NOT_FOUND.getMessage());
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(ResponseCode.NOT_FOUND.getCode(), message);
    }

    public static <T> ApiResponse<T> validationError() {
        return new ApiResponse<>(ResponseCode.VALIDATION_ERROR.getCode(), ResponseCode.VALIDATION_ERROR.getMessage());
    }

    public static <T> ApiResponse<T> validationError(String message) {
        return new ApiResponse<>(ResponseCode.VALIDATION_ERROR.getCode(), message);
    }

}
