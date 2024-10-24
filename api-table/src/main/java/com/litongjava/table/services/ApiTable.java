package com.litongjava.table.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.postgresql.util.PGobject;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.litongjava.db.TableInput;
import com.litongjava.db.TableResult;
import com.litongjava.db.activerecord.Db;
import com.litongjava.db.activerecord.DbPro;
import com.litongjava.db.activerecord.Record;
import com.litongjava.db.activerecord.dialect.Dialect;
import com.litongjava.db.activerecord.dialect.PostgreSqlDialect;
import com.litongjava.db.activerecord.dialect.TdEngineDialect;
import com.litongjava.db.utils.PgVectorUtils;
import com.litongjava.model.page.Page;
import com.litongjava.table.config.DbDataConfig;
import com.litongjava.table.model.DataPageRequest;
import com.litongjava.table.model.DataQueryRequest;
import com.litongjava.table.model.DbTableStruct;
import com.litongjava.table.model.Sql;
import com.litongjava.table.utils.TableInputUtils;
import com.litongjava.tio.utils.UUIDUtils;
import com.litongjava.tio.utils.snowflake.SnowflakeIdGenerator;

public class ApiTable {
  private static DbSqlService dbSqlService = new DbSqlService();

  private static PrimaryKeyService primaryKeyService = new PrimaryKeyService();

  private static TableColumnService tableColumnService = new TableColumnService();

  private static DbTableService dbTableService = new DbTableService();

  private static DbService dbService = new DbService();

  private static Function<String, String> embeddingFun;

  public static void setEmbeddingFun(Function<String, String> embeddingFun) {
    ApiTable.embeddingFun = embeddingFun;
  }

  public static Function<String, String> getEmbeddingFun() {
    return embeddingFun;
  }

  public static TableResult<Kv> saveOrUpdate(String tableName, TableInput kv) {
    String[] jsonFields = TableInputUtils.getJsonFields(kv);
    return saveOrUpdate(tableName, kv, jsonFields);
  }

  public static TableResult<Kv> save(String tableName, TableInput kv) {
    String[] jsonFields = TableInputUtils.getJsonFields(kv);
    return save(tableName, kv, jsonFields);
  }

  /**
   * @param tableName
   * @param kv
   * @param jsonFields
   * @return
   */
  @SuppressWarnings("unchecked")
  public static TableResult<Kv> save(String tableName, TableInput kv, String[] jsonFields) {
    TableInputUtils.removeEmptyValue(kv);
    TableInputUtils.true21(kv);
    Record record = new Record();
    record.setColumns(kv);

    String primaryKeyName = primaryKeyService.getPrimaryKeyName(tableName);
    if (kv.get(primaryKeyName) == null) {
      // 如果主键是varchar类型,插入uuid类型
      String primaryKeyColumnType = primaryKeyService.getPrimaryKeyColumnType(tableName);
      if (!StrKit.isBlank(primaryKeyColumnType)) {
        if (primaryKeyColumnType.startsWith("varchar")) {
          String id = UUIDUtils.random();
          record.set(primaryKeyName, id);
        } else if (primaryKeyColumnType.startsWith("bigint") || primaryKeyColumnType.startsWith("long")) {
          // 如果主键是bigint (20)类型,插入雪花Id
          long threadId = Thread.currentThread().getId();
          if (threadId > 31) {
            threadId = threadId % 31;
          }
          if (threadId < 0) {
            threadId = 0;
          }
          long id = new SnowflakeIdGenerator(threadId, 0).generateId();
          record.set(primaryKeyName, id);
        }
      }
    }

    boolean save = Db.save(tableName, primaryKeyName, record, jsonFields);
    if (save) {
      Kv by = Kv.by(primaryKeyName, record.getObject(primaryKeyName));
      return new TableResult<>(by);
    } else {
      return TableResult.fail("save fail");
    }

  }

