package com.litongjava.data.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestParamUtils {
  public static Map<String, Object> getRequestMap(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    String contentType = request.getContentType();

    if (contentType != null && contentType.contains("application/json")) {
      throw new RuntimeException("unspupport: application/json");
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
