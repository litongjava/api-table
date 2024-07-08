package com.litongjava.data.config;

public class DbDataConfig {

  public static String delCol = "is_del";
  public static String updateTimeCol = "update_time";
  public static String createTimeCol = "create_time";

  public static String getDelColName() {
    return delCol;
  }

  public static void setDelColName(String delColName) {
    delCol = delColName;
  }

  public static String getUpdateTimeCol() {
    return updateTimeCol;
  }

  public static void setUpdateTimeCol(String updateTimeColName) {
    updateTimeCol = updateTimeColName;
  }

  public static String getCreateTimeCol() {
    return createTimeCol;
  }

  public static void setCreateTimeCol(String createTimeColName) {
    createTimeCol = createTimeColName;
  }
}