  @SuppressWarnings("unchecked")
  public static TableResult<Kv> saveOrUpdate(String tableName, TableInput kv, String[] jsonFields) {
    // KvUtils.removeEmptyValue(kv);
    TableInputUtils.true21(kv);
    Map<String, String> embeddingMap = TableInputUtils.getEmbeddingMap(kv);
    Record record = new Record();
    record.setColumns(kv);

    Set<Entry<String, String>> set = embeddingMap.entrySet();
    if (embeddingFun != null) {
      for (Entry<String, String> e : set) {
        String key = e.getKey();
        String textValue = kv.getStr(key);
        if (textValue != null && Db.use().getConfig().getDialect() instanceof PostgreSqlDialect) {
          String embeddingArrayString = embeddingFun.apply(textValue);
          PGobject pgVector = PgVectorUtils.getPgVector(embeddingArrayString);
          record.set(e.getValue(), pgVector);
        }
      }
    }

    String primaryKeyName = primaryKeyService.getPrimaryKeyName(tableName);
    if (kv.containsKey(primaryKeyName)) { // update
      String idValue = record.getStr(primaryKeyName);

      if (!StrKit.isBlank(idValue)) {
        boolean update = update(tableName, primaryKeyName, idValue, record, jsonFields);
        if (update) {
          return new TableResult<>();
        } else {
          return TableResult.fail();
        }
      } else {
        Object id = getIdValueByType(tableName);
        if (id != null) {
          record.set(primaryKeyName, id);
        }
        boolean save = Db.save(tableName, record, jsonFields);
        if (save) {
          Kv by = Kv.by(primaryKeyName, record.getObject(primaryKeyName));
          return new TableResult<>(by);
        } else {
          return TableResult.fail("save fail");
        }
      }

    } else { // 保存
      boolean save = save(tableName, jsonFields, primaryKeyName, record);
      if (save) {
        Kv by = Kv.by(primaryKeyName, record.getObject(primaryKeyName));
        return new TableResult<>(by);
      } else {
        return TableResult.fail("save fail");
      }
    }
  }

  public static boolean save(String tableName, String[] jsonFields, String primaryKeyName, Record record) {
    // 如果主键是varchar类型,插入uuid类型 不处理uuid类型.如果是uuid类型,让数据库自动生成
    Object id = getIdValueByType(tableName);
    if (id != null) {
      record.set(primaryKeyName, id);
    }
    return Db.save(tableName, record, jsonFields);
  }

  public static Object getIdValueByType(String tableName) {
    String primaryKeyColumnType = primaryKeyService.getPrimaryKeyColumnType(tableName).toLowerCase();
    if (!StrKit.isBlank(primaryKeyColumnType)) {
      if (primaryKeyColumnType.startsWith("varchar") || primaryKeyColumnType.startsWith("text")) {
        return UUIDUtils.random();
      } else if (primaryKeyColumnType.startsWith("bigint") || primaryKeyColumnType.startsWith("long")) {
        // 如果主键是bigint (20)类型,插入雪花Id
        long threadId = Thread.currentThread().getId();
        if (threadId > 31) {
          threadId = threadId % 31;
        }
        if (threadId < 0) {
          threadId = 0;
        }
        return new SnowflakeIdGenerator(threadId, 0).generateId();
      }
    }
    return null;
  }

  public static boolean update(String tableName, String primaryKeyName, String idValue, Record record, String[] jsonFields) {
    String primaryKeyColumnType = primaryKeyService.getPrimaryKeyColumnType(tableName);

    boolean update = false;
    if ("uuid".equals(primaryKeyColumnType)) {
      UUID idUUID = UUID.fromString(idValue);
      record.set(primaryKeyName, idUUID);
      update = Db.update(tableName, primaryKeyName, record, jsonFields);
    } else {
      update = Db.update(tableName, primaryKeyName, record, jsonFields);
    }
    return update;
  }

  public static boolean update(String tableName, String primaryKeyName, Object idValue, Record record) {
    String primaryKeyColumnType = primaryKeyService.getPrimaryKeyColumnType(tableName);

    boolean update = false;
    if ("uuid".equals(primaryKeyColumnType)) {
      UUID idUUID = UUID.fromString((String) idValue);
      record.set(primaryKeyName, idUUID);
      update = Db.update(tableName, primaryKeyName, record);
    } else {
      record.set(primaryKeyName, idValue);
      update = Db.update(tableName, primaryKeyName, record);
    }
    return update;
  }

