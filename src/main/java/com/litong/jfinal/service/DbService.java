package com.litong.jfinal.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bill robot
 * @date 2020年8月27日_下午2:55:52 
 * @version 1.0 
 * @desc
 */
@Slf4j
public class DbService {

  public String[] getPrimaryKey(String tableName) {
    List<Record> cloumns = cloumns(tableName);
    List<String> ret = new ArrayList<String>();
    for (Record record : cloumns) {
      String key = record.getStr("Key");
      if ("PRI".equals(key)) {
        ret.add(record.getStr("Field"));
      }
    }
    if (ret.size() == 0) {
      ret.add("id");
    }
    return toArray(ret);
  }

  private String[] toArray(List<String> list) {
    int size = list.size();
    String[] ret = new String[size];
    for (int i = 0; i < size; i++) {
      ret[i] = list.get(i);
    }
    return ret;
  }

  /**
   * 查询表名
   * @return
   */
  public List<Record> tables() {
    String sql = "show tables";
    List<Record> find = Db.find(sql);
    return find;
  }

  /**
   * 查询表字段
   * @param tableName
   * @return
   */
  public List<Record> cloumns(String tableName) {
    String sql = "show columns from " + tableName;
    List<Record> find = Db.find(sql);
    return find;
  }

  /**
   * 增加表字段
   * @param tableName
   * @param field
   * @param type
   * @param comment
   */
  public void addColumn(String tableName, String field, String type, String comment) {
    String sql = "ALTER TABLE %s ADD COLUMN `%s` %s comment '%s';";
    sql = String.format(sql, tableName, field, type, comment);
    int update = Db.update(sql);
    log.info("execute sql:{},return code:{}", sql, update);
  }
}
