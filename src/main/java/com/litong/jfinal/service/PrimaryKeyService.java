package com.litong.jfinal.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.aop.Aop;

/**
 * @author bill robot
 * @date 2020年8月27日_下午7:35:57 
 * @version 1.0 
 * @desc
 */
public class PrimaryKeyService {
  private volatile Map<String, String> primaryKeys = new ConcurrentHashMap<>();
  private DbService dbService = Aop.get(DbService.class);

  public String getPrimaryKey(String tableName) {
    String string = primaryKeys.get(tableName);
    if (string == null) {
      synchronized (dbService) {
        string = primaryKeys.get(tableName);
        if (string == null) {
          // 1.主键名称
          String keyName = dbService.getPrimaryKey(tableName)[0];
          primaryKeys.put(tableName, keyName);
          return keyName;
        }

      }
    }
    return string;
  }
}