  public static TableResult<Kv> batchUpdateByIds(String f, TableInput kv) {
    DbPro dbPro = Db.use();
    return batchUpdateByIds(dbPro, f, kv);
  }

  public static TableResult<Kv> batchUpdateByIds(DbPro dbPro, String tableName, TableInput kv) {
    Object[] ids = kv.getAs("ids", new Object[0]);
    kv.remove("ids");
    DbTableStruct primaryKey = primaryKeyService.getPrimaryKey(dbPro, tableName);
    String primaryKeyName = primaryKey.getField();
    String type = primaryKey.getType();
    boolean isUuid = false;
    if (type != null && type.startsWith("uuid")) {
      isUuid = true;
    }
    List<Record> lists = new ArrayList<>();
    for (Object id : ids) {
      Record record = new Record();
      record.setColumns(kv.toMap());
      if (isUuid) {
        record.set(primaryKeyName, UUID.fromString((String) id));
      } else {
        record.set(primaryKeyName, id);
      }

      lists.add(record);
    }
    int[] results = dbPro.batchUpdate(tableName, lists, lists.size());
    return new TableResult<>(Kv.by("data", results));
  }

  public static TableResult<Kv> saveOrUpdate(TableInput kv) {
    String tableName = (String) kv.remove("table_name");
    return saveOrUpdate(tableName, kv);
  }

  /**
   * @param tableName
   * @return
   */
  public static TableResult<List<Record>> listAll(String tableName) {
    DbPro dbPro = Db.use();
    return listAll(dbPro, tableName);
  }

  public static TableResult<List<Record>> listAll(String f, TableInput kv) {
    DbPro dbPro = Db.use();
    return listAll(dbPro, f, kv);
  }

  /**
   * 无任何条件过滤,包含所有数据
   *
   * @param dbPro
   * @param tableName
   * @return
   */
  public static TableResult<List<Record>> listAll(DbPro dbPro, String tableName) {
    List<Record> records = dbPro.find("select * from " + tableName);
    if (records.size() < 1) {
      List<DbTableStruct> columns = dbService.getTableStruct(dbPro, tableName);
      Record record = new Record();
      for (DbTableStruct struct : columns) {
        record.set(struct.getField(), null);
      }
      records.add(record);
    }
    return new TableResult<List<Record>>(records);
  }

  public static TableResult<List<Record>> listAll(DbPro dbPro, String tableName, TableInput kv) {
    String[] jsonFields = TableInputUtils.getJsonFields(kv);
    String sql = "select * from " + tableName;
    List<Record> records = null;
    if (jsonFields != null) {
      records = dbPro.findWithJsonField(sql, jsonFields);
    } else {
      records = dbPro.find(sql);
    }
    if (records.size() < 1) {
      List<DbTableStruct> columns = dbService.getTableStruct(dbPro, tableName);
      Record record = new Record();
      for (DbTableStruct struct : columns) {
        record.set(struct.getField(), null);
      }
      records.add(record);
    }
    return new TableResult<List<Record>>(records);
  }

  /**
   * @param tableName
   * @param queryParam
   * @return
   */
  public static TableResult<List<Record>> list(String tableName, TableInput kvBean) {
    return list(null, tableName, kvBean);
  }

  /**
   * @param dbPro
   * @param tableName
   * @param queryParam
   * @return
   */
  public static TableResult<List<Record>> list(DbPro dbPro, String tableName, TableInput kvBean) {
    if (dbPro == null) {
      dbPro = Db.use();
    }
    DataQueryRequest queryRequest = new DataQueryRequest(kvBean);
    String[] jsonFields = TableInputUtils.getJsonFields(kvBean);

    // 添加其他查询条件
    Sql sql = dbSqlService.getWhereClause(queryRequest, kvBean);
    sql.setColumns(queryRequest.getColumns());
    sql.setTableName(tableName);

    List<Object> params = sql.getParams();

    // 添加操作表
    List<Record> list = null;
    if (params == null) {
      if (jsonFields != null) {
        list = dbPro.findWithJsonFields(sql.getsql(), jsonFields);
      } else {
        list = dbPro.find(sql.getsql());
      }

    } else {
      if (jsonFields != null) {
        list = dbPro.findWithJsonField(sql.getsql(), jsonFields, sql.getParams().toArray());
      } else {
        list = dbPro.find(sql.getsql(), sql.getParams().toArray());
      }

    }
    return new TableResult<>(list);
  }

