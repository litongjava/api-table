package com.litongjava.spring.boot.table.json.controller;


import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.utils.DbJsonBeanUtils;
import com.litongjava.spring.boot.table.json.init.TableToJsonInit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

@Slf4j
public class TableJsonControllerTest {

  @Before
  public void before() {
    TableToJsonInit.initActiveRecord();

  }

  @Test
  public void test01() {

    // 传入参数
    // tableName:semester,kv:{columns=name, institution_id=260626905730908160}
    String tableName = "semester";
    Kv kv = Kv.create();
    kv.put("columns", "name");
//    kv.put("institution_id", "260626905730908160");
    //institution_id_type=int
    //institution_id_type=long
    kv.put("institution_id", 260626905730908160L);
    log.info("tableName:{},kv:{}", tableName, kv);
    /**
     *
     * 如果institution_id类型是是string则会出现下面的异常
     * 如果institution_id类型是是int则不
     */
    //com.jfinal.plugin.activerecord.ActiveRecordException: org.postgresql.util.PSQLException: ERROR: operator does not exist: bigint = character varying
    DbJsonService dbJsonService = new DbJsonService();
    DbJsonBean<List<Kv>> listDbJsonBean = DbJsonBeanUtils.recordsToKv(dbJsonService.list(tableName, kv));
    System.out.println(listDbJsonBean);
  }

  @Test
  public void test02() {
    Dialect dialect = DbKit.getConfig().getDialect();
    System.out.println(dialect);
  }
}