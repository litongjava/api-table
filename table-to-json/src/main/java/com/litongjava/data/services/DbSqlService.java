package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.Operator;

/**
 * @author Ping E Lee
 * @version 1.0
 * @date 2020年8月27日_下午7:11:29
 * @desc
 */
public class DbSqlService {

  private TableColumnService tableColumnService = new TableColumnService();
  private OperatorService operatorService = new OperatorService();

  /**
   * 移除kv中不需要的值
   *
   * @param kv
   */
  private void removeKv(Kv kv) {

    // {pageNo=1, pageSize=10,
    // tableName=cron4j_task,orderBy=update_time,isAsc=false}
    kv.remove("page_no");
    kv.remove("page_size");
    kv.remove("table_name");
    kv.remove("order_by");
    kv.remove("is_asc");
    kv.remove("columns");
  }

  public List<Object> sqlExceptSelect(String tableName, int pageNo, int pageSize, String orderBy, Boolean isAsc, Kv kv,
      StringBuffer sqlExceptSelect) {
    if (pageNo == 0) {
      pageNo = 1;
    }
    if (pageSize == 0) {
      pageSize = 10;
    }

    // 移除kv中的值
    // pageNo,pageSize,tableName,orderBy,orderBy,isAsc
    removeKv(kv);
    // 添加表
    sqlExceptSelect.append("from " + tableName);
    StringBuffer where = new StringBuffer();
    where.append(" where ");
    // 获取查询条件
    List<Object> paramList = getListWhere(tableName, kv, where);

    // 拼接查询条件
    if (paramList.size() > 0) {
      sqlExceptSelect.append(where);
    }
    String orderField = "";
    if (orderBy != null) {
      orderField += " order by " + orderBy;
    }

    if (isAsc != null && !isAsc) {
      orderField += " desc";
    }
    sqlExceptSelect.append(orderField);

    return paramList;

  }

  /**
   * 根据kv中的key和value where表达式
   */
  private List<Object> getWhere(Kv kv, StringBuffer sql) {
    List<Object> paramList = new ArrayList<>();
    @SuppressWarnings("unchecked")
    Set<Map.Entry<String, Object>> entrySet = kv.entrySet();
    for (Map.Entry<String, Object> entry : entrySet) {
      Object value = entry.getValue();
      if (!StrKit.notNull(value)) {
        continue;
      }
      String key = entry.getKey();
      if (sql.toString().endsWith("where")) {
        sql.append(" " + key + " = ?");
      } else {
        sql.append(" and " + key + " = ?");
      }

      paramList.add(value);
    }
    return paramList;
  }

  /**
   * 根据kv中的键和值生成sql语句,并返回参数
   *
   * @param kv
   * @param where
   * @return
   */
  public List<Object> getListWhere(String tableName, Kv kv, StringBuffer where) {

    // 1.查询条件的值
    List<Object> paramList = new ArrayList<>();

    // 3.处理时间查询和其他条件
    Map<String, Object> notEqualsMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    Iterator<Map.Entry<String, Object>> iterator = kv.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> entry = iterator.next();
      String key = entry.getKey();
      Object value = entry.getValue();

      if (key.endsWith("_op")) {
        notEqualsMap.put(key, value);
        iterator.remove();
      }
    }

    List<Operator> operators = new ArrayList<>();
    for (Map.Entry<String, Object> e : notEqualsMap.entrySet()) {
      String key = e.getKey();
      String field = key.substring(0, key.lastIndexOf("_"));
      operators.add(new Operator(field, (String) e.getValue(), kv.remove(field)));
    }

    @SuppressWarnings("unchecked")
    Iterator<Map.Entry<String, Object>> iterator2 = kv.entrySet().iterator();
    while (iterator2.hasNext()) {
      Map.Entry<String, Object> entry = iterator2.next();
      operatorService.addWhereField(where, entry.getKey(), "=");
      paramList.add(entry.getValue());
    }

    operators.forEach(it -> {
      operatorService.addOperator(where, paramList, it.getField(), it.getValue(), it.getOprator());
    });

    // 数组类型
    return paramList;
  }

  public List<Object> getByIdWhere(String tableName, Kv kv, StringBuffer where, List<Object> paramList) {
    // 3.处理时间查询和其他条件
    return getWhere(kv, where);
  }

  public List<Object> removeByIdWhere(Kv kv, StringBuffer sql) {
    return getWhere(kv, sql);
  }

  /**
   * 无任何条件过滤,包含所有数据
   * @param kv
   * @return
   */
  public DbJsonBean<List<Record>> listAll(Kv kv) {
    String tableName = kv.getStr("tableName");
    StringBuffer sqlStringBuffer = new StringBuffer();
    sqlStringBuffer.append("select * from " + tableName);
    // 4.执行查询
    List<Record> find = Db.find(sqlStringBuffer.toString());
    return new DbJsonBean<List<Record>>(find);
  }

}
