package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;

import lombok.extern.slf4j.Slf4j;

import com.litongjava.data.model.DataPageRequest;

@Slf4j
public class DbJsonServiceTest {

  DbJsonService dbJsonService = new DbJsonService();

  @Before
  public void initDb() {
    DbInit.init();
  }

  /**
   * 分页查询
   */
  @Test
  public void test() {
    String tableName = "cf_alarm";
    DataPageRequest dataPageRequest = new DataPageRequest();
    Kv kv = new Kv();

    System.out.println(dataPageRequest);
    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(tableName, dataPageRequest, kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  /**
   * 模糊查询
   */
  @SuppressWarnings("unchecked")
  @Test
  public void queryLike() {
    String tableName = "cf_alarm";
    DataPageRequest dataPageRequest = new DataPageRequest();
    Kv kv = new Kv();
    kv.put("like.ship_name", "救助");

    System.out.println(dataPageRequest);
    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(tableName, dataPageRequest, kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  /**
   * 闭区间查询
   */
  @Test
  public void queryStartAndEnd() {
    String tableName = "cf_alarm";
    DataPageRequest dataPageRequest = new DataPageRequest();
    Kv kv = new Kv();
    kv.put("start.create_date", "2021-10-27 00:00:00");
    kv.put("end.create_date", "2021-10-27 23:59:59");

    System.out.println(dataPageRequest);
    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(tableName, dataPageRequest, kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  /**
   * 闭区间查询
   */
  @Test
  public void queryGtAndLt() {
    String tableName = "cf_alarm";
    DataPageRequest dataPageRequest = new DataPageRequest();
    Kv kv = new Kv();
    kv.put("gt.longitude", "64000000");
    kv.put("lt.longitude", "64999999");

    System.out.println(dataPageRequest);
    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(tableName, dataPageRequest, kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  /**
   * 增加数据
   */
  @SuppressWarnings("unchecked")
  @Test
  public void add() {
    // 不存在Id,增加数据
    String tableName = "cf_alarm";
    DataPageRequest dataPageRequest = new DataPageRequest();
    Kv kv = new Kv();
    kv.put("config_id", "17");
    kv.put("ship_name", "JIUZHU805");
    kv.put("mmsi", "413844815");
    kv.put("processed", "1");
    kv.put("longitude", "64151636");
    kv.put("latitude", "17812464");
    kv.put("speed", "21");
    kv.put("course", "2071");
    kv.put("heading", "205");
    kv.put("nav_status", "1");
//    kv.put("create_date", LocalDate.now());
//    kv.put("update_time", LocalDate.now());
    kv.put("deleted", 0);
    kv.put("tenant_id", 1);

    System.out.println(dataPageRequest);
    DbJsonBean<Boolean> booleanDbJsonBean = dbJsonService.saveOrUpdate(tableName, kv);

    System.out.println(booleanDbJsonBean);
  }

  @Test
  public void physicalDelete() {
    String tableName = "cf_alarm";
    DbJsonBean<Boolean> booleanDbJsonBean = dbJsonService.delById(tableName, "1532704");
    System.out.println(booleanDbJsonBean);
  }

  @Test
  public void logoicalDelete() {
    String tableName = "cf_alarm";
    DbJsonBean<Integer> deleted = dbJsonService.updateFlagById(tableName, "1532708", "deleted", 2);
    System.out.println(deleted);
  }

  private DbSqlService dbDataService = new DbSqlService();

  @SuppressWarnings("unchecked")
  @Test
  public void testGetById() {
    String tableName = "cf_alarm_ai";
    Kv queryParam = new Kv();
    // 删除
    queryParam.put("deleted", 0);
    queryParam.put("id", 1);
    queryParam.put("tenant_id", 3);

    // 拼接sql语句
    StringBuffer sql = new StringBuffer();
    List<Object> paramList = new ArrayList<Object>();

    String sqlTemplate = "select * from %s where ";
    String format = String.format(sqlTemplate, tableName);
    sql.append(format);

    // 添加其他查询条件
    paramList = dbDataService.getListWhere(tableName, queryParam, sql);

    // 添加操作表
    log.info("sql:{}", sql);
    log.info("paramList:{}", paramList);
  }

}
