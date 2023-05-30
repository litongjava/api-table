package com.litongjava.data.services;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DataPageRequest;

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
    kv.put("like.ship_name", "远洋");
    
    System.out.println(dataPageRequest);
    DbJsonBean<Page<Record>> jsonBean = dbJsonService.page(tableName, dataPageRequest, kv);

    System.out.println(jsonBean);
    Page<Record> page = jsonBean.getData();
    List<Record> list = page.getList();
    System.out.println(list);
  }

}
