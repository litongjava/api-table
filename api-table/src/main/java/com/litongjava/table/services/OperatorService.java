package com.litongjava.table.services;

import java.util.Collections;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.litongjava.table.constants.Operators;
import com.litongjava.table.model.TableInput;
import com.litongjava.table.utils.ObjectUtils;

public class OperatorService {
  public void addOperator(StringBuffer where, List<Object> paramList, String fieldName, String operator, TableInput kv) {
    Object value = kv.get(fieldName);
    if (!ObjectUtils.isEmpty(value)) {
      if (Operators.EQ.equals(operator)) {
        addWhereAndField(where, fieldName, "=");
        paramList.add(kv.remove(fieldName));
      } else if (Operators.NE.equals(operator)) {
        addWhereAndField(where, fieldName, "!=");
        paramList.add(kv.remove(fieldName));

      } else if (Operators.GT.equals(operator)) {
        addWhereAndField(where, fieldName, ">");
        paramList.add(kv.remove(fieldName));

      } else if (Operators.GE.equals(operator)) {
        addWhereAndField(where, fieldName, ">=");
        paramList.add(kv.remove(fieldName));

      } else if (Operators.LT.equals(operator)) {
        addWhereAndField(where, fieldName, "<");
        paramList.add(kv.remove(fieldName));

      } else if (Operators.LE.equals(operator)) {
        addWhereAndField(where, fieldName, "<=");
        paramList.add(kv.remove(fieldName));

      } else if (Operators.BT.equals(operator)) {
        Object remove = kv.remove(fieldName);

        if (remove instanceof Object[]) {
          Object[] valueArray = (Object[]) remove;
          if (valueArray.length > 1) {
            addWhereFieldForWoOperator(where, fieldName, "between", "and");
            paramList.add(valueArray[0]);
            paramList.add(valueArray[1]);
          }
        } else if (remove instanceof List) {
          @SuppressWarnings("rawtypes")
          List list = (List) remove;
          if (list != null && list.size() > 1) {
            addWhereFieldForWoOperator(where, fieldName, "between", "and");
            paramList.add(list.get(0));
            paramList.add(list.get(1));
          }
        }

      } else if (Operators.NB.equals(operator)) {
        Object remove = kv.remove(fieldName);

        if (remove instanceof Object[]) {
          Object[] valueArray = (Object[]) remove;
          if (valueArray.length > 1) {
            addWhereFieldForWoOperator(where, fieldName, "not between", "and");
            paramList.add(valueArray[0]);
            paramList.add(valueArray[1]);
          }
        } else if (remove instanceof List) {
          @SuppressWarnings("rawtypes")
          List list = (List) remove;
          if (list != null && list.size() > 1) {
            addWhereFieldForWoOperator(where, fieldName, "not between", "and");
            paramList.add(list.get(0));
            paramList.add(list.get(1));
          }
        }
      } else if (Operators.CT.equals(operator)) {
        addWhereAndField(where, fieldName, "like");
        paramList.add("%" + kv.remove(fieldName) + "%");

      } else if (Operators.SW.equals(operator)) {
        value = kv.remove(fieldName);
        if (StrKit.notNull(kv.remove(fieldName))) {
          addWhereAndField(where, fieldName, "like");
          paramList.add("%" + value);
        }

      } else if (Operators.EW.equals(operator)) { // EndWith
        value = kv.remove(fieldName);
        addWhereAndField(where, fieldName, "like");
        paramList.add(value + "%");

      } else if (Operators.OL.equals(operator)) {
        value = kv.remove(fieldName);
        if (value instanceof Object[]) {
          Object[] valueArray = (Object[]) value;
          addWhereOrField(where, fieldName, "like", valueArray);
          Collections.addAll(paramList, valueArray);
        }

      } else if (Operators.NK.equals(operator)) {
        value = kv.remove(fieldName);
        addWhereAndField(where, fieldName, "not like");
        paramList.add(value);

      } else if (Operators.IL.equals(operator)) {
        value = kv.remove(fieldName);
        if (value instanceof Object[]) {
          Object[] valueArray = (Object[]) value;
          addWhereInField(where, fieldName, "in", valueArray);
          Collections.addAll(paramList, valueArray);
        }

      } else if (Operators.NI.equals(operator)) {
        value = kv.remove(fieldName);
        if (value instanceof Object[]) {
          Object[] valueArray = (Object[]) value;
          addWhereInField(where, fieldName, "not in", valueArray);
          Collections.addAll(paramList, valueArray);
        }
      } else if (Operators.NL.equals(operator)) {
        addWhereNotValueField(where, fieldName, "is null", "and");

      } else if (Operators.NN.equals(operator)) {
        addWhereNotValueField(where, fieldName, "is not null", "and");

      } else if (Operators.EY.equals(operator)) {
        addWhereEmptyField(where, fieldName);

      } else if (Operators.NY.equals(operator)) {
        addWhereNotEmptyField(where, fieldName);
      }
    } else {
      if (Operators.NL.equals(operator)) {
        addWhereAndField(where, fieldName, "is null");
      } else if (Operators.NN.equals(operator)) {
        addWhereAndField(where, fieldName, "is not null");
      } else if (Operators.EY.equals(operator)) {
        addWhereEmptyField(where, fieldName);

      } else if (Operators.NY.equals(operator)) {
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
