package com.alhakim.issuetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private String status;
    private String message;
    private Map<String, String> errors;
    private T data;

    public static <T> BaseResponse<T> success(String message, T data) {
        return BaseResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> success(String message) {
        return BaseResponse.<T>builder()
                .status("success")
                .message(message)
                .build();
    }

    public static <T> BaseResponse<T> fail(String message) {
        return BaseResponse.<T>builder()
                .status("fail")
                .message(message)
                .build();
    }

    public static <T> BaseResponse<T> fail(String message, Map<String, String> errors) {
        return BaseResponse.<T>builder()
                .status("fail")
                .message(message)
                .errors(errors)
                .build();
    }
}
