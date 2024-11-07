package com.litongjava.table.model;

import java.util.List;

import com.litongjava.table.constants.SqlTemplateConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by litonglinux@qq.com on 2023/6/8_17:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sql {
  private String columns = "*";
  private String tableName;
  private StringBuffer where = new StringBuffer();
  private List<Object> params;

  public Sql(StringBuffer where, List<Object> params) {
    this.where = where;
    this.params = params;
  }

  public void setColumns(String columns) {
    if (columns != null) {
      this.columns = columns;
    }
  }

  public String getsql() {
    // 拼接sql语句
    StringBuffer sql = new StringBuffer();
    String format = String.format(SqlTemplateConstants.SELECT_COLUMNS_FROM, columns, tableName);
    sql.append(format);
    sql.append(" " + where.toString());
    return sql.toString();
  }

  public String getDelSql() {
    StringBuffer sql = new StringBuffer();
    sql.append("DELETE FROM " + tableName);
    sql.append(" " + where.toString());
    return sql.toString();
  }

  public String getSqlExceptSelect() {
    return "from " + tableName + " " + where.toString();
  }

  public String getSelectColumns() {
    return "select " + columns;
  }
}