  /**
   * @param kv
   * @return
   */
  public static TableResult<Page<Record>> page(TableInput kv) {
    String tableName = (String) kv.remove(TableInput.table_name);
    DataPageRequest dataPageRequest = new DataPageRequest(kv);
    return page(tableName, dataPageRequest, kv);
  }

  /**
   * @return
   */
  public static TableResult<Page<Record>> page(DbPro dbPro, TableInput kv) {
    String tableName = (String) kv.remove(TableInput.table_name);
    DataPageRequest dataPageRequest = new DataPageRequest(kv);
    return page(dbPro, tableName, dataPageRequest, kv);
  }

  /**
   * @param tableName
   * @param kv
   * @return
   */
  public static TableResult<Page<Record>> page(String tableName, TableInput kv) {
    DataPageRequest dataPageRequest = new DataPageRequest(kv);
    return page(tableName, dataPageRequest, kv);
  }

  /**
   * @param tableName
   * @return
   */
  public static TableResult<Page<Record>> page(String tableName) {
    TableInput kv = TableInput.create();
    DataPageRequest dataPageRequest = new DataPageRequest(kv);
    return page(tableName, dataPageRequest, kv);
  }

  /**
   * @param dbPro
   * @param f
   * @param kv
   * @return
   */
  public static TableResult<Page<Record>> page(DbPro dbPro, String f, TableInput kv) {
    kv.remove(TableInput.table_name);
    DataPageRequest dataPageRequest = new DataPageRequest(kv);
    return page(dbPro, f, dataPageRequest, kv);
  }

  /**
   * @param tableName
   * @param dataPageRequest
   * @param kv
   * @return
   */
  public static TableResult<Page<Record>> page(String tableName, DataPageRequest dataPageRequest, TableInput kv) {
    return page(null, tableName, dataPageRequest, kv);
  }

  /**
   * 分页查询
   *
   * @param dbPro
   * @param tableName
   * @param pageRequest
   * @param para
   * @return
   */
  public static TableResult<Page<Record>> page(DbPro dbPro, String tableName, DataPageRequest pageRequest, TableInput para) {
    if (dbPro == null) {
      dbPro = Db.use();
    }
    // process for primary key is uuid
    Dialect dialect = dbPro.getConfig().getDialect();
    if (dialect instanceof TdEngineDialect) {

    } else {
      DbTableStruct primaryKey = primaryKeyService.getPrimaryKey(dbPro, tableName);
      String primaryKeyName = primaryKey.getField();
      Object idValue = para.get(primaryKeyName);
      if (idValue != null) {
        if ("uuid".equals(primaryKey.getType())) {
          UUID idUUID = UUID.fromString((String) idValue);
          para.set(primaryKeyName, idUUID);
        }
      }
    }

    Integer pageNo = pageRequest.getPageNo();
    Integer pageSize = pageRequest.getPageSize();

    DataQueryRequest queryRequest = new DataQueryRequest(para);
    String[] jsonFields = TableInputUtils.getJsonFields(para);

    Sql sql = dbSqlService.getWhereClause(queryRequest, para);
    sql.setTableName(tableName);
    sql.setColumns(queryRequest.getColumns());

    List<Object> params = sql.getParams();

    String sqlExceptSelect = sql.getSqlExceptSelect();
    Page<Record> listPage = null;
    if (params == null) {
      if (jsonFields != null && jsonFields.length > 0) {
        listPage = dbPro.paginateJsonFields(pageNo, pageSize, sql.getSelectColumns(), sqlExceptSelect, jsonFields);
      } else {
        listPage = dbPro.paginate(pageNo, pageSize, sql.getSelectColumns(), sqlExceptSelect);
      }
    } else {
      if (jsonFields != null && jsonFields.length > 0) {
        listPage = dbPro.paginateJsonFields(pageNo, pageSize, sql.getSelectColumns(), sqlExceptSelect, jsonFields, params.toArray());
      } else {
        listPage = dbPro.paginate(pageNo, pageSize, sql.getSelectColumns(), sqlExceptSelect, params.toArray());
      }

    }
    return new TableResult<>(listPage);

  }

