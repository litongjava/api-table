package com.litongjava.data.services;

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
import com.litongjava.data.model.DataQueryRequest;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.Sql;
import com.litongjava.data.utils.KvUtils;
import com.litongjava.data.utils.SnowflakeIdGenerator;
import com.litongjava.data.utils.UUIDUtils;

public class DbJsonService {
  private DbSqlService dbSqlService = new DbSqlService();

  private PrimaryKeyService primaryKeyService = new PrimaryKeyService();

  private TableColumnService tableColumnService = new TableColumnService();

  private DbTableService dbTableService = new DbTableService();

  private DbService dbService = new DbService();

  @SuppressWarnings("unchecked")
  public DbJsonBean<Boolean> saveOrUpdate(String tableName, Kv kv) {

    KvUtils.removeEmptyValue(kv);
    true21(kv);
    Record record = new Record();
    record.setColumns(kv);

    String primarykeyName = primaryKeyService.getPrimaryKeyName(tableName);
    if (kv.containsKey(primarykeyName)) { // 更新
      String idValue = kv.getStr(primarykeyName);
      if (!StrKit.isBlank(idValue)) {
        boolean update = Db.update(tableName, record);
        System.out.println("update result:" + update);
        DbJsonBean<Boolean> dataJsonBean = new DbJsonBean<>(update);
        return dataJsonBean;
      } else {
        return new DbJsonBean<>(-1, "id value can't be null");
      }
    } else { // 保存
      // 如果主键是varchar类型,插入uuid类型
      String primaryKeyColumnType = primaryKeyService.getPrimaryKeyColumnType(tableName);
      if (primaryKeyColumnType.startsWith("varchar")) {
        record.set(primarykeyName, UUIDUtils.random());
      }
      // 如果主键是bigint (20)类型,插入雪花Id
      if (!StrKit.isBlank(primaryKeyColumnType)) {
        if (primaryKeyColumnType.startsWith("bigint")) {
          long threadId = Thread.currentThread().getId();
          if (threadId > 31) {
            threadId = threadId % 31;
          }
          if (threadId < 0) {
            threadId = 0;
          }
          long generateId = new SnowflakeIdGenerator(threadId, 0).generateId();
          record.set(primarykeyName, generateId);
        }
      }

      boolean save = Db.save(tableName, record);
      System.out.println("save result:" + save);
      return new DbJsonBean<>(save);
    }
  }

  public DbJsonBean<Boolean> saveOrUpdate(Kv kv) {
    String tableName = (String) kv.remove("table_name");
    return this.saveOrUpdate(tableName, kv);
  }

  /**
   * 无任何条件过滤,包含所有数据
   *
   * @param kv
   * @return
   */
  public DbJsonBean<List<Record>> listAll(String tableName) {
    return new DbJsonBean<List<Record>>(Db.find("select * from " + tableName));
  }

  public DbJsonBean<List<Record>> list(Kv kv) {
    String tableName = (String) kv.remove("table_name");
    return list(tableName, kv);
  }

  public DbJsonBean<List<Record>> list(String tableName, Kv queryParam) {
    DataQueryRequest queryRequest = new DataQueryRequest(queryParam);
    // 添加其他查询条件
    Sql sql = dbSqlService.getWhereClause(queryRequest, queryParam);
    sql.setColumns(queryRequest.getColumns());
    sql.setTableName(tableName);

    List<Object> params = sql.getParams();

    System.out.println("sql:" + sql.getsql());
    System.out.println(params);
    // 添加操作表
    List<Record> list = null;
    if (params == null) {
      list = Db.find(sql.getsql());
    } else {
      list = Db.find(sql.getsql(), sql.getParams().toArray());
    }
    return new DbJsonBean<>(list);
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

  /**
   * 分页查询
   *
   * @param tableName
   * @param pageRequest
   * @param queryParam
   * @return
   */
  public DbJsonBean<Page<Record>> page(String tableName, DataPageRequest pageRequest, Kv queryParam) {
    Integer pageNo = pageRequest.getPageNo();
    Integer pageSize = pageRequest.getPageSize();

    DataQueryRequest queryRequest = new DataQueryRequest(queryParam);

    Sql sql = dbSqlService.getWhereClause(queryRequest, queryParam);
    sql.setTableName(tableName);
    sql.setColumns(queryRequest.getColumns());

    System.out.println("sql:" + sql.getsql());
    List<Object> params = sql.getParams();
    System.out.println(params);
    System.out.println(pageNo + " " + pageSize);

    String sqlExceptSelect = sql.getSqlExceptSelect();
    Page<Record> listPage = null;
    if (params == null) {
      listPage = Db.paginate(pageNo, pageSize, sql.getSelectColumns(), sqlExceptSelect);
    } else {
      listPage = Db.paginate(pageNo, pageSize, sql.getSelectColumns(), sqlExceptSelect, params.toArray());
    }
    return new DbJsonBean<>(listPage);

  }

  /**
   * 因为 需要需要获取Id,是否删除,租户id等.所以使用了queryParam
   * @param tableName
   * @param queryParam
   * @return
   */
  public DbJsonBean<Record> getById(String tableName, Kv queryParam) {
    String columns = queryParam.getStr("cloumns");

    // 添加其他查询条件
    Sql sql = dbSqlService.getWhereQueryClause(queryParam);
    sql.setColumns(columns);
    sql.setTableName(tableName);

    System.out.println("sql:" + sql.getsql());
    System.out.println(sql.getParams());

    // 添加操作表
    Record record = Db.findFirst(sql.getsql(), sql.getParams().toArray());
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
    String sql = String.format(upateTemplate, tableName, delColumn, flag, primaryKey);

    System.out.println("sql:" + sql.toString());
    System.out.println(id);

    return new DbJsonBean<>(Db.update(sql, id));
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
    String sql = String.format(upateTemplate, tableName, primaryKey);

    System.out.println("sql:" + sql.toString());
    System.out.println(id);

    return new DbJsonBean<>(Db.update(sql, id));
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

    System.out.println("sql:" + sql.toString());
    System.out.println(idValues);

    return new DbJsonBean<>(Db.update(sql, idValues));
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

  /**
   * 获取所有表
   * @return
   */
  public String[] getAllTableNames() {
    return dbTableService.getAllTableNames();
  }

  public DbJsonBean<String[]> tableNames() {
    String[] allTableNames = dbTableService.getAllTableNames();
    DbJsonBean<String[]> dbJsonBean = new DbJsonBean<>();
    dbJsonBean.setData(allTableNames);
    return dbJsonBean;
  }

  public DbJsonBean<List<Record>> tables() {
    return new DbJsonBean<>(dbService.tables());
  }


  public DbJsonBean<Map<String,Object>> tableConfig(String tableName, String lang) {
    return new DbJsonBean<>(dbTableService.getTableConfig(tableName,lang));
  }

}
