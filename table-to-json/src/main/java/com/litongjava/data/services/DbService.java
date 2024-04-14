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
 * @author Tong Li
 * @version 1.0
 * @date 2020年8月27日_下午2:55:52
 * @desc
 */
public class DbService {

  public List<DbTableStruct> getPrimaryKey(String tableName) {
    List<DbTableStruct> ret = new ArrayList<>();

    Dialect dialect = DbKit.getConfig().getDialect();
    List<DbTableStruct> columns = null;
    if (dialect instanceof PostgreSqlDialect) {
      columns = getTableColumnsOfPostgre(tableName, "public");

    } else {
      columns = getTableColumnsOfMysql(tableName);
    }

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
  public List<DbTableStruct> getTableColumnsOfMysql(String tableName) {
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

  public List<DbTableStruct> getTableColumnsOfPostgre(String tableName, String tableSchema) {
    List<DbTableStruct> ret = new ArrayList<>();

    String sql = "SELECT column_name as field, data_type as type, is_nullable as isNull, " +
      "column_default as defaultValue, " +
      "CASE WHEN column_name = ANY (ARRAY(SELECT kcu.column_name " +
      "FROM information_schema.key_column_usage AS kcu " +
      "JOIN information_schema.table_constraints AS tc " +
      "ON kcu.constraint_name = tc.constraint_name " +
      "WHERE tc.table_name = ? AND tc.constraint_type = 'PRIMARY KEY')) THEN 'PRI' ELSE '' END AS key " +
      "FROM information_schema.columns WHERE table_name = ? and table_schema = ?;";

    List<Record> columns = Db.find(sql, tableName, tableName, tableSchema);

    for (Record record : columns) {
      DbTableStruct tableColumn = new DbTableStruct();
      tableColumn.setField(record.getStr("field"));
      tableColumn.setType(record.getStr("type"));
      tableColumn.setIsNull(record.getStr("isNull"));
      tableColumn.setDefaultValue(record.getStr("defaultValue"));
      tableColumn.setKey(record.getStr("key"));
      ret.add(tableColumn);
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
