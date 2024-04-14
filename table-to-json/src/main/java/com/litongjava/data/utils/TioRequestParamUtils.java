package com.litongjava.data.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jfinal.kit.StrKit;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.utils.json.Json;

public class TioRequestParamUtils {
  public static List<String> types = new ArrayList<>();

  static {
    types.add("int");
    types.add("long");
    types.add("int[]");
    types.add("long[]");
    types.add("string[]");
    types.add("ISO8601");
  }

  public static Map<String, Object> getRequestMap(HttpRequest request) {
    Map<String, Object> map = new HashMap<>();
    // String contentType = request.getHeader(HttpConst.RequestHeaderKey.Content_Type);
    String contentType = request.getContentType();

    Map<String, Object> requestMap = new HashMap<>();
    Map<String, List<Object>> arrayParams = new HashMap<>();
    Map<String, Object> paramType = new HashMap<>();

    if (contentType != null && contentType.contains("application/json")) {
      // throw new RuntimeException("unspupport: application/json");
      String bodyString = request.getBodyString();
      requestMap = Json.getJson().parseToMap(bodyString, String.class, Object.class);
    } else {
      // Form data handling
      Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String paramName = parameterNames.nextElement();
        String paramValue = request.getParameter(paramName);
        requestMap.put(paramName, paramValue);
      }
    }

    Set<Entry<String, Object>> entrySet = requestMap.entrySet();
    for (Entry<String, Object> entry : entrySet) {
      String paramName = entry.getKey();
      Object paramValue = entry.getValue();

      if (paramName.contains("[")) {
        // This is an array paramValue
        String arrayName = paramName.substring(0, paramName.indexOf('['));
        if (!arrayParams.containsKey(arrayName)) {
          arrayParams.put(arrayName, new ArrayList<>());
        }
        arrayParams.get(arrayName).add(paramValue);
      } else if (paramName.endsWith("Type") || paramName.endsWith("type") && types.contains(paramValue)) {
        // 前端传递指定数缺定数据类型
        paramType.put(paramName, paramValue);
      } else {
        // This is a regular paramValue
        map.put(paramName, paramValue);
      }
    }

    // Convert the lists to arrays and add them to the map
    convertValueType(map, arrayParams, paramType);
    return map;
  }

  public static void convertValueType(Map<String, Object> map, Map<String, List<Object>> arrayParams,
      Map<String, Object> paramType) {
    // convert type
    for (Map.Entry<String, List<Object>> entry : arrayParams.entrySet()) {
      map.put(entry.getKey(), entry.getValue().toArray(new String[0]));
    }
    // convert type
    for (Map.Entry<String, Object> entry : paramType.entrySet()) {
      // idType=long
      String typeKey = entry.getKey();
      // 支持id_type and idType
      int lastIndexOf = typeKey.lastIndexOf("Type");
      String paramKey = null;
      if (lastIndexOf != -1) {
        paramKey = typeKey.substring(0, lastIndexOf);
      } else {
        lastIndexOf = typeKey.lastIndexOf("_");
        paramKey = typeKey.substring(0, lastIndexOf);
      }
      Object paramValue = map.get(paramKey);

      if (StrKit.notNull(paramValue)) {
        Object parmTypeValue = entry.getValue();

        if (paramValue instanceof String) {
          String stringValue = (String) paramValue;
          if (StrKit.notBlank(stringValue)) {
            if ("int".equals(parmTypeValue)) {
              map.put(paramKey, Integer.parseInt(stringValue));
            } else if ("long".equals(parmTypeValue)) {
              map.put(paramKey, Long.parseLong(stringValue));
            } else if ("ISO8601".equals(parmTypeValue)) {
              map.put(paramKey, DateParseUtils.parseIso8601Date(stringValue));
            }
          }
        } else if (parmTypeValue instanceof com.alibaba.fastjson2.JSONArray) {
          com.alibaba.fastjson2.JSONArray array = (com.alibaba.fastjson2.JSONArray) parmTypeValue;

          if ("string[]".equals(parmTypeValue)) {
            map.put(paramKey, array.toArray(new String[0]));
          } else if ("int[]".equals(parmTypeValue)) {
            map.put(paramKey, array.toArray(new Integer[0]));
          } else if ("long[]".equals(parmTypeValue)) {
            map.put(paramKey, array.toArray(new Long[0]));
          }
        }
      }
    }
  }
}
