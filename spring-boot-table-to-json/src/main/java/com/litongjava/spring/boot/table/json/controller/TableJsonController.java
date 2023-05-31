package com.litongjava.spring.boot.table.json.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DbPage;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.utils.DbJsonBeanUtils;
import com.litongjava.data.utils.EasyExcelUtils;
import com.litongjava.data.utils.KvUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/table/json")
@Slf4j
public class TableJsonController {

  @Autowired
  private DbJsonService dbJsonService;

  @RequestMapping("index")
  public String index() {
    return "index";
  }

  @SuppressWarnings("unchecked")
  @RequestMapping("page")
  public DbJsonBean<DbPage<Kv>> page(@RequestParam Map<String, Object> map) {
    log.info("map:{}", map);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
    kv.put("deleted", 0);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(kv));
  }

  @PostMapping("/create")
  public DbJsonBean<Boolean> create(@RequestBody Map<String, Object> map) {
    Kv kv = KvUtils.camelToUnderscore(map);
    log.info("map:{}", map);
    return dbJsonService.saveOrUpdate(kv);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping("/get")
  public DbJsonBean<Kv> get(String tableName, String id) {
    log.info("tableName:{},id:{}", tableName, id);
    Kv kv = new Kv();
    // 删除标记
    kv.put("deleted", 0);
    log.info("kv:{}", kv);

    return DbJsonBeanUtils.recordToKv(dbJsonService.getById(tableName, id, kv));
  }

  @DeleteMapping("/delete")
  public DbJsonBean<Integer> delete(String tableName, String id) {
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 1);
  }

  @PutMapping("/update")
  public DbJsonBean<Boolean> update(@RequestBody Map<String, Object> map) {
    Kv kv = KvUtils.camelToUnderscore(map);
    log.info("map:{}", map);
    return dbJsonService.saveOrUpdate(kv);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping("/list")
  public DbJsonBean<List<Kv>> list(@RequestParam Map<String, Object> map) {
    log.info("map:{}", map);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
    kv.put("deleted", 0);
    return DbJsonBeanUtils.recordsToKv(dbJsonService.list(kv));
  }

  @SuppressWarnings("unchecked")
  @RequestMapping("/export-excel")
  public void exportExcel(@RequestParam Map<String, Object> map, HttpServletResponse response) throws IOException {
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
    kv.put("deleted", 0);
    log.info("kv:{}", kv);
    String tableName = kv.getStr("table_name");

    List<Record> records = dbJsonService.list(kv).getData();
    List<Kv> kvs = KvUtils.recordsToKv(records);

    // 导出Excel
    String filename = tableName + "_export.xls";
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    EasyExcelUtils.write(response.getOutputStream(), filename, "tableName", null, kvs);
    // EasyExcelUtils.write(response, "水位配置.xls", "数据", WaterLevelSettingsExcelVO.class, kvs);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping("/download-excel")
  public void downloadExcel(@RequestParam Map<String, Object> map, HttpServletResponse response) throws IOException {
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
    kv.put("deleted", 0);
    log.info("kv:{}", kv);
    String tableName = kv.getStr("table_name");

    List<Record> records = dbJsonService.list(kv).getData();
    List<Map<String,Object>> kvs = KvUtils.recordsToMap(records);
    log.info("size:{}", kvs.size());
    // 导出Excel
    String filename = tableName + "_export.xls";
    response.setContentType("application/octet-stream;charset=UTF-8");
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    ServletOutputStream outputStream = response.getOutputStream();
    EasyExcelUtils.write(outputStream, filename, "tableName", null, kvs);
    // EasyExcelUtils.write(response, "水位配置.xls", "数据", WaterLevelSettingsExcelVO.class, kvs);
  }

  @SuppressWarnings("unchecked")
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
}
