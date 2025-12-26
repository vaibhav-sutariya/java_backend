package com.infynno.javastartup.startup.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String message;
    private T data;
    private Pagination pagination;

    public static <T> ApiResponse<T> success(String message){
            return new ApiResponse<>(message, null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data){
            return new ApiResponse<>(message, data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data, Pagination pagination){
            return new ApiResponse<>(message, data, pagination);
    }
}
