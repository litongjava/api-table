package com.litongjava.table.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.kit.TypeKit;
import com.litongjava.tio.utils.json.Json;

@SuppressWarnings({ "rawtypes" })
public class TableInput extends HashMap {
  private static final long serialVersionUID = -808251639784763326L;
  public static final String pageNo = "page_no";
  public static final String pageSize = "page_size";
  public static final String columns = "columns";
  public static final String jsonFields = "json_fields";
  public static final String orderBy = "order_by";
  public static final String isAsc = "is_asc";
  public static final String groupBy = "group_by";

  public TableInput() {
  }

  public static TableInput of(Object key, Object value) {
    return new TableInput().set(key, value);
  }

  public static TableInput by(Object key, Object value) {
    return new TableInput().set(key, value);
  }

  public static TableInput create() {
    return new TableInput();
  }

  @SuppressWarnings("unchecked")
  public TableInput set(Object key, Object value) {
    super.put(key, value);
    return this;
  }

  public TableInput setIfNotBlank(Object key, String value) {
    if (StrKit.notBlank(value)) {
      set(key, value);
    }
    return this;
  }

  public TableInput setIfNotNull(Object key, Object value) {
    if (value != null) {
      set(key, value);
    }
    return this;
  }

  @SuppressWarnings("unchecked")
  public TableInput set(Map map) {
    super.putAll(map);
    return this;
  }

  @SuppressWarnings("unchecked")
  public TableInput set(Kv kv) {
    super.putAll(kv);
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public TableInput set(TableInput kv) {
    super.putAll(kv);
    return this;
  }

  public TableInput delete(Object key) {
    super.remove(key);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> T getAs(Object key) {
    return (T) get(key);
  }

  @SuppressWarnings("unchecked")
  public <T> T getAs(Object key, T defaultValue) {
    Object ret = get(key);
    return ret != null ? (T) ret : defaultValue;
  }

  public String getStr(Object key) {
    Object s = get(key);
    return s != null ? s.toString() : null;
  }

  public Integer getInt(Object key) {
    return TypeKit.toInt(get(key));
  }

  public Long getLong(Object key) {
    return TypeKit.toLong(get(key));
  }

  public BigDecimal getBigDecimal(Object key) {
    return TypeKit.toBigDecimal(get(key));
  }

  public Double getDouble(Object key) {
    return TypeKit.toDouble(get(key));
  }

  public Float getFloat(Object key) {
    return TypeKit.toFloat(get(key));
  }

  public Number getNumber(Object key) {
    return TypeKit.toNumber(get(key));
  }

  public Boolean getBoolean(Object key) {
    return TypeKit.toBoolean(get(key));
  }

  public java.util.Date getDate(Object key) {
    return TypeKit.toDate(get(key));
  }

  public java.time.LocalDateTime getLocalDateTime(Object key) {
    return TypeKit.toLocalDateTime(get(key));
  }

  /**
   * key 存在，并且 value 不为 null
   */
  public boolean notNull(Object key) {
    return get(key) != null;
  }

  /**
   * key 不存在，或者 key 存在但 value 为null
   */
  public boolean isNull(Object key) {
    return get(key) == null;
  }

  /**
   * key 存在，并且 value 为 true，则返回 true
   */
  public boolean isTrue(Object key) {
    Object value = get(key);
    return value != null && TypeKit.toBoolean(value);
  }

  /**
   * key 存在，并且 value 为 false，则返回 true
   */
  public boolean isFalse(Object key) {
    Object value = get(key);
    return value != null && !TypeKit.toBoolean(value);
  }

  public String toJson() {
    return Json.getJson().toJson(this);
  }

  public boolean equals(Object kv) {
    return kv instanceof Kv && super.equals(kv);
  }

  @SuppressWarnings({ "unchecked" })
  public TableInput keep(String... keys) {
    if (keys != null && keys.length > 0) {
      Kv newKv = Kv.create();
      for (String k : keys) {
        if (containsKey(k)) { // 避免将并不存在的变量存为 null
          newKv.put(k, get(k));
        }
      }

      clear();
      putAll(newKv);
    } else {
      clear();
    }

    return this;
  }

  @SuppressWarnings("unchecked")
  public <K, V> Map<K, V> toMap() {
    return this;
  }

  public TableInput setCoumns(String inputColumns) {
    set(columns, inputColumns);
    return this;
  }

  public String getCoumns() {
    return getStr(columns);
  }

  public TableInput setJsonFields(String[] inputJsonFields) {
    set(jsonFields, inputJsonFields);
    return this;
  }

  public String[] getJsonFields() {
    return getAs(jsonFields);
  }

  public TableInput setOrderBy(String inputOrderBy) {
    set(orderBy, inputOrderBy);
    return this;
  }

  public String getOrderBy() {
    return getStr(orderBy);
  }

  public TableInput setGroupBy(String inputGroupBy) {
    set(orderBy, inputGroupBy);
    return this;
  }

  public String getGroupBy() {
    return getStr(groupBy);
  }

  public TableInput setPageNo(Integer inputPageNo) {
    return set(pageNo, inputPageNo);
  }

  public Integer getPageNo() {
    return getInt(pageNo);
  }

  public TableInput setPageSize(Integer inputPageSize) {
    return set(pageSize, inputPageSize);
  }

  public Integer getPageSize() {
    return getInt(pageSize);
  }
  
  public TableInput setPageSize(Boolean inputIsAsc) {
    return set(isAsc, inputIsAsc);
  }

  public Boolean isAsc() {
    return getBoolean(isAsc);
  }
}
