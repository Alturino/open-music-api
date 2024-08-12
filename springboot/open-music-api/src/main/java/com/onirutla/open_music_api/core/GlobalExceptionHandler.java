package com.onirutla.open_music_api.core;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.integration.support.MapBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<Object, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> fieldError = ex.getFieldErrors()
                .stream()
                .map(error -> String.format("field=%s, message=%s", error.getField(), error.getDefaultMessage()))
                .toList();
        String errorMessage = String.join(" ", fieldError);
        Map<Object, Object> errorBody = new MapBuilder<>()
                .put("status", "fail")
                .put("message", errorMessage)
                .get();
        return ResponseEntity.badRequest().body(errorBody);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<Object, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<Object, Object> errorBody = new MapBuilder<>()
                .put("status", "fail")
                .put("message", ex.getLocalizedMessage())
                .get();
        return ResponseEntity.badRequest().body(errorBody);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<Object, Object>> handleNoSuchElementException(NoSuchElementException ex) {
        Map<Object, Object> errorBody = new MapBuilder<>()
                .put("status", "fail")
                .put("message", ex.getLocalizedMessage())
                .get();
        return ResponseEntity.status(404).body(errorBody);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<Object, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<Object, Object> errorBody = new MapBuilder<>()
                .put("status", "fail")
                .put("message", ex.getLocalizedMessage())
                .get();
        return ResponseEntity.status(400).body(errorBody);
    }
}
