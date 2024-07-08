package com.litongjava.data.utils;

import java.util.Objects;

public class ObjectUtils {

  public static boolean isEmpty(Object obj) {
    if (Objects.toString(obj, "").equals("")) {
      return true;
    } else {
      return false;
    }
  }
}
