package com.litong.jfinal.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.aop.Aop;
import com.litong.jfinal.model.TableColumn;

/**
 * @author bill robot
 * @date 2020年8月27日_下午7:35:57 
 * @version 1.0 
 * @desc
 */
public class PrimaryKeyService {
  private volatile Map<String, TableColumn> primaryKeys = new ConcurrentHashMap<>();
  private DbService dbService = Aop.get(DbService.class);

  /**
   * 获取主键的列名
   * @param tableName
   * @return
   */
  public String getPrimaryKeyName(String tableName) {
    TableColumn primaryKey = getPrimaryKey(tableName);
    return primaryKey.getField();
  }

  /**
   * 获取主键的类型
   * @param tableName
   * @return
   */
  public String getPrimaryKeyColumnType(String tableName) {
    TableColumn primaryKey = getPrimaryKey(tableName);
    return primaryKey.getType();
  }

  /**
   * 获取主键
   * @param tableName
   * @return
   */
  public TableColumn getPrimaryKey(String tableName) {
    TableColumn primaryKey = primaryKeys.get(tableName);
    if (primaryKey == null) {
      synchronized (dbService) {
        primaryKey = primaryKeys.get(tableName);
        if (primaryKey == null) {
          // 1.主键名称
          primaryKey = dbService.getPrimaryKey(tableName).get(0);
          primaryKeys.put(tableName, primaryKey);
        }

      }
    }
    return primaryKey;
  }
}
