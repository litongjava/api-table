package com.litongjava.spring.boot.table.json.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestParamUtils {
  @SuppressWarnings("unchecked")
  public static Map<String, Object> getRequestMap(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    String contentType = request.getContentType();

    if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
      // JSON handling
      try {
        map = new ObjectMapper().readValue(request.getInputStream(), Map.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      // Form data handling
      Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String paramName = parameterNames.nextElement();
        map.put(paramName, request.getParameter(paramName));
      }
    }
    return map;
  }
}
