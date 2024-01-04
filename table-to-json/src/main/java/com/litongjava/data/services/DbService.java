package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.List;

import com.litongjava.data.model.DbTableStruct;
import com.litongjava.jfinal.plugin.activerecord.Db;
import com.litongjava.jfinal.plugin.activerecord.DbKit;
import com.litongjava.jfinal.plugin.activerecord.Record;
import com.litongjava.jfinal.plugin.activerecord.dialect.Dialect;
import com.litongjava.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;

/**
 * @author bill robot
 * @version 1.0
 * @date 2020年8月27日_下午2:55:52
 * @desc
 */
public class DbService {

  public List<DbTableStruct> getPrimaryKey(String tableName) {
    List<DbTableStruct> ret = new ArrayList<>();

    Dialect dialect = DbKit.getConfig().getDialect();
    if (dialect instanceof PostgreSqlDialect) {
      DbTableStruct dbTableStruct = new DbTableStruct();
      dbTableStruct.setField("id");
      dbTableStruct.setType("long");
      dbTableStruct.setKey("PRI");
      ret.add(dbTableStruct);
    } else {
      List<DbTableStruct> columns = columns(tableName);

      // 遍历出主键,添加到ret中
      for (DbTableStruct record : columns) {
        String key = record.getKey();
        if ("PRI".equals(key)) {
          DbTableStruct tableColumn = new DbTableStruct();
          tableColumn.setField(record.getField());
          tableColumn.setType(record.getType());
          tableColumn.setKey(key);
          ret.add(tableColumn);
        }
      }
    }

    return ret;
  }

  @SuppressWarnings("unused")
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
   *
   * @return
   */
  public List<Record> tables() {
    String sql = "show tables";
    return Db.find(sql);
  }

  /**
   * 查询表字段
   *
   * @param tableName
   * @return {"Field": "id", "Type": "int(11) unsigned", "Null": "NO", "Extra": "auto_increment", "Default": null, "Key": "PRI" },
   */
  public List<DbTableStruct> columns(String tableName) {
    List<DbTableStruct> ret = new ArrayList<>();

    String sql = null;
    Dialect dialect = DbKit.getConfig().getDialect();
    if (dialect instanceof PostgreSqlDialect) {
      sql = "SELECT column_name as field, data_type as type, is_nullable, column_default FROM information_schema.columns " +
        "WHERE table_name ='" + tableName + "';";
      List<Record> columns = Db.find(sql);
      // 即便将别名设置为大写,返回的依然是小写,气人

      // 遍历出主键,添加到ret中
      for (Record record : columns) {
        DbTableStruct tableColumn = new DbTableStruct();
        tableColumn.setField(record.getStr("field"));
        tableColumn.setType(record.getStr("type"));
        String key = record.getStr("key");
        tableColumn.setKey(key);
        ret.add(tableColumn);
      }
    } else {
      sql = "show columns from " + tableName;
      List<Record> columns = Db.find(sql);


      // 遍历出主键,添加到ret中
      for (Record record : columns) {
        DbTableStruct tableColumn = new DbTableStruct();
        tableColumn.setField(record.getStr("Field"));
        tableColumn.setType(record.getStr("Type"));
        String key = record.getStr("Key");
        tableColumn.setKey(key);
        ret.add(tableColumn);
      }
    }


    return ret;
  }

  /**
   * 增加表字段
   *
   * @param tableName
   * @param field
   * @param type
   * @param comment
   */
  public void addColumn(String tableName, String field, String type, String comment) {
    String sql = "ALTER TABLE %s ADD COLUMN `%s` %s comment '%s';";
    sql = String.format(sql, tableName, field, type, comment);
    Db.update(sql);
  }
}
