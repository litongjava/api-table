package com.litongjava.jfinal.table.json.controler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.ext.cors.EnableCORS;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DbPage;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.utils.DbJsonBeanUtils;
import com.litongjava.data.utils.KvUtils;
import com.litongjava.data.utils.RequestParamUtils;
import com.litongjava.spring.boot.table.json.constants.TableNames;
import com.litongjava.spring.boot.table.json.utils.EesyExcelResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Path("/table/json")
@Slf4j
//@CrossOrigi
@EnableCORS
public class TableJsonController extends Controller {

  private DbJsonService dbJsonService = Aop.get(DbJsonService.class);

  @Path("index")
  public String index() {
    return "index";
  }

  @Path("/{f}/create")
  public DbJsonBean<Boolean> create(String f) {
    HttpServletRequest request=getRequest();
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    Kv kv = KvUtils.camelToUnderscore(map);
    log.info("tableName:{},kv:{}", tableName, kv);
    return dbJsonService.saveOrUpdate(tableName, kv);
  }

  @Path("/{f}/list")
  public DbJsonBean<List<Kv>> list(String f) {
    HttpServletRequest request=getRequest();
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    Kv kv = KvUtils.camelToUnderscore(map);
//    kv.put("deleted", 0);
    log.info("tableName:{},kv:{}", tableName, kv);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.list(tableName, kv));
  }

  @Path("/{f}/listAll")
  public DbJsonBean<List<Kv>> listAll(String f) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{}", tableName);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.listAll(tableName));
  }

  @Path("/{f}/page")
  public DbJsonBean<DbPage<Kv>> page(String f) {
    HttpServletRequest request=getRequest();
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
//    kv.put("deleted", 0);
    log.info("tableName:{},kv:{}", tableName, kv);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(tableName, kv));
  }

  @Path("/{f}/get")
  public DbJsonBean<Kv> getOnef(String f) {
    HttpServletRequest request=getRequest();
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除标记
//    kv.put("deleted", 0);
//    log.info("kv:{}", kv);

    log.info("tableName:{},kv:{}", tableName, kv);
    return DbJsonBeanUtils.recordToKv(dbJsonService.get(tableName, kv));
  }

  @Path("/{f}/update")
  public DbJsonBean<Boolean> update(String f) {
    HttpServletRequest request=getRequest();
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);

    Kv kv = KvUtils.camelToUnderscore(map);
    log.info("tableName:{},kv:{}", tableName, kv);

    return dbJsonService.saveOrUpdate(tableName, kv);
  }

  @Path("/{f}/delete")
  public DbJsonBean<Integer> delete(String f, String id) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 1);
  }

  @Path("/{f}/pageDeleted")
  public DbJsonBean<DbPage<Kv>> pageDeleted(String f) {
    HttpServletRequest request=getRequest();
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);

    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
//    kv.put("deleted", 1);
    log.info("tableName:{},kv:{}", tableName, kv);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(tableName, kv));
  }

  @Path("/{f}/recover")
  public DbJsonBean<Integer> recover(String f, String id) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 0);
  }

  /**
   * 导出当前数据
   *
   * @param request
   * @param response
   * @throws IOException
   */
  @Path("/{f}/export-excel")
  public void exportExcel(String f) throws IOException {
    HttpServletRequest request=getRequest();
    HttpServletResponse response = getResponse();
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    Kv kv = KvUtils.camelToUnderscore(map);
//    kv.put("deleted", 0);
//    log.info("kv:{}", kv);
    log.info("tableName:{},kv:{}", tableName, kv);
    String filename = tableName + "_export.xls";

    // 获取数据
    List<Record> records = dbJsonService.list(tableName, kv).getData();
    EesyExcelResponseUtils.exportRecords(response, filename, tableName, records);
  }

  /**
   * 导出所有数据
   *
   * @param response
   * @throws IOException
   * @throws SQLException
   */
  @Path("/{f}/export-table-excel")
  public void exporAllExcel(String f, HttpServletResponse response) throws IOException, SQLException {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{}", tableName);
    // 导出 Excel
    String filename = tableName + "-all.xlsx";

    // 获取数据
    List<Record> records = dbJsonService.listAll(tableName).getData();

    EesyExcelResponseUtils.exportRecords(response, filename, tableName, records);
    log.info("finished");
  }

  @Path("/{f}/f-config")
  public DbJsonBean<Map<String, Object>> fConfig(String f, String lang) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{}", tableName);
    return dbJsonService.tableConfig(f, tableName, lang);

  }

  @Path("/f-names")
  public DbJsonBean<String[]> tableNames() throws IOException, SQLException {
    return new DbJsonBean<String[]>(TableNames.getF());
  }

  @Path("/export-all-table-excel")
  public void exporAllTableExcel(HttpServletResponse response) throws IOException, SQLException {
    String filename = "all-table.xlsx";
    String[] tables = dbJsonService.getAllTableNames();
    int length = tables.length;
    LinkedHashMap<String, List<Record>> allTableData = new LinkedHashMap<>();

    for (int i = 0; i < length; i++) {
      // 获取数据
      List<Record> records = dbJsonService.listAll(tables[i]).getData();
      allTableData.put(tables[i], records);
    }
    EesyExcelResponseUtils.exportAllTableRecords(response, filename, allTableData);
    log.info("finished");
  }
}