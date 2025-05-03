package com.alturino.open_music_api.core;

import java.util.Map;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.integration.support.StringStringMapBuilder;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class LoggingInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String requestId = request.getHeader("X-Request-Id");
    if (requestId == null || requestId.isBlank() || requestId.isEmpty()) {
      requestId = UUID.randomUUID().toString();
    }
    Map<String, String> contextMap = new StringStringMapBuilder()
        .put("request_id", requestId)
        .put("request_method", request.getMethod())
        .put("request_host", request.getRemoteHost())
        .put("request_addres", request.getRemoteAddr())
        .put("request_uri", request.getRequestURI())
        .get();
    MDC.setContextMap(contextMap);
    response.addHeader("X-Request-Id", requestId);
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }

}
