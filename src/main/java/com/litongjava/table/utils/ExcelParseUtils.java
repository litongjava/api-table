package com.litongjava.table.utils;

import java.math.BigDecimal;

public class ExcelParseUtils {

  /** 转为 BigDecimal（去除 $, % 和 ,） */
  public static BigDecimal toBigDecimal(Object obj) {
    if (obj == null) {
      return BigDecimal.ZERO;
    }
    if (obj instanceof Number) {
      // 直接用 Number 的 toString 保留原始精度
      return new BigDecimal(obj.toString());
    }
    // 是 String 时
    String s = obj.toString().replace("$", "").replace("%", "").replace(",", "").trim();
    if (s.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(s);
  }

  /** 转为 Integer（去除 ,） */
  public static Integer toInteger(Object obj) {
    if (obj == null) {
      return 0;
    }
    if (obj instanceof Number) {
      return ((Number) obj).intValue();
    }
    String s = obj.toString().replace(",", "").trim();
    if (s.isEmpty()) {
      return 0;
    }
    return Integer.valueOf(s);
  }

}
