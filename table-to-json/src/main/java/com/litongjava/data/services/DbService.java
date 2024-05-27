package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litongjava.data.model.DbTableStruct;
import com.litongjava.data.utils.MarkdownTableUtils;
import com.litongjava.jfinal.plugin.activerecord.Db;
import com.litongjava.jfinal.plugin.activerecord.DbPro;
import com.litongjava.jfinal.plugin.activerecord.Page;
import com.litongjava.jfinal.plugin.activerecord.Record;
import com.litongjava.jfinal.plugin.activerecord.dialect.Dialect;
import com.litongjava.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.litongjava.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;

/**
 * @author Tong Li
 * @version 1.0
 * @date 2020年8月27日_下午2:55:52
 * @desc
 */
public class DbService {

  public Map<String, List<Record>> getAllTableColumns(DbPro dbPro) {
    Map<String, List<Record>> retval = new HashMap<>();

    String[] tableNames = tableNames(dbPro);
    for (String name : tableNames) {
      retval.put(name, getTableColumns(dbPro, name));
    }
    return retval;
  }

  public String getAllTableDataExamplesOfMarkdown(DbPro dbPro) {
    StringBuilder markdown = new StringBuilder();

    String[] tableNames = tableNames(dbPro);
    for (int i = 0; i < tableNames.length; i++) {
      String name = tableNames[i];
      Page<Record> paginate = dbPro.paginate(1, 1, "select *", "from " + name);
      if (paginate == null) {
        continue;
      }
      List<Record> records = paginate.getList();
      if (records == null) {
        continue;
      }

      String markdownTable = MarkdownTableUtils.to(records);

      markdown.append("**" + (i + 1) + " ." + name + "**\n");
      markdown.append(markdownTable);
    }
    return markdown.toString();
  }

  /**
   * 为ai提供的支持
   * @param dbPro
   * @return
   */
  public String getAllTableColumnsOfMarkdown(DbPro dbPro) {

    StringBuilder markdown = new StringBuilder();

    String[] tableNames = tableNames(dbPro);
    for (int i = 0; i < tableNames.length; i++) {
      String name = tableNames[i];
      List<Record> tableColumns = getTableColumns(dbPro, name);

      String markdownTable = MarkdownTableUtils.to(tableColumns);

      markdown.append("**" + (i + 1) + " ." + name + "**\n");
      markdown.append(markdownTable);
    }
    return markdown.toString();
  }

  public List<DbTableStruct> getPrimaryKey(DbPro dbPro, String tableName) {
    List<DbTableStruct> ret = new ArrayList<>();

    List<DbTableStruct> columns = getTableStruct(dbPro, tableName);

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
   * support mysql and postgresql
   */
  public String[] tableNames(DbPro dbPro) {
    List<Record> tables = null;
    Dialect dialect = dbPro.getConfig().getDialect();
    if (dialect instanceof PostgreSqlDialect) {
      String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';";
      tables = Db.find(sql);
    } else {
      String sql = "show tables";
      tables = Db.find(sql);
    }

    int size = tables.size();
    String[] retval = new String[size];
    for (int i = 0; i < size; i++) {
      retval[i] = (String) tables.get(i).getColumnValues()[0];
    }
    return retval;
  }

  public String[] tableNames() {
    return tableNames(Db.use());
  }

  public List<DbTableStruct> getTableStruct(DbPro dbPro, String tableName) {
    Dialect dialect = dbPro.getConfig().getDialect();
    List<Record> columns = getTableColumns(dbPro, tableName);
    ;

    List<DbTableStruct> ret = new ArrayList<>();

    if (dialect instanceof PostgreSqlDialect) {

    } else if (dialect instanceof MysqlDialect) {
      for (Record record : columns) {
        DbTableStruct tableColumn = new DbTableStruct();
        tableColumn.setField(record.getStr("Field"));
        tableColumn.setType(record.getStr("Type"));
        tableColumn.setIsNull(record.getStr("Null"));
        tableColumn.setDefaultValue(record.getStr("Default"));
        tableColumn.setKey(record.getStr("Key"));
        ret.add(tableColumn);
      }
      for (Record record : columns) {
        DbTableStruct tableColumn = new DbTableStruct();
        tableColumn.setField(record.getStr("field"));
        tableColumn.setType(record.getStr("type"));
        String key = record.getStr("key");
        tableColumn.setKey(key);
        ret.add(tableColumn);
      }

    } else {
      // 遍历出主键,添加到ret中
      for (Record record : columns) {
        DbTableStruct tableColumn = new DbTableStruct();
        tableColumn.setField(record.getStr("field"));
        tableColumn.setType(record.getStr("type"));
        String key = record.getStr("key");
        tableColumn.setKey(key);
        ret.add(tableColumn);
      }
    }
    return ret;

  }

  public List<Record> getTableColumns(DbPro dbPro, String tableName) {
    String sql;
    Dialect dialect = dbPro.getConfig().getDialect();
    List<Record> records;
    if (dialect instanceof PostgreSqlDialect) {
      // Field,Type,Null,Key,Default,Extra
      sql = "SELECT column_name as Field, data_type as Type, is_nullable as Null, " + "column_default as Default, "
          + "CASE WHEN column_name = ANY (ARRAY(SELECT kcu.column_name "
          + "FROM information_schema.key_column_usage AS kcu " + "JOIN information_schema.table_constraints AS tc "
          + "ON kcu.constraint_name = tc.constraint_name "
          + "WHERE tc.table_name = ? AND tc.constraint_type = 'PRIMARY KEY')) THEN 'PRI' ELSE '' END AS key "
          + "FROM information_schema.columns WHERE table_name = ? and table_schema = ?;";

      records = dbPro.find(sql, tableName, tableName, "public");
      // 即便将别名设置为大写,返回的依然是小写,气人
    } else {
      // Field,Type,Null,Key,Default,Extra
      sql = "show columns from " + tableName;
      records = dbPro.find(sql, tableName);
    }
    return records;
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
