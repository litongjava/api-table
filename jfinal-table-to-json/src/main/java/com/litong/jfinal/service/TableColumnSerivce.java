package com.litong.jfinal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.aop.Aop;
import com.jfinal.plugin.activerecord.Record;

/**
 * @author bill robot
 * @date 2020年8月31日_下午10:42:02 
 * @version 1.0 
 * @desc
 */
public class TableColumnSerivce {

  private DbService dbService = Aop.get(DbService.class);

  /**
   * 存放表名和字段名
   */
  private Map<String, List<String>> tableColumns = new ConcurrentHashMap<>();

  /**
   * 判断字段在表格中是否存在
   * @param cloumn
   * @param tableName
   * @return
   */
  public boolean isExists(String cloumn, String tableName) {
    List<String> list = tableColumns.get(tableName);
    if (list == null || list.size() == 0) {
      return getColumnsToMap(tableName, cloumn);
    }

    for (String field : list) {
      if (field.equals(cloumn)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 从数据库中获取自动名存放到map中
   * @param tableName
   * @param cloumn
   * @return
   */
  private boolean getColumnsToMap(String tableName, String cloumn) {
    boolean ret = false;
    /*
     * { "Field": "id", "Type": "int(11) unsigned", "Null": "NO", "Extra":
     * "auto_increment", "Default": null, "Key": "PRI" },
     */
    List<Record> listRecord = dbService.cloumns(tableName);
    List<String> cloumns = new ArrayList<>();
    for (Record record : listRecord) {
      String field = record.getStr("Field");
      cloumns.add(field);
      if (field.equals(cloumn)) {
        ret = true;
      }
    }
    // 添加到map中
    synchronized (dbService) {
      List<String> list = tableColumns.get(tableName);
      if (list == null || list.size() == 0) {
        tableColumns.put(tableName, cloumns);
      }
    }

    return ret;
  }

  /**
   * 清楚缓存
   * @param tableName
   */
  public boolean clear(String tableName) {
    synchronized (dbService) {
      List<String> list = tableColumns.get(tableName);
      if (list != null && list.size() > 0) {
        tableColumns.remove(tableName);
      }
      return true;
    }
  }

  /**
   * 
   * @param tableName
   * @param field
   * @param type 不仅仅是type,eg,CHAR(1) NULL DEFAULT '0'
   * @param commons
   */
  public void addColumn(String tableName, String field, String type, String comment) {
    clear(tableName);
    dbService.addColumn(tableName,field,type,comment);
  }

}
