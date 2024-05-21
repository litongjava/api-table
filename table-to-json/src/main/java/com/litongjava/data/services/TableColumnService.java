package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.litongjava.data.model.DbTableStruct;
import com.litongjava.jfinal.plugin.activerecord.Db;

/**
 * @author bill robot
 * @version 1.0
 * @date 2020年8月31日_下午10:42:02
 * @desc
 */
public class TableColumnService {

  private DbService dbService = new DbService();

  /**
   * 存放表名和字段名
   */
  private Map<String, List<String>> tableColumns = new ConcurrentSkipListMap<>();

  /**
   * 判断字段在表格中是否存在
   *
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
   *
   * @param tableName
   * @param column
   * @return
   */
  private boolean getColumnsToMap(String tableName, String column) {
    boolean ret = false;
    List<DbTableStruct> listRecord = dbService.getTableColumnsOfMysql(Db.use(), tableName);
    List<String> columns = new ArrayList<>();
    for (DbTableStruct record : listRecord) {
      String field = record.getField();
      columns.add(field);
      if (field.equals(column)) {
        ret = true;
      }
    }
    // 添加到map中
    synchronized (dbService) {
      List<String> list = tableColumns.get(tableName);
      if (list == null || list.size() == 0) {
        tableColumns.put(tableName, columns);
      }
    }

    return ret;
  }

  /**
   * 清楚缓存
   *
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
   * @param tableName
   * @param field
   * @param type      不仅仅是type,eg,CHAR(1) NULL DEFAULT '0'
   * @param commons
   */
  public void addColumn(String tableName, String field, String type, String comment) {
    clear(tableName);
    dbService.addColumn(tableName, field, type, comment);
  }

}
