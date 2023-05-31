package com.litongjava.spring.boot.table.json.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jfinal.kit.Kv;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DbPage;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.utils.DbJsonBeanUtils;
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
  @GetMapping("/get")
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
  @RequestMapping("pageDeleted")
  public DbJsonBean<DbPage<Kv>> pageDeleted(@RequestParam Map<String, Object> map) {
    log.info("map:{}", map);
    Kv kv = KvUtils.camelToUnderscore(map);
    // 删除
    kv.put("deleted", 1);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(kv));
  }

  @GetMapping("/recover")
  public DbJsonBean<Integer> recover(String tableName, String id) {
    log.info("tableName:{},id:{}", tableName, id);
    return dbJsonService.updateFlagById(tableName, id, "deleted", 0);
  }
}
