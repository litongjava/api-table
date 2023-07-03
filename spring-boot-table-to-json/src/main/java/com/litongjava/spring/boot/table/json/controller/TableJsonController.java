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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DbPage;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.utils.DbJsonBeanUtils;
import com.litongjava.data.utils.KvUtils;
import com.litongjava.data.utils.RequestMapUtils;
import com.litongjava.data.vo.DateTimeReqVo;
import com.litongjava.spring.boot.table.json.utils.EesyExcelResponseUtils;
import com.litongjava.spring.boot.table.json.utils.RequestParamUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/table/json")
@Slf4j
@CrossOrigin
@SuppressWarnings("unchecked")
public class TableJsonController {

  @Autowired
  private DbJsonService dbJsonService;

  @RequestMapping("index")
  public String index() {
    return "index";
  }

  @PostMapping("/create")
  public DbJsonBean<Boolean> create(HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    log.info("map:{}", map);
    Kv kv = KvUtils.camelToUnderscore(map);
    return dbJsonService.saveOrUpdate(kv);
  }

  @RequestMapping("/list")
  public DbJsonBean<List<Kv>> list(HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    log.info("map:{}", map);
    Kv kv = KvUtils.camelToUnderscore(map);
    kv.put("deleted", 0);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.list(kv));
  }

  @RequestMapping("/query")
  public DbJsonBean<List<Kv>> query(String sql) {
    log.info("sql:{}", sql);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.query(sql));
  }

  @RequestMapping("/listAll")
  public DbJsonBean<List<Kv>> listAll(String tableName) {
    log.info("tableName:{}", tableName);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.listAll(tableName));
  }

  @RequestMapping("page")
  public DbJsonBean<DbPage<Kv>> page(@RequestParam Map<String, Object> map, DateTimeReqVo reqVo) {
    RequestMapUtils.putEntityToMap(map, reqVo);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
    kv.put("deleted", 0);
    log.info("kv:{}", kv);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(kv));
  }

  @RequestMapping("/get")
  public DbJsonBean<Kv> get(String tableName, String id) {
    log.info("tableName:{},id:{}", tableName, id);
    Kv kv = new Kv();
    // 删除标记
    kv.put("deleted", 0);
    log.info("kv:{}", kv);

    return DbJsonBeanUtils.recordToKv(dbJsonService.getById(tableName, id, kv));
  }

  @PutMapping("/update")
  public DbJsonBean<Boolean> update(HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    Kv kv = KvUtils.camelToUnderscore(map);
    log.info("map:{}", map);
    return dbJsonService.saveOrUpdate(kv);
  }

  @DeleteMapping("/delete")
  public DbJsonBean<Integer> delete(String tableName, String id) {
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 1);
  }

  @RequestMapping("pageDeleted")
  public DbJsonBean<DbPage<Kv>> pageDeleted(HttpServletRequest request) {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    log.info("map:{}", map);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
    kv.put("deleted", 1);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(kv));
  }

  @RequestMapping("/recover")
  public DbJsonBean<Integer> recover(String tableName, String id) {
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 0);
  }

  @RequestMapping("/export-excel")
  public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, Object> map = RequestParamUtils.getRequestMap(request);
    Kv kv = KvUtils.camelToUnderscore(map);
    kv.put("deleted", 0);
    log.info("kv:{}", kv);
    String tableName = kv.getStr("table_name");
    String filename = tableName + "_export.xls";

    // 获取数据
    List<Record> records = dbJsonService.list(kv).getData();
    EesyExcelResponseUtils.exportRecords(response, filename, tableName, records);
  }

  @RequestMapping("/export-table-excel")
  public void exporAllExcel(String tableName, HttpServletResponse response) throws IOException, SQLException {

    // 导出 Excel
    String filename = tableName + "-all.xlsx";

    // 获取数据
    List<Record> records = dbJsonService.listAll(tableName).getData();

    EesyExcelResponseUtils.exportRecords(response, filename, tableName, records);
    log.info("finished");
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

  @RequestMapping("/tables")
  public DbJsonBean<List<Record>> tables() throws IOException, SQLException {
    return dbJsonService.tables();
  }

  @RequestMapping("/table-names")
  public DbJsonBean<String[]> tableNames() throws IOException, SQLException {
    return dbJsonService.tableNames();
  }

  @RequestMapping("/table-config")
  public DbJsonBean<Map<String, Object>> queryItems(String tableName, String lang) {
    return dbJsonService.tableConfig(tableName, lang);

  }
}