  /**
   * @param dbPro
   * @param tableName
   * @param queryParam
   * @return
   */
  public static TableResult<Record> get(String tableName, TableInput kvBean) {
    return get(null, tableName, kvBean);
  }

  public static TableResult<Record> get(DbPro dbPro, String tableName, TableInput kvBean) {
    if (dbPro == null) {
      dbPro = Db.use();
    }

    DataQueryRequest queryRequest = new DataQueryRequest(kvBean);
    String[] jsonFields = TableInputUtils.getJsonFields(kvBean);

    // 添加其他查询条件
    Sql sql = dbSqlService.getWhereClause(queryRequest, kvBean);
    sql.setTableName(tableName);
    sql.setColumns(queryRequest.getColumns());

    // 添加操作表
    Record record = null;
    List<Object> params = sql.getParams();

    if (jsonFields != null && jsonFields.length > 0) {

      if (params == null) {
        record = dbPro.findFirstJsonField(sql.getsql(), jsonFields);
      } else {
        record = dbPro.findFirstJsonField(sql.getsql(), jsonFields, params.toArray());
      }

    } else {
      if (params == null) {
        record = dbPro.findFirst(sql.getsql());
      } else {
        record = dbPro.findFirst(sql.getsql(), params.toArray());
      }
    }

    return new TableResult<Record>(record);
  }

  @SuppressWarnings("unchecked")
  public static TableResult<Record> getById(String tableName, Object idValue, TableInput kvBean) {
    // 获取主键名称
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    kvBean.put(primaryKey, idValue);
    return get(tableName, kvBean);
  }

  public static TableResult<Record> getById(String tableName, Object idValue) {
    // 获取主键名称
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    TableInput kvBean = new TableInput();
    kvBean.set(primaryKey, idValue);
    return get(null, tableName, kvBean);
  }

  public static TableResult<Boolean> delById(String tableName, Object id) {
    if (Db.deleteById(tableName, id)) {
      return new TableResult<Boolean>();
    } else {
      return TableResult.fail();
    }

  }

  public static TableResult<Boolean> updateFlagById(String tableName, Object id, String delColumn, int flag) {
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    String updateSqlTemplate = "update %s set %s=%s where %s =?";
    String sql = String.format(updateSqlTemplate, tableName, delColumn, flag, primaryKey);
    int updateResult = Db.updateBySql(sql, id);
    if (updateResult > 0) {
      return new TableResult<>();
    } else {
      return TableResult.fail(-1, "update fail");
    }
  }

  public static TableResult<Boolean> updateFlagByIdAndUserId(String tableName, Object id, String delColumn, int flag, String userIdColumn, String userId) {
    // 获取主键名
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);

    // 构建更新SQL模板，使用占位符
    String updateSqlTemplate = "UPDATE `%s` SET `%s` = ? WHERE `%s` = ? AND `%s` = ?";

    // 格式化SQL语句
    String sql = String.format(updateSqlTemplate, tableName, delColumn, primaryKey, userIdColumn);

    // 执行更新操作
    int updateResult = Db.updateBySql(sql, flag, id, userId);

