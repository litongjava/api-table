package com.litongjava.tio.boot.table.to.json.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.Kv;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DbPage;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.utils.DbJsonBeanUtils;
import com.litongjava.data.utils.KvUtils;
import com.litongjava.data.utils.TioRequestParamUtils;
import com.litongjava.jfinal.aop.annotation.AAutowired;
import com.litongjava.jfinal.plugin.activerecord.Record;
import com.litongjava.tio.boot.table.to.json.utils.EesyExcelResponseUtils;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.server.annotation.EnableCORS;
import com.litongjava.tio.http.server.annotation.RequestPath;

import lombok.extern.slf4j.Slf4j;

@RequestPath("/table/json")
@Slf4j
@EnableCORS
public class TableJsonController {

  @AAutowired
  private DbJsonService dbJsonService;

  @RequestPath("/index")
  public String index() {
    return "TableJsonController";
  }

  @RequestPath("/{f}/create")
  public DbJsonBean<Boolean> create(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Kv kv = KvUtils.camelToUnderscore(map);
    log.info("tableName:{},kv:{}", f, kv);
    return dbJsonService.saveOrUpdate(f, kv);
  }

  @RequestPath("/{f}/list")
  public DbJsonBean<List<Kv>> list(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Kv kv = KvUtils.camelToUnderscore(map);
//    kv.put("deleted", 0);
    log.info("tableName:{},kv:{}", f, kv);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.list(f, kv));
  }

  @RequestPath("/{f}/listAll")
  public DbJsonBean<List<Kv>> listAll(String f) {
    log.info("tableName:{}", f);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.listAll(f));
  }

  @RequestPath("/{f}/page")
  public DbJsonBean<DbPage<Kv>> page(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
//    kv.put("deleted", 0);
    log.info("tableName:{},kv:{}", f, kv);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(f, kv));
  }

  @RequestPath("/{f}/get")
  public DbJsonBean<Kv> get(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除标记
//    kv.put("deleted", 0);
//    log.info("kv:{}", kv);

    log.info("tableName:{},kv:{}", f, kv);
    return DbJsonBeanUtils.recordToKv(dbJsonService.get(f, kv));
  }

  @RequestPath("/{f}/update")
  public DbJsonBean<Boolean> update(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Kv kv = KvUtils.camelToUnderscore(map);
    log.info("tableName:{},kv:{}", f, kv);

    return dbJsonService.saveOrUpdate(f, kv);
  }

  @RequestPath("/{f}/delete")
  public DbJsonBean<Integer> delete(String f, String id) {
    log.info("tableName:{},id:{}", f, id);
    return dbJsonService.updateFlagById(f, id, "deleted", 1);
  }

  @RequestPath("/{f}/pageDeleted")
  public DbJsonBean<DbPage<Kv>> pageDeleted(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
//    kv.put("deleted", 1);
    log.info("tableName:{},kv:{}", f, kv);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(f, kv));
  }

  @RequestPath("/{f}/recover")
  public DbJsonBean<Integer> recover(String f, String id) {
    log.info("tableName:{},id:{}", f, id);
    return dbJsonService.updateFlagById(f, id, "deleted", 0);
  }

  /**
   * 导出当前数据
   *
   * @param request
   * @param response
   * @throws IOException
   */
  @RequestPath("/{f}/export-excel")
  public HttpResponse exportExcel(String f, HttpRequest request) throws IOException {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Kv kv = KvUtils.camelToUnderscore(map);
//    kv.put("deleted", 0);
//    log.info("kv:{}", kv);
    log.info("tableName:{},kv:{}", f, kv);
    String filename = f + "_export.xls";

    // 获取数据
    List<Record> records = dbJsonService.list(f, kv).getData();
    return EesyExcelResponseUtils.exportRecords(request, filename, f, records);
  }

  /**
   * 导出所有数据
   *
   * @param response
   * @throws IOException
   * @throws SQLException
   */
  @RequestPath("/{f}/export-table-excel")
  public HttpResponse exporAllExcel(String f, HttpRequest request) throws IOException, SQLException {
    log.info("tableName:{}", f);
    // 导出 Excel
    String filename = f + "-all.xlsx";

    // 获取数据
    List<Record> records = dbJsonService.listAll(f).getData();

    HttpResponse response = EesyExcelResponseUtils.exportRecords(request, filename, f, records);
    log.info("finished");
    return response;
  }

  @RequestPath("/{f}/f-config")
  public DbJsonBean<Map<String, Object>> fConfig(String f, String lang) {
    log.info("tableName:{}", f);
    return dbJsonService.tableConfig(f, f, lang);

  }

  @RequestPath("/f-names")
  public DbJsonBean<String[]> tableNames() throws IOException, SQLException {
    return new DbJsonBean<String[]>(dbJsonService.tableNames().getData());
  }

  @RequestPath("/export-all-table-excel")
  public HttpResponse exporAllTableExcel(HttpRequest request) throws IOException, SQLException {
    String filename = "all-table.xlsx";
    String[] tables = dbJsonService.getAllTableNames();
    int length = tables.length;
    LinkedHashMap<String, List<Record>> allTableData = new LinkedHashMap<>();

    for (int i = 0; i < length; i++) {
      // 获取数据
      List<Record> records = dbJsonService.listAll(tables[i]).getData();
      allTableData.put(tables[i], records);
    }
    HttpResponse httpResponse = EesyExcelResponseUtils.exportAllTableRecords(request, filename, allTableData);
    log.info("finished");
    return httpResponse;
  }
}