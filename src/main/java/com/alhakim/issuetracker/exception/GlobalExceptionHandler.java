package com.alhakim.issuetracker.exception;

import com.alhakim.issuetracker.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        Map<String, String> errors = Map.of("general", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail("Resource not found", errors));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse<Void>> handleUnauthorizedException(UnauthorizedException e) {
        Map<String, String> errors = Map.of("general", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.fail("Unauthorized access", errors));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateException(DuplicateException e) {
        Map<String, String> errors = Map.of("general", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.fail("Duplicate resource", errors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail("Validation failed", errors));
    }
}