    // 根据更新结果返回相应的 DbJsonBean 实例
    if (updateResult > 0) {
      return new TableResult<>(true); // 返回成功状态
    } else {
      return TableResult.fail(-1, "update fail"); // 返回失败信息
    }
  }

  public static TableResult<Boolean> updateIsDelFlagById(String tableName, Object id) {
    // 判断is_del是否存在,如果不存在则创建
    String delFlagColumn = DbDataConfig.getDelColName();
    boolean isExists = tableColumnService.isExists(delFlagColumn, tableName);
    if (!isExists) {
      tableColumnService.addColumn(tableName, delFlagColumn, "int(1) unsigned DEFAULT 0 ", "是否删除,1删除,0未删除");
    }
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    String upateTemplate = "update %s set is_del=1 where  %s =?";
    String sql = String.format(upateTemplate, tableName, primaryKey);
    int updateResult = Db.updateBySql(sql, id);
    if (updateResult > 0) {
      return new TableResult<>();
    } else {
      return TableResult.fail(-1, "update fail");
    }
  }

  public static TableResult<Integer> removeByIds(String tableName, Kv kv) {
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
    Db.updateBySql(sql, idValues);
    return new TableResult<>();
  }

  private static String[] getParaValues(String key) {
    return null;
  }

  /**
   * 判断是否为数字
   *
   * @param str
   * @return
   */
  private static boolean isNumeric(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * 获取所有表
   *
   * @return
   */
  public static String[] getAllTableNames() {
    return dbService.tableNames();
  }

  public static TableResult<String[]> tableNames() {
    String[] allTableNames = dbService.tableNames();
    return new TableResult<>(allTableNames);
  }

  /**
   * @param f         from
   * @param tableName table name
   * @param lang      language
   * @return
   */
  public static TableResult<Map<String, Object>> tableConfig(String f, String tableName, String lang) {
    if (StrKit.isBlank(tableName)) {
      return new TableResult<>(-1, "tableName can't be empty");
    }
    return new TableResult<>(dbTableService.getTableConfig(f, tableName, lang));
  }

  public static TableResult<List<Record>> query(String sql) {
    List<Record> find = Db.find(sql);
    return new TableResult<>(find);
  }

  public static TableResult<List<Record>> query(String sql, Object... paras) {
    List<Record> find = null;
    if (paras == null || paras.length < 1) {
      find = Db.find(sql);
    } else {
      find = Db.find(sql, paras);
    }
    return new TableResult<>(find);
  }

  /**
   * Ant design procomponents proTableColumns
   */
  public static TableResult<List<Map<String, Object>>> columns(String f) {
    if (StrKit.isBlank(f)) {
      return new TableResult<>(-1, "tableName can't be empty");
    }
    return new TableResult<>(dbTableService.columns(f));
  }

  public static String queryStr(String tableName, Object idValue, TableInput ti) {
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    ti.set(primaryKey, idValue);
    return queryStr(tableName, ti);
  }

  public static String queryStr(String tableName, TableInput ti) {
    return query(null, tableName, ti);
  }

  public static Long queryLong(String tableName, Object idValue, TableInput ti) {
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    ti.set(primaryKey, idValue);
    return queryLong(tableName, ti);
  }

  public static Long queryLong(String tableName, TableInput ti) {
    return query(null, tableName, ti);
  }

  public static PGobject queryPGobject(String tableName, Object idValue, TableInput ti) {
    String primaryKey = primaryKeyService.getPrimaryKeyName(tableName);
    ti.set(primaryKey, idValue);
    return queryPGobject(tableName, ti);
  }

  public static PGobject queryPGobject(String tableName, TableInput ti) {
    return query(null, tableName, ti);
  }

  public static <T> T query(String tableName, TableInput ti) {
    return query(Db.use(), tableName, ti);
  }

  public static <T> T query(DbPro dbPro, String tableName, TableInput kvBean) {
    if (dbPro == null) {
      dbPro = Db.use();
    }
    DataQueryRequest queryRequest = new DataQueryRequest(kvBean);

    // 添加其他查询条件
    Sql sql = dbSqlService.getWhereClause(queryRequest, kvBean);
    sql.setTableName(tableName);
    sql.setColumns(queryRequest.getColumns());

    List<Object> params = sql.getParams();
    if (params == null) {
      return Db.queryFirst(sql.getsql());
    } else {
      return Db.queryFirst(sql.getsql(), params.toArray());
    }
  }

}
