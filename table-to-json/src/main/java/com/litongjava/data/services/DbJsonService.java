package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.config.DbDataConfig;
import com.litongjava.data.model.DataPageRequest;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.utils.KvUtils;
import com.litongjava.data.utils.SnowflakeIdGenerator;
import com.litongjava.data.utils.UUIDUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbJsonService {
  private DbDataService dbDataService = new DbDataService();

  private PrimaryKeyService primaryKeyService = new PrimaryKeyService();

  private TableColumnService tableColumnService = new TableColumnService();

  /**
   * 分页查询
   *
   * @param tableName
   * @param pageRequest
   * @param quereyParams
   * @return
   */
  public DbJsonBean<Page<Record>> page(String tableName, DataPageRequest pageRequest, Kv quereyParams) {
    String columns = pageRequest.getColumns();
    Integer pageNo = pageRequest.getPageNo();
    Integer pageSize = pageRequest.getPageSize();
    String orderBy = pageRequest.getOrderBy();
    Boolean isAsc = pageRequest.getIsAsc();
    if (StrKit.isBlank(columns)) {
      columns = "*";
    }

    StringBuffer sqlExceptSelect = new StringBuffer();
    List<Object> paramList = dbDataService.sqlExceptSelect(tableName, pageNo, pageSize, orderBy, isAsc, quereyParams,
        sqlExceptSelect);
    Page<Record> listPage = Db.paginate(pageNo, pageSize, "select " + columns, sqlExceptSelect.toString(),
        paramList.toArray());
    return new DbJsonBean<>(listPage);

  }

  public DbJsonBean<Page<Record>> page(Kv kv) {
    String tableName = (String) kv.remove("table_name");
    DataPageRequest dataPageRequest = new DataPageRequest(kv);
    return page(tableName, dataPageRequest, kv);
  }

  public DbJsonBean<Page<Record>> page(String tableName, Kv kv) {
    DataPageRequest dataPageRequest = new DataPageRequest(kv);
    return page(tableName, dataPageRequest, kv);
  }

  public DbJsonBean<Record> getById(String tableName, Kv queryParam) {

    // 拼接sql语句
    StringBuffer sql = new StringBuffer();
    List<Object> paramList = new ArrayList<Object>();

    String sqlTemplate = "select * from %s where %s";
    String format = String.format(sqlTemplate, tableName, dbDataService.getRequireCondition(tableName, paramList));
    sql.append(format);

    // 添加其他查询条件
    paramList = dbDataService.getListWhere(tableName, queryParam, sql);

    // 添加操作表
    Record record = Db.findFirst(sql.toString(), paramList.toArray());
    return new DbJsonBean<Record>(record);
  }

  @SuppressWarnings("unchecked")
  public DbJsonBean<Record> getById(String tableName, Object idValue, Kv kv) {
    // 获取主键名称
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    kv.put(primaryKey, idValue);
    return getById(tableName, kv);
  }

  public DbJsonBean<Boolean> delById(String tableName, Object id) {
    return new DbJsonBean<Boolean>(Db.deleteById(tableName, id));
  }

  public DbJsonBean<Integer> updateFlagById(String tableName, Object id, String delColumn, int flag) {
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    String upateTemplate = "update %s set %s=%s where %s =?";
    int update = Db.update(String.format(upateTemplate, tableName, delColumn, flag, primaryKey), id);
    DbJsonBean<Integer> dataJsonBean = new DbJsonBean<>(update);
    return dataJsonBean;
  }

  public DbJsonBean<Integer> updateIsDelFlagById(String tableName, Object id) {
    // 判断is_del是否存在,如果不存在则创建
    String delFlagColumn = DbDataConfig.getDelColName();
    boolean isExists = tableColumnService.isExists(delFlagColumn, tableName);
    if (!isExists) {
      tableColumnService.addColumn(tableName, delFlagColumn, "int(1) unsigned DEFAULT 0 ", "是否删除,1删除,0未删除");
    }
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    String upateTemplate = "update %s set is_del=1 where  %s =?";
    int update = Db.update(String.format(upateTemplate, tableName, primaryKey), id);
    DbJsonBean<Integer> dataJsonBean = new DbJsonBean<>(update);
    return dataJsonBean;
  }

  public DbJsonBean<Integer> removeByIds(String tableName, Kv kv) {
    @SuppressWarnings("unchecked")
    Set<String> keySet = kv.keySet();
    // 判断size大小
    int size = keySet.size();
    if (size < 1) {
      return null;
    }
    // 获取ids,一共就只有1个
    String key = null;
    for (String string : keySet) {
      key = string;
    }

    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);

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
    int update = Db.update(sql, idValues);
    DbJsonBean<Integer> jsonBean = new DbJsonBean<>(update);
    return jsonBean;
  }

  private String[] getParaValues(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * 判断是否为数字
   *
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

  /**
   * 将kv中的键为is_开头的值为true转为1
   *
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

  @SuppressWarnings("unchecked")
  public DbJsonBean<Boolean> saveOrUpdate(String tableName, Kv kv) {
    KvUtils.removeEmptyValue(kv);
    true21(kv);
    Record record = new Record();
    record.setColumns(kv);

    String primarykeyName = primaryKeyService.getPrimaryKeyName(tableName);
    if (kv.containsKey(primarykeyName) && !StrKit.isBlank(kv.getStr(primarykeyName))) { // 更新
      boolean update = Db.update(tableName, record);
      DbJsonBean<Boolean> dataJsonBean = new DbJsonBean<>(update);
      return dataJsonBean;

    } else { // 保存
      // 如果主键是varchar类型,插入uuid类型
      String primaryKeyColumnType = primaryKeyService.getPrimaryKeyColumnType(tableName);
      if (primaryKeyColumnType.startsWith("varchar")) {
        record.set(primarykeyName, UUIDUtils.random());
      }
      // 如果主键是bigint (20)类型,插入雪花Id
      if ("bigint(20)".equals(primaryKeyColumnType)) {
        record.set(primarykeyName, new SnowflakeIdGenerator(0, 0).generateId());
      }
      boolean save = Db.save(tableName, record);
      DbJsonBean<Boolean> dataJsonBean = new DbJsonBean<>(save);
      return dataJsonBean;
    }
  }

  public DbJsonBean<Boolean> saveOrUpdate(Kv kv) {
    String tableName = (String) kv.remove("table_name");
    return this.saveOrUpdate(tableName, kv);
  }
}
