package com.litongjava.table.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.litongjava.db.activerecord.Db;
import com.litongjava.db.activerecord.DbPro;
import com.litongjava.table.model.DbTableStruct;

/**
 * @author bill robot
 * @date 2020年8月27日_下午7:35:57 
 * @version 1.0 
 * @desc
 */
public class PrimaryKeyService {
  private volatile Map<String, DbTableStruct> primaryKeys = new ConcurrentHashMap<>();
  private DbService dbService = new DbService();

  /**
   * 获取主键的列名
   * @param tableName
   * @return
   */
  public String getPrimaryKeyName(String tableName) {
    DbTableStruct primaryKey = getPrimaryKey(Db.use(), tableName);
    return primaryKey.getField();
  }

  public String getPrimaryKeyName(DbPro dbPro, String tableName) {
    DbTableStruct primaryKey = getPrimaryKey(dbPro, tableName);
    return primaryKey.getField();
  }

  /**
   * 获取主键的类型
   * @param tableName
   * @return
   */
  public String getPrimaryKeyColumnType(String tableName) {
    DbTableStruct primaryKey = getPrimaryKey(Db.use(), tableName);
    return primaryKey.getType();
  }

  public String getPrimaryKeyColumnType(DbPro dbPro, String tableName) {
    DbTableStruct primaryKey = getPrimaryKey(dbPro, tableName);
    return primaryKey.getType();
  }

  /**
   * 获取主键
   * @param tableName
   * @return
   */
  public DbTableStruct getPrimaryKey(DbPro dbPro, String tableName) {
    DbTableStruct primaryKey = primaryKeys.get(tableName);
    if (primaryKey == null) {
      synchronized (dbService) {
        primaryKey = primaryKeys.get(tableName);
        if (primaryKey == null) {
          // 1.主键名称
          primaryKey = dbService.getPrimaryKey(dbPro, tableName).get(0);
          primaryKeys.put(tableName, primaryKey);
        }

      }
    }
    return primaryKey;
  }
}
