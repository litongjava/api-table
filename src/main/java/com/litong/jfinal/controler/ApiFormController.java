package com.litong.jfinal.controler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.litong.jfinal.service.ApiFormService;
import com.litong.jfinal.service.PrimaryKeyService;
import com.litong.jfinal.service.TableColumnSerivce;
import com.litong.jfinal.validate.ApiFormValidator;
import com.litong.jfinal.vo.PageJsonBean;
import com.litong.utils.vo.JsonBean;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bill robot
 * @date 2020年8月24日_下午3:05:36 
 * @version 1.0 
 * @desc
 */
@Slf4j
@Before(ApiFormValidator.class)
public class ApiFormController extends Controller {

  @Inject
  private ApiFormService apiFormService;
  @Inject
  private PrimaryKeyService primaryKeyService;
  @Inject
  private TableColumnSerivce tableColumnService;

  /**
   * 数据列表
   * @param kv 普通情况下的数据 
   * {pageNo=1, orderBy=update_time, pageSize=10, isAsc=false, tableName=cron4j_task}
   * 搜索情况下的数据
   * {end.create_time=2020-08-27 00:00:00, pageNo=1, orderBy=update_time, pageSize=10, start.create_time=2020-08-01 00:00:00, 
  //   * end.update_time=2020-08-27 00:00:00, isAsc=false, id=null, start.update_time=2020-08-01 00:00:00, tableName=cron4j_task}
   */
  public void list(int pageNo, int pageSize, String tableName, String columns, String orderBy, Boolean isAsc, Kv kv) {
    log.info("kv : " + kv);
    if (StrKit.isBlank(columns)) {
      columns = "*";
    }
    StringBuffer sqlExceptSelect = new StringBuffer();
    List<Object> paramList = apiFormService.sqlExceptSelect(pageNo, pageSize, tableName, orderBy, isAsc, kv, sqlExceptSelect);
    Page<Record> listPage = null;
    try {
      listPage = Db.paginate(pageNo, pageSize, "select " + columns, sqlExceptSelect.toString(), paramList.toArray());
    } catch (Exception e) {
      e.printStackTrace();
      renderJson(getException(e));
      return;
    }

    PageJsonBean<Record> pageJsonBean = new PageJsonBean<>(listPage);
    renderJson(pageJsonBean);
    return;
  }

  /**
   * 
   * @param kv
   * {id=1, tableName=cron4j_task}
   */
  public void getById(String tableName, Kv kv) {
    log.info("kv : " + kv);
    kv.remove("tableName");
    // 获取主键名称
    String primaryKey = primaryKeyService.getPrimaryKey(tableName);
    // 拼接sql语句
    StringBuffer sql = new StringBuffer();
    sql.append("select * from " + tableName + " where" + apiFormService.getRequireCondition(tableName));
    apiFormService.addWhereField(sql, primaryKey, "=");
    Object idValue = kv.get("id");

    // 添加操作表
    Record record = null;
    try {
      record = Db.findFirst(sql.toString(), idValue);
      // record = Db.findById(tableName, kv.get("id"));
    } catch (Exception e) {
      e.printStackTrace();
      renderJson(getException(e));
      return;
    }
    JsonBean<Record> jsonBean = new JsonBean<>(record);
    renderJson(jsonBean);
    return;
  }

  public void removeById(String tableName, Kv kv) {
    log.info("kv : " + kv);
    kv.remove("tableName");
    if (kv.size() == 0) {
      renderJson(new JsonBean<Void>(-1, "no params"));
      return;
    }
    // 判断is_del是否存在,如果不存在在创建
    String delFlagColumn = "is_del";
    boolean isExists = tableColumnService.isExists(delFlagColumn, tableName);
    if (!isExists) {
      tableColumnService.addColumn(tableName, delFlagColumn, "CHAR(1) NULL DEFAULT '0'", "是否删除,1删除,0未删除");
    }
    String primaryKey = primaryKeyService.getPrimaryKey(tableName);
    StringBuffer sql = new StringBuffer();

    sql.append("update " + tableName + " set is_del=1 where " + primaryKey + "=?");
    try {
      Db.update(sql.toString(), kv.get("id"));
    } catch (Exception e) {
      e.printStackTrace();
      renderJson(getException(e));
      return;
    }
    JsonBean<Void> jsonBean = new JsonBean<>();
    renderJson(jsonBean);
    return;
  }

