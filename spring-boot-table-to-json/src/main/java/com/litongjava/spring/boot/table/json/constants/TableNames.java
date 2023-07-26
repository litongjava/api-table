package com.litongjava.spring.boot.table.json.constants;

import java.util.LinkedHashMap;
import java.util.Map;

public class TableNames {

  /**
   * 数据表的别名
   */
  public static Map<String, String> tableNameMap = new LinkedHashMap<>();
  static {
    tableNameMap.put("alarm", "cf_alarm");
    tableNameMap.put("alarm-ai", "cf_alarm_ai");

  }

  public static String getTableName(String f) {
    return tableNameMap.get(f);
  }

  public static String[] getF() {
    return tableNameMap.keySet().toArray(new String[0]);
  }
}
