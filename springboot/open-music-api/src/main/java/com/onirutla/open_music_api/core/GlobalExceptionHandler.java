package com.onirutla.open_music_api.core;

import com.onirutla.open_music_api.core.exception.BadRequestException;
import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> fieldError = ex.getFieldErrors()
                .stream()
                .map(error -> String.format("field=%s, message=%s", error.getField(), error.getDefaultMessage()))
                .toList();
        String errorMessage = String.join(" ", fieldError);
        log.atError()
                .setMessage(ex.getMessage())
                .addKeyValue("exception", ex.getClass().getSimpleName())
                .log();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", errorMessage)
                .get();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.atError()
                .setMessage(ex.getMessage())
                .addKeyValue("exception", ex.getClass().getSimpleName())
                .log();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", ex.getLocalizedMessage())
                .get();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElementException(NoSuchElementException ex) {
        log.atError()
                .setMessage(ex.getMessage())
                .addKeyValue("exception", ex.getClass().getSimpleName())
                .log();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", ex.getLocalizedMessage())
                .get();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.atError()
                .setMessage(ex.getMessage())
                .addKeyValue("exception", ex.getClass().getSimpleName())
                .log();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", ex.getLocalizedMessage())
                .get();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        log.atError()
                .setMessage(ex.getMessage())
                .addKeyValue("exception", ex.getClass().getSimpleName())
                .log();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", ex.getLocalizedMessage())
                .get();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(BadRequestException ex) {
        log.atError()
                .setMessage(ex.getMessage())
                .addKeyValue("exception", ex.getClass().getSimpleName())
                .log();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", ex.getMessage())
                .get();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException e) {
        log.atError()
                .setCause(e)
                .addKeyValue("exception", e.getClass().getSimpleName())
                .log(e.getMessage());
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", e.getMessage())
                .get();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedRequestException(UnauthorizedRequestException e) {
        log.atError()
                .setCause(e)
                .addKeyValue("exception", e.getClass().getSimpleName())
                .log(e.getMessage());
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", e.getMessage())
                .get();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException e) {
        log.atError()
                .setCause(e)
                .addKeyValue("exception", e.getClass().getSimpleName())
                .log(e.getMessage());
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "fail")
                .put("message", e.getMessage())
                .get();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}
