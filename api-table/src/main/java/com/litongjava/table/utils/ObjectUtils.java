package com.litongjava.table.utils;

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
