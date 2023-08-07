package com.litongjava.spring.boot.table.json.controller;


import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/table/json")
@Slf4j
@CrossOrigin
public class TableJsonController {

  @Autowired
  private DbJsonService dbJsonService;

  @RequestMapping("index")
  public String index() {
    return "index";
  }

  @PostMapping("/{f}/create")
  public DbJsonBean<Boolean> create(@PathVariable String f, HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},map:{}", tableName, map);
    Kv kv = KvUtils.camelToUnderscore(map);
    return dbJsonService.saveOrUpdate(tableName, kv);
  }

  @RequestMapping("/{f}/list")
  public DbJsonBean<List<Kv>> list(@PathVariable String f, HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},map:{}", tableName, map);
    Kv kv = KvUtils.camelToUnderscore(map);
//    kv.put("deleted", 0);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.list(tableName, kv));
  }

  @RequestMapping("/{f}/listAll")
  public DbJsonBean<List<Kv>> listAll(@PathVariable String f) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{}", tableName);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.listAll(tableName));
  }

  @RequestMapping("/{f}/page")
  public DbJsonBean<DbPage<Kv>> page(@PathVariable String f, HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},map:{}", tableName, map);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
//    kv.put("deleted", 0);
    log.info("kv:{}", kv);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(tableName, kv));
  }

  @RequestMapping("/{f}/get")
  public DbJsonBean<Kv> get(@PathVariable String f, String id) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},id:{}", tableName, id);
    Kv kv = new Kv();
    // 删除标记
//    kv.put("deleted", 0);
//    log.info("kv:{}", kv);

    return DbJsonBeanUtils.recordToKv(dbJsonService.getById(tableName, id, kv));
  }

  @PutMapping("/{f}/update")
  public DbJsonBean<Boolean> update(@PathVariable String f, HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},map:{}", tableName, map);

    Kv kv = KvUtils.camelToUnderscore(map);
    return dbJsonService.saveOrUpdate(tableName, kv);
  }

  @DeleteMapping("/{f}/delete")
  public DbJsonBean<Integer> delete(@PathVariable String f, String id) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 1);
  }

  @RequestMapping("/{f}/pageDeleted")
  public DbJsonBean<DbPage<Kv>> pageDeleted(@PathVariable String f, HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},map:{}", tableName, map);

    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
//    kv.put("deleted", 1);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(tableName, kv));
  }

  @RequestMapping("/{f}/recover")
  public DbJsonBean<Integer> recover(@PathVariable String f, String id) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 0);
  }

  /**
   * 导出当前数据
   * @param request
   * @param response
   * @throws IOException
   */
  @RequestMapping("/{f}/export-excel")
  public void exportExcel(@PathVariable String f, HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{},map:{}", tableName, map);
    Kv kv = KvUtils.camelToUnderscore(map);
//    kv.put("deleted", 0);
//    log.info("kv:{}", kv);
    String filename = tableName + "_export.xls";

    // 获取数据
    List<Record> records = dbJsonService.list(tableName, kv).getData();
    EesyExcelResponseUtils.exportRecords(response, filename, tableName, records);
  }

  /**
   * 导出所有数据
   * @param f
   * @param response
   * @throws IOException
   * @throws SQLException
   */
  @RequestMapping("/{f}/export-table-excel")
  public void exporAllExcel(@PathVariable String f, HttpServletResponse response) throws IOException, SQLException {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{}", tableName);
    // 导出 Excel
    String filename = tableName + "-all.xlsx";

    // 获取数据
    List<Record> records = dbJsonService.listAll(tableName).getData();

    EesyExcelResponseUtils.exportRecords(response, filename, tableName, records);
    log.info("finished");
  }

  @RequestMapping("/{f}/f-config")
  public DbJsonBean<Map<String, Object>> fConfig(@PathVariable String f, String lang) {
    String tableName = TableNames.getTableName(f);
    log.info("tableName:{}", tableName);
    return dbJsonService.tableConfig(f, tableName, lang);

  }

  @RequestMapping("/f-names")
  public DbJsonBean<String[]> tableNames() throws IOException, SQLException {
    return new DbJsonBean<String[]>(TableNames.getF());
  }

  @RequestMapping("/export-all-table-excel")
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