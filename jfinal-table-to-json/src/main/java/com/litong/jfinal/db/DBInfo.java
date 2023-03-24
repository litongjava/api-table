package com.litong.jfinal.db;

import com.litong.jfinal.utils.PropKitUtil;

public class DBInfo {

  public static String dbType;
  public static String dbName;

  /**
   * 获取mysql的dbname
   */
  public static String getDbName() {
    String jdbcUrl = PropKitUtil.get("jdbc.url");
    if (dbName == null) {
      int startIndex = jdbcUrl.lastIndexOf("/") + 1;
      int endIndex = jdbcUrl.lastIndexOf("?");
      if (endIndex == -1) {
        dbName = jdbcUrl.substring(startIndex);
      } else {
        dbName = jdbcUrl.substring(startIndex, endIndex);
      }
    }
    return dbName;
  }

  public static String getDBType() {
    if (dbType == null) {
      String string = PropKitUtil.get("jdbc.url");
      getDbType(string);
    }
    return dbType;
  }

  public static void getDbType(String string) {
    if (string.contains("jdbc:oracle:thin:")) {
      dbType = "oracle";
    } else {
      dbType = "mysql";
    }
  }
}
