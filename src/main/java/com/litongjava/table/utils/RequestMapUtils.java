package com.litongjava.table.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class RequestMapUtils {

  public static void putEntityToMap(Map<String, Object> map, Object reqVo) {
    Arrays.stream(reqVo.getClass().getDeclaredMethods())
        // 1.转为stream对象 2.过滤到get方法
        .filter(method -> method.getName().startsWith("get") && method.getParameterCount() == 0)
        // 2.遍历get方法
        .forEach(method -> {
          String fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
          try {
            Optional.ofNullable(method.invoke(reqVo)).ifPresent(value -> map.put(fieldName, value));
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }

        });
  }

}
