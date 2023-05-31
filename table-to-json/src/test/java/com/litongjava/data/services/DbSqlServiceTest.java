package com.litongjava.data.services;

import java.util.List;

import org.junit.Test;

import com.jfinal.kit.Kv;

public class DbSqlServiceTest {

  @SuppressWarnings("unchecked")
  @Test
  public void testGetListWhere() {
    String tableName = "alarm_ai";
    Kv kv = Kv.create();
    kv.put("key1", "value1");
    kv.put("key2", "value2");
    kv.put("key3", new Object[] { "value31", "value32" });
    kv.put("key4", new Object[] { "value41", "value42" });

    kv.put("key3_op", "bt");
    kv.put("key4_op", "bt");

    StringBuffer where = new StringBuffer();

    List<Object> paramList = new DbSqlService().getListWhere(tableName, kv, where);
    System.out.println(where);
    System.out.println(paramList);
  }

}
