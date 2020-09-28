package com.litong.jfinal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;

/**
 * @author bill robot
 * @date 2020年8月27日_下午7:11:29 
 * @version 1.0 
 * @desc
 */
public class ApiFormService {

  @Inject
  private TableColumnSerivce tableColumnService;

  /**
   * 移除kv中不需要的值
   * @param kv
   */
  private void removeKv(Kv kv) {
    // {pageNo=1, pageSize=10,
    // tableName=cron4j_task,orderBy=update_time,isAsc=false}
    kv.remove("pageNo");
    kv.remove("pageSize");
    kv.remove("tableName");
    kv.remove("orderBy");
    kv.remove("isAsc");
    kv.remove("columns");
  }

  /**
   * id_del 必须为0
   * @return
   */
  public String getRequireCondition(String tableName,List<Object> paramList) {
    if (tableColumnService.isExists("is_del", tableName)) {
      paramList.add(0);
      return " is_del=?";
    }
    return "";
  }

  /**
   * 
   * @param pageNo
   * @param pageSize
   * @param tableName
   * @param orderBy
   * @param isAsc
   * @param kv
   * @param sqlExceptSelect2 
   * @param paramList2 
   */
  public List<Object> sqlExceptSelect(int pageNo, int pageSize, String tableName, String orderBy, Boolean isAsc, Kv kv,
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
    where.append(" where");
    // 获取查询插件
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
   * @param kv
   * @param where
   * @param paramList
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
   * @param kv
   * @param where
   * @return
   */
  public List<Object> getListWhere(String tableName, Kv kv, StringBuffer where) {

    // 1.查询条件的值
    List<Object> paramList = new ArrayList<>();

    // 2.添加过滤
    where.append(getRequireCondition(tableName,paramList));

    // 3.处理时间查询和其他条件
    @SuppressWarnings("unchecked")
    Set<Map.Entry<String, Object>> entrySet = kv.entrySet();
    for (Map.Entry<String, Object> entry : entrySet) {
      Object value = entry.getValue();
      if (!StrKit.notNull(value)) {
        continue;
      }
      String key = entry.getKey();

      if (key.startsWith("start.") || key.startsWith("end.") || key.startsWith("like.")) {
        String[] fieldAndValue = key.split("\\.");
        if (fieldAndValue[0].equals("start")) {
          addWhereField(where, fieldAndValue[1], ">=");
          paramList.add(value);
        } else if (fieldAndValue[0].equals("end")) {
          addWhereField(where, fieldAndValue[1], "<=");
          paramList.add(value);
        } else if (fieldAndValue[0].equals("like")) {
          addWhereField(where, fieldAndValue[1], "like");
          paramList.add("%" + value + "%");
        }
      } else {
        addWhereField(where, key, "=");
        paramList.add(value);
      }
    }
    return paramList;
  }

  /**
   * 添加where添加,判断and是否存在
   * @param sql
   * @param field
   * @param operator
   */
  public void addWhereField(StringBuffer sql, String field, String operator) {
    if (sql.toString().endsWith("where")) {
      sql.append(" " + field + " " + operator + " ?");
    } else {
      sql.append(" and " + field + " " + operator + " ?");
    }
  }

  public List<Object> getByIdWhere(String tableName, Kv kv, StringBuffer where,List<Object> paramList) {
    // 1.添加过滤
    where.append(getRequireCondition(tableName,paramList));
    // 3.处理时间查询和其他条件
    return getWhere(kv, where);
  }

  public List<Object> removeByIdWhere(Kv kv, StringBuffer sql) {
    return getWhere(kv, sql);
  }

}
