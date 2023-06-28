package com.litongjava.spring.boot.table.json.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DbPage;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.utils.DbJsonBeanUtils;
import com.litongjava.data.utils.EasyExcelUtils;
import com.litongjava.data.utils.KvUtils;
import com.litongjava.data.utils.RequestMapUtils;
import com.litongjava.data.vo.DateTimeReqVo;

import cn.hutool.core.bean.BeanUtil;
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
    Map<String, Object> map = getRequestMap(request);
    log.info("map:{}", map);
    Kv kv = KvUtils.camelToUnderscore(map);
    return dbJsonService.saveOrUpdate(kv);
  }

  @RequestMapping("/list")
  public DbJsonBean<List<Kv>> list(HttpServletRequest request) {
    Map<String, Object> map = getRequestMap(request);
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
  public DbJsonBean<Boolean> update(@RequestBody Map<String, Object> map) {
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
  public DbJsonBean<DbPage<Kv>> pageDeleted(@RequestParam Map<String, Object> map) {
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
  public void exportExcel(@RequestParam Map<String, Object> map, HttpServletResponse response) throws IOException {
    Kv kv = KvUtils.camelToUnderscore(map);
    kv.put("deleted", 0);
    log.info("kv:{}", kv);
    String tableName = kv.getStr("table_name");
    String filename = tableName + "_export.xls";

    // 获取数据
    List<Record> records = dbJsonService.list(kv).getData();
    exportRecords(response, filename, tableName, records);
  }

  @RequestMapping("/export-table-excel")
  public void exporAllExcel(String tableName, HttpServletResponse response) throws IOException, SQLException {

    // 导出 Excel
    String filename = tableName + "-all.xlsx";

    // 获取数据
    List<Record> records = dbJsonService.listAll(tableName).getData();

    exportRecords(response, filename, tableName, records);
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
    exportAllTableRecords(response, filename, allTableData);
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

  private void exportRecords(HttpServletResponse response, String filename, String sheetName, List<Record> records)
      throws IOException {
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    EasyExcelUtils.write(response.getOutputStream(), sheetName, records);
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
  }

  private void exportAllTableRecords(HttpServletResponse response, String filename,
      LinkedHashMap<String, List<Record>> allTableData) throws IOException {
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    EasyExcelUtils.write(response.getOutputStream(), allTableData);
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
  }

  /**
   * 自定义导出
   */
  public static <T> void export(String sheetName, HttpServletResponse response, String filename, List<Record> records,
      Class<T> clazz) throws UnsupportedEncodingException, IOException {
    List<Map<String, Object>> collect = records.stream().map(e -> e.toMap()).collect(Collectors.toList());
    List<T> exportDatas = BeanUtil.copyToList(collect, clazz);

    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    EasyExcelUtils.write(response.getOutputStream(), filename, sheetName, clazz, exportDatas);
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
  }

  private Map<String, Object> getRequestMap(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    String contentType = request.getContentType();

    if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
      // JSON handling
      try {
        map = new ObjectMapper().readValue(request.getInputStream(), Map.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      // Form data handling
      Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String paramName = parameterNames.nextElement();
        map.put(paramName, request.getParameter(paramName));
      }
    }
    return map;
  }
}
