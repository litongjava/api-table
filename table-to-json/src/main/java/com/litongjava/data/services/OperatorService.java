package com.litongjava.data.services;

import java.util.Collections;
import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.litongjava.data.constants.OperatorConstants;
import com.litongjava.data.utils.ObjectUtils;

public class OperatorService {
  public void addOperator(StringBuffer where, List<Object> paramList, String fieldName, String operator, Kv kv) {
    Object value = kv.get(fieldName);
    if (!ObjectUtils.isEmpty(value)) {
      if (OperatorConstants.EQ.equals(operator)) {
        addWhereAndField(where, fieldName, "=");
        paramList.add(kv.remove(fieldName));
      } else if (OperatorConstants.NE.equals(operator)) {
        addWhereAndField(where, fieldName, "!=");
        paramList.add(kv.remove(fieldName));

      } else if (OperatorConstants.GT.equals(operator)) {
        addWhereAndField(where, fieldName, ">");
        paramList.add(kv.remove(fieldName));

      } else if (OperatorConstants.GE.equals(operator)) {
        addWhereAndField(where, fieldName, ">=");
        paramList.add(kv.remove(fieldName));

      } else if (OperatorConstants.LT.equals(operator)) {
        addWhereAndField(where, fieldName, "<");
        paramList.add(kv.remove(fieldName));

      } else if (OperatorConstants.LE.equals(operator)) {
        addWhereAndField(where, fieldName, "<=");
        paramList.add(kv.remove(fieldName));

      } else if (OperatorConstants.BT.equals(operator)) {
        Object[] valueArray = (Object[]) kv.remove(fieldName);
        if (valueArray.length > 1) {
          addWhereFieldForWoOperator(where, fieldName, "between", "and");
          paramList.add(valueArray[0]);
          paramList.add(valueArray[1]);
        }
      } else if (OperatorConstants.NB.equals(operator)) {
        addWhereFieldForWoOperator(where, fieldName, "not between", "and");
        Object[] valueArray = (Object[]) kv.remove(fieldName);
        paramList.add(valueArray[0]);
        paramList.add(valueArray[1]);

      } else if (OperatorConstants.CT.equals(operator)) {
        addWhereAndField(where, fieldName, "like");
        paramList.add("%" + kv.remove(fieldName) + "%");

      } else if (OperatorConstants.SW.equals(operator)) {
        value = kv.remove(fieldName);
        if (StrKit.notNull(kv.remove(fieldName))) {
          addWhereAndField(where, fieldName, "like");
          paramList.add("%" + value);
        }

      } else if (OperatorConstants.EW.equals(operator)) { // EndWith
        value = kv.remove(fieldName);
        addWhereAndField(where, fieldName, "like");
        paramList.add(value + "%");
      } else if (OperatorConstants.OL.equals(operator)) {
        value = kv.remove(fieldName);
        if (value instanceof Object[]) {
          Object[] valueArray = (Object[]) value;
          addWhereOrField(where, fieldName, "like", valueArray);
          Collections.addAll(paramList, valueArray);
        }

      } else if (OperatorConstants.NK.equals(operator)) {
        value = kv.remove(fieldName);
        addWhereAndField(where, fieldName, "not like");
        paramList.add(value);

      } else if (OperatorConstants.IL.equals(operator)) {
        value = kv.remove(fieldName);
        if (value instanceof Object[]) {
          Object[] valueArray = (Object[]) value;
          addWhereInField(where, fieldName, "in", valueArray);
          Collections.addAll(paramList, valueArray);
        }

      } else if (OperatorConstants.NI.equals(operator)) {
        value = kv.remove(fieldName);
        if (value instanceof Object[]) {
          Object[] valueArray = (Object[]) value;
          addWhereInField(where, fieldName, "not in", valueArray);
          Collections.addAll(paramList, valueArray);
        }
      } else if (OperatorConstants.NL.equals(operator)) {
        addWhereNotValueField(where, fieldName, "is null", "and");

      } else if (OperatorConstants.NN.equals(operator)) {
        addWhereNotValueField(where, fieldName, "is not null", "and");

      } else if (OperatorConstants.EY.equals(operator)) {
        addWhereEmptyField(where, fieldName);

      } else if (OperatorConstants.NY.equals(operator)) {
        addWhereNotEmptyField(where, fieldName);
      }
    } else {
      if (OperatorConstants.NL.equals(operator)) {
        addWhereAndField(where, fieldName, "is null");
      } else if (OperatorConstants.NN.equals(operator)) {
        addWhereAndField(where, fieldName, "is not null");
      } else if (OperatorConstants.EY.equals(operator)) {
        addWhereEmptyField(where, fieldName);

      } else if (OperatorConstants.NY.equals(operator)) {
        addWhereNotEmptyField(where, fieldName);
      }
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
   */
  public void addWhereAndField(StringBuffer sql, String fieldName, String operator) {
    addWhereField(sql, fieldName, operator, "and");
  }

  public void addWhereOrField(StringBuffer sql, String fieldName, String operator) {
    addWhereField(sql, fieldName, operator, "or");
  }

  public void addWhereField(StringBuffer sql, String fieldName, String operator, String logic) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" ").append(logic).append(" ");
    }
    String format = "%s %s ?";
    sql.append(String.format(format, fieldName, operator));
  }

  public void addWhereNotValueField(StringBuffer sql, String fieldName, String operator, String logic) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" ").append(logic).append(" ");
    }
    String format = "%s %s";
    sql.append(String.format(format, fieldName, operator));
  }


  public void addWhereFieldForWoOperator(StringBuffer sql, String fieldName, String operator1, String operator2) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    // eg: time between ? and ?
    String format = "%s %s ? %s ?";
    sql.append(String.format(format, fieldName, operator1, operator2));
  }

  public void addWhereNotEmptyField(StringBuffer sql, String fieldName) {


    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    String format = "%s is not null and %s != ''";
    sql.append(String.format(format, fieldName, fieldName));
  }

  public void addWhereEmptyField(StringBuffer sql, String fieldName) {
    if (!sql.toString().endsWith("where ")) {
      sql.append(" and ");
    }

    String format = "%s is null or %s = ''";

    sql.append(String.format(format, fieldName, fieldName));
  }

}