  public void removeByIds(String tableName, Kv kv) {
    log.info("kv : " + kv);
    kv.remove("tableName");
    if (kv.size() == 0) {
      renderJson(new JsonBean<Void>(-1, "no params"));
      return;
    }
    @SuppressWarnings("unchecked")
    Set<String> keySet = kv.keySet();
    // 判断size大小
    int size = keySet.size();
    if (size < 1) {
      return;
    }
    // 获取ids,一共就只有1个
    String key = null;
    for (String string : keySet) {
      key = string;
    }

    String primaryKey = primaryKeyService.getPrimaryKey(tableName);

    String[] ids = getParaValues(key);
    log.info("paraValues : " + Arrays.toString(ids));
    // 是否为int值
    boolean isNumeric = isNumeric(ids[0]);
    // 根据int和string,组成不同类型的ids
    Object[] idValues = null;
    if (isNumeric) {
      idValues = new Integer[ids.length];
      for (int i = 0; i < ids.length; i++) {
        idValues[i] = Integer.parseInt(ids[i]);
      }
    } else {
      idValues = new String[ids.length];
      for (int i = 0; i < ids.length; i++) {
        idValues[i] = ids[i];
      }
    }
    StringBuffer where = new StringBuffer();
    for (int i = 0; i < idValues.length; i++) {
      if (i == idValues.length - 1) {
        where.append(primaryKey + "=?");
      } else {
        where.append(primaryKey + "=? or ");

      }
    }
    String sql = "update %s set is_del=1 where " + where.toString();
    sql = String.format(sql, tableName);
    try {
      Db.update(sql, idValues);
    } catch (Exception e) {
      e.printStackTrace();
      renderJson(getException(e));
      return;
    }
    JsonBean<Void> jsonBean = new JsonBean<>();
    renderJson(jsonBean);
    return;
  }

  /*
   * {tableName=cron4j_task,cron=* * * * *, task=xx.coas, name=同步数据, is_daemon=0,
   * id=null, is_enable=0}
   */
  @SuppressWarnings("unchecked")
  public void saveOrUpdate(String tableName, Kv kv) {
    log.info("kv : " + kv);
    kv.remove("tableName");

    true21(kv);
    Record record = new Record();
    record.setColumns(kv);
    if (tableColumnService.isExists("update_time", tableName)) {
      record.set("update_time", new Date());
    }

    String primaryKey = primaryKeyService.getPrimaryKey(tableName);
    if (kv.containsKey(primaryKey) && !StrKit.isBlank(kv.getStr(primaryKey))) { // 更新
      try {
        Db.update(tableName, record);
      } catch (Exception e) {
        e.printStackTrace();
        renderJson(getException(e));
        return;
      }
    } else { // 保存
      if (tableColumnService.isExists("create_time", tableName)) {
        record.set("create_time", new Date());
      }

      try {
        Db.save(tableName, record);
      } catch (Exception e) {
        e.printStackTrace();
        renderJson(getException(e));
        return;
      }
    }
    JsonBean<Void> jsonBean = new JsonBean<>();
    renderJson(jsonBean);
    return;
  }

  /**
   * 
   * @param e
   */
  private JsonBean<Void> getException(Exception e) {
    String message = e.getMessage();
    JsonBean<Void> jsonBean = new JsonBean<Void>(-1, message);
    return jsonBean;
  }

  /**
   * 将kv中的键为is_开头的值为true转为1 
   * @param kv
   */
  @SuppressWarnings("unchecked")
  private void true21(Kv kv) {
    Set<Map.Entry<String, Object>> entrySet = kv.entrySet();
    for (Map.Entry<String, Object> e : entrySet) {
      String key = e.getKey();
      if (key.startsWith("is")) {
        String str = kv.getStr(key);
        // boolean b1 = Boolean.getBoolean(str); //str命名为true,但是返回false
        if ("true".equalsIgnoreCase(str)) {
          kv.put(key, 1);
        } else {
          kv.put(key, 0);
        }
      }
    }
  }

  /**
   * 判断是否为数字
   * @param str
   * @return
   */
  private boolean isNumeric(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
