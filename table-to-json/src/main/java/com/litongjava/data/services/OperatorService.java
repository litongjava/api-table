package com.litongjava.data.services;

import java.util.Collections;
import java.util.List;

public class OperatorService {
  public void addOperator(StringBuffer where, List<Object> paramList, String fieldName, Object value, String operator) {
    if ("eq".equals(operator)) {
      addWhereField(where, fieldName, "=");
      paramList.add(value);
    } else if ("ne".equals(operator)) {
      addWhereField(where, fieldName, "!=");
      paramList.add(value);

    } else if ("gt".equals(operator)) {
      addWhereField(where, fieldName, ">");
      paramList.add(value);

    } else if ("ge".equals(operator)) {
      addWhereField(where, fieldName, ">=");
      paramList.add(value);

    } else if ("lt".equals(operator)) {
      addWhereField(where, fieldName, "<");
      paramList.add(value);
    } else if ("le".equals(operator)) {
      addWhereField(where, fieldName, "<=");
      paramList.add(value);

    } else if ("bt".equals(operator)) {
      if (value instanceof Object[]) {
        addWhereField(where, fieldName, "between", "and");
        Object[] valueArray = (Object[]) value;
        paramList.add(valueArray[0]);
        paramList.add(valueArray[1]);
      }

    } else if ("nb".equals(operator)) {
      if (value instanceof Object[]) {
        addWhereField(where, fieldName, "not between", "and");
        Object[] valueArray = (Object[]) value;
        paramList.add(valueArray[0]);
        paramList.add(valueArray[1]);
      }

    } else if ("ct".equals(operator)) {
      addWhereField(where, fieldName, "like");
      paramList.add("%" + value + "%");
      paramList.add(value);

    } else if ("sw".equals(operator)) {
      addWhereField(where, fieldName, "like");
      paramList.add("%" + value);

    } else if ("ew".equals(operator)) { // EndWith
      addWhereField(where, fieldName, "like");
      paramList.add(value + "%");

    } else if ("ol".equals(operator)) {
      if (value instanceof Object[]) {
        Object[] valueArray = (Object[]) value;
        addWhereOrField(where, fieldName, "like", valueArray);
        for (int i = 0; i < valueArray.length; i++) {
          paramList.add(valueArray[i]);
        }
      }

    } else if ("nk".equals(operator)) {
      addWhereField(where, fieldName, "not like");
      paramList.add(value);

    } else if ("il".equals(operator)) {
      if (value instanceof Object[]) {
        Object[] valueArray = (Object[]) value;
        addWhereInField(where, fieldName, "in", valueArray);
        for (int i = 0; i < valueArray.length; i++) {
          paramList.add(valueArray[i]);
        }
      }

    } else if ("ni".equals(operator)) {
      if (value instanceof Object[]) {
        Object[] valueArray = (Object[]) value;
        addWhereInField(where, fieldName, "not in", valueArray);
        for (int i = 0; i < valueArray.length; i++) {
          paramList.add(valueArray[i]);
        }
      }

    } else if ("nl".equals(operator)) {
      addWhereField(where, fieldName, "is null");
    } else if ("nn".equals(operator)) {
      addWhereField(where, fieldName, "is not null");

    } else if ("ey".equals(operator)) {
      addWhereEmpytField(where, fieldName);

    } else if ("ny".equals(operator)) {
      addWhereNotEmpytField(where, fieldName);
    }

  }

  /**
   * x in (?, ?, ...)
   */
  private void addWhereInField(StringBuffer sql, String fieldName, String operator, Object[] valueArray) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }
    String format = "%s %s (%s)";
    String questionMarks = String.join(",", Collections.nCopies(valueArray.length, "?"));

    sql.append(String.format(format, fieldName, operator, questionMarks));

  }

  /**
   * sql 示例如下,用于完成括号中的多个内容
   * select * from cf_alarm_ai where deleted=0 and (text like 'Ai%' or text like 'ce%')
   */
  public void addWhereOrField(StringBuffer sql, String fieldName, String operator, Object[] valueArray) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    sql.append("(");
    String format = "%s %s ?";
    sql.append(String.format(format, fieldName, operator));

    String orFormat = "or %s %s ?";
    for (int i = 1; i < valueArray.length; i++) {
      sql.append(String.format(orFormat, fieldName, operator));
    }

    sql.append(")");
  }

  /**
   * 添加where添加,判断and是否存在
   *
   * @param sql
   * @param field
   * @param operator
   */
  public void addWhereField(StringBuffer sql, String fieldName, String operator) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    String format = "%s %s ?";
    sql.append(String.format(format, fieldName, operator));
  }

  public void addWhereField(StringBuffer sql, String fieldName, String operator1, String operator2) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    // eg: time between ? and ?
    String format = "%s %s ? %s ?";
    sql.append(String.format(format, fieldName, operator1, operator2));
  }

  public void addWhereNotEmpytField(StringBuffer sql, String fieldName) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    String format = "%s is null or %s = ''";
    sql.append(String.format(format, fieldName, fieldName));
  }

  public void addWhereEmpytField(StringBuffer sql, String fieldName) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    String format = "%s is not null and %s != ''";
    sql.append(String.format(format, fieldName, fieldName));

  }

}
