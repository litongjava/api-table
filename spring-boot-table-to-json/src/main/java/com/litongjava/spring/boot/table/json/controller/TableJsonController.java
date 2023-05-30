package com.litongjava.spring.boot.table.json.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jfinal.kit.Kv;
import com.litongjava.data.model.DataPageRequest;
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

  @RequestMapping("page")
  public DbJsonBean<DbPage<Kv>> page(String tableName, DataPageRequest pageRequest,
      @RequestParam Map<String, Object> map) {
    log.info("tableName:{}", tableName);
    log.info("page:{}", pageRequest);
    log.info("map:{}", map);

    Kv kv = KvUtils.camelToUnderscore(map);
    return DbJsonBeanUtils.pageToDbPage(dbJsonService.page(tableName, pageRequest, kv));

  }
}
