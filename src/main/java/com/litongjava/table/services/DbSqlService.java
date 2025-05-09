package com.litongjava.table.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.litongjava.db.TableInput;
import com.litongjava.db.activerecord.DbPro;
import com.litongjava.db.activerecord.dialect.PostgreSqlDialect;
import com.litongjava.table.model.DataQueryRequest;
import com.litongjava.table.model.Sql;
import com.litongjava.table.utils.ObjectUtils;

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
  private void removeKv(TableInput kv) {

    // {pageNo=1, pageSize=10,
    // tableName=cron4j_task,orderBy=update_time,isAsc=false}
    kv.remove("page_no");
    kv.remove("page_size");
    kv.remove("table_name");
    kv.remove("order_by");
    kv.remove("is_asc");
    kv.remove("columns");
  }

  public Sql getWhereClause(DbPro dbPro, DataQueryRequest queryRequest, TableInput kv) {
    // 移除kv中的值
    // pageNo,pageSize,tableName,orderBy,orderBy,isAsc
    removeKv(kv);
    // 获取查询条件
    Sql whereClause = getWhereQueryClause(dbPro, kv);
    StringBuffer where = whereClause.getWhere();
    if (where.toString().equals("where ")) {
      where.setLength(0);
    }

    String orderBy = queryRequest.getOrderBy();
    Boolean isAsc = queryRequest.getIsAsc();
    String groupBy = queryRequest.getGroupBy();
    Integer pageNo = queryRequest.getPageNo();
    Integer pageSize = queryRequest.getPageSize();

    if (orderBy != null) {
      where.append(" order by ").append(orderBy);
    }

    if (isAsc != null && !isAsc) {
      where.append(" desc");
    }

    if (groupBy != null) {
      where.append(" group by ").append(groupBy);
    }
    if (pageNo != null && pageSize != null) {
      int offset = (pageNo - 1) * pageSize;
      where.append(" limit ").append(pageSize).append(" offset ").append(offset);

    } else if (pageSize != null) {
      where.append(" limit ").append(pageSize);
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
  public Sql getWhereQueryClause(DbPro dbPro, TableInput kv) {
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

    String searchKey = kv.getSearchKey();
    String searchKeyLogic = kv.getStr(TableInput.search_key + "_logic");
    if (searchKey != null) {
      kv.remove(TableInput.search_key);
      if (dbPro.getConfig().getDialect() instanceof PostgreSqlDialect) {
        if (!sql.toString().endsWith("where ")) {
          if ("or".equals(searchKeyLogic)) {
            sql.append(" ").append(searchKeyLogic).append(" ");
          } else {
            sql.append(" ").append("and").append(" ");
          }
        }
        sql.append("search_vector @@ to_tsquery('english', ?)");
        paramList.add(searchKey);
      }
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
    return new Sql(sql);
  }

}
