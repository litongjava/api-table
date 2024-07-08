package com.litongjava.data.model;

import com.jfinal.kit.Kv;

@SuppressWarnings("serial")
public class KvBean extends Kv {
  public static final String columns = "columns";
  public static final String jsonFields = "json_fields";
  public static final String orderBy = "order_by";
  public static final String groupBy = "group_by";

  public KvBean() {
  }
  
  public KvBean(Kv kv) {
    set(kv);
  }

  public KvBean setCoumns(String inputColumns) {
    set(columns, inputColumns);
    return this;
  }

  public String getCoumns() {
    return getStr(columns);
  }

  public KvBean setJsonFields(String[] inputJsonFields) {
    set(jsonFields, inputJsonFields);
    return this;
  }

  public String[] getJsonFields() {
    return getAs(jsonFields);
  }

  public KvBean setOrderBy(String inputOrderBy) {
    set(orderBy, inputOrderBy);
    return this;
  }

  public String getOrderBy() {
    return getStr(orderBy);
  }

  public KvBean setGroupBy(String inputGroupBy) {
    set(orderBy, inputGroupBy);
    return this;
  }

  public String getGroupBy() {
    return getStr(groupBy);
  }

}
