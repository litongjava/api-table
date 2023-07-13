package com.litongjava.spring.boot.table.json.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
      Map<String, List<String>> arrayParams = new HashMap<>();

      Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String paramName = parameterNames.nextElement();
        if (paramName.contains("[")) {
          // This is an array parameter
          String arrayName = paramName.substring(0, paramName.indexOf('['));
          if (!arrayParams.containsKey(arrayName)) {
            arrayParams.put(arrayName, new ArrayList<>());
          }
          arrayParams.get(arrayName).add(request.getParameter(paramName));
        } else {
          // This is a regular parameter
          map.put(paramName, request.getParameter(paramName));
        }
      }

      // Convert the lists to arrays and add them to the map
      for (Map.Entry<String, List<String>> entry : arrayParams.entrySet()) {
        map.put(entry.getKey(), entry.getValue().toArray(new String[0]));
      }
    }
    return map;
  }
}
