package com.litongjava.data.services;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jfinal.kit.Kv;
import com.litongjava.data.constants.OperatorConstants;
import com.litongjava.data.model.DataPageRequest;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.jfinal.plugin.activerecord.Page;
import com.litongjava.jfinal.plugin.activerecord.Record;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbJsonServiceTest {

  DbJsonService dbJsonService = DbJsonService.getInstance();

  @Before
  public void initDb() {
    DbInit.init();
  }

  @Test
  public void test_listAll() {
    String tableName = "cf_alarm";
    DbJsonBean<List<Record>> bean = dbJsonService.listAll(tableName);
    System.err.println(bean);
  }

  /**
   * 分页查询
   */
  @SuppressWarnings("unchecked")
  @Test
  public void test_page() {
    String tableName = "cf_alarm";
    Kv kv = new Kv();
    kv.put("table_name", tableName);

    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test_pageWithParams() {
    String tableName = "cf_alarm";
    Kv kv = new Kv();
    kv.put("table_name", tableName);
    kv.put("config_id", 17);
    kv.put("order_by", "create_date");

    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(kv);

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
    kv.put("table_name", tableName);
    kv.put("ship_name", "救助");
    kv.put("ship_name_op", "ew");

    System.out.println(dataPageRequest);
    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  /**
   * 闭区间查询
   */
  @SuppressWarnings("unchecked")
  @Test
  public void queryStartAndEnd() {
    String tableName = "cf_alarm";
    Kv kv = new Kv();
    kv.put("table_name", tableName);
    // LocalDateTime[] createDateArray = { LocalDateTime.MIN, LocalDateTime.MAX };
    String[] createDateArray = { "2020-10-27 14:47:42", "2021-10-27 14:47:42" };
    kv.put("create_date", createDateArray);
    kv.put("create_date_op", OperatorConstants.BT);

    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  /**
   * 闭区间查询
   */
  @SuppressWarnings("unchecked")
  @Test
  public void queryGt() {
    String tableName = "cf_alarm";
    Kv kv = new Kv();
    kv.put("longitude", "64000000");
    kv.put("longitude_op", OperatorConstants.GT);

    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(tableName, kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testQueryLt() {
    String tableName = "cf_alarm";
    Kv kv = new Kv();
    kv.put("longitude", "64999999");
    kv.put("longitude_op", OperatorConstants.LT);

    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(tableName, kv);

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
    DbJsonBean<Kv> booleanDbJsonBean = dbJsonService.saveOrUpdate(tableName, kv);

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
    DbJsonBean<Boolean> deleted = dbJsonService.updateFlagById(tableName, "1532708", "deleted", 2);
    System.out.println(deleted);
  }

  @Test
  public void query() {
    String sql = "select ship_name from cf_alarm where id =?";
    String id = "1532708";
    DbJsonBean<List<Record>> result = dbJsonService.query(sql, id);
    System.out.println(result);
  }

}
