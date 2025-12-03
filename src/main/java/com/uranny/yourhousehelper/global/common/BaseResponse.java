package com.uranny.yourhousehelper.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class BaseResponse<T> {

    private final int status;
    private final String message;
    private final T data;

    private BaseResponse(T data, HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseEntity<BaseResponse<T>> of(T data, HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new BaseResponse<>(data, status, message));
    }

    public static <T> ResponseEntity<BaseResponse<T>> of(HttpStatus status, String message) {
        return of(null, status, message);
    }

    public static <T> ResponseEntity<BaseResponse<T>> of(T data, String message) {
        return of(data, HttpStatus.OK, message);
    }
}