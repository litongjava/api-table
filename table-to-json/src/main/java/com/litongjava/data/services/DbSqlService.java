package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.Kv;
import com.litongjava.data.model.DataQueryRequest;
import com.litongjava.data.model.Sql;
import com.litongjava.data.utils.ObjectUtils;

/**
 * @author Ping E Lee
 * @version 1.0
 * @date 2020年8月27日_下午7:11:29
 * @desc
 */
public class DbSqlService {

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

  public Sql getWhereClause(DataQueryRequest queryRequest, Kv kv) {
    // 移除kv中的值
    // pageNo,pageSize,tableName,orderBy,orderBy,isAsc
    removeKv(kv);
    // 获取查询条件
    Sql whereClause = getWhereQueryClause(kv);
    StringBuffer where = whereClause.getWhere();

    String orderBy = queryRequest.getOrderBy();
    Boolean isAsc = queryRequest.getIsAsc();
    String groupBy = queryRequest.getGroupBy();

    if (orderBy != null) {
      where.append(" order by ").append(orderBy);
    }

    if (isAsc != null && !isAsc) {
      where.append(" desc");
    }

    if (groupBy != null) {
      where.append(" group by ").append(groupBy);
    }

    whereClause.setWhere(where);
    return whereClause;

  }

  /**
   * 根据kv中的键和值生成sql语句,并返回参数
   *
   * @param kv
   * @return
   */
  public Sql getWhereQueryClause(Kv kv) {
    StringBuffer sql = new StringBuffer();
    sql.append("where ");
    // 查询条件的值
    List<Object> paramList = new ArrayList<>();

    // 没有操作符的查询
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

    for (Map.Entry<String, Object> e : notEqualsMap.entrySet()) {
      String key = e.getKey();
      String field = key.substring(0, key.lastIndexOf("_"));

      operatorService.addOperator(sql, paramList, field, (String) e.getValue(), kv);
      //operators.add(new Operator(field, (String) e.getValue(), kv.remove(field)));
    }

    @SuppressWarnings("unchecked")
    Iterator<Map.Entry<String, Object>> iterator2 = kv.entrySet().iterator();
    while (iterator2.hasNext()) {
      Map.Entry<String, Object> entry = iterator2.next();
      String fieldName = entry.getKey();
      if (fieldName.endsWith("_logic")) {
        continue;
      }
      Object fieldValue = entry.getValue();
      if (!ObjectUtils.isEmpty(fieldValue)) {
        String logic = (String) kv.get(fieldName + "_logic");
        if ("or".equals(logic)) {
          operatorService.addWhereOrField(sql, fieldName, "=");
        } else {
          operatorService.addWhereAndField(sql, fieldName, "=");
        }
        paramList.add(fieldValue);
        iterator2.remove();
      }

    }

    // 数组类型
    if (paramList.size() > 0) {
      return new Sql(sql, paramList);
    }
    return new Sql();
  }

}
