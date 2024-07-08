package com.litongjava.data.services;

import org.junit.Test;

import com.jfinal.kit.Kv;
import com.litongjava.data.model.DataQueryRequest;
import com.litongjava.data.model.Sql;

public class DbSqlServiceTest {

  @SuppressWarnings("unchecked")
  private Kv getKey() {
    Kv kv = Kv.create();
    kv.put("key1", "value1");
    kv.put("key2", "value2");
    kv.put("key3", new Object[] { "value31", "value32" });
    kv.put("key4", new Object[] { "value41", "value42" });

    kv.put("key3_op", "bt");
    kv.put("key4_op", "bt");
    return kv;
  }

  @Test
  public void test_getWhereQueryClause() {
    Kv kv = getKey();

    Sql whereQueryClause = new DbSqlService().getWhereQueryClause(kv);

    System.out.println(whereQueryClause.getWhere().toString());
    System.out.println(whereQueryClause.getParams());
  }

  @Test
  public void test_getWhereClause() {
    Kv kv = getKey();

    String orderBy = "create_time";
    Boolean isAsc = true;
    String groupyBy = "user_id";

    DataQueryRequest queryRequest = new DataQueryRequest();
    queryRequest.setOrderBy(orderBy);
    queryRequest.setIsAsc(isAsc);
    queryRequest.setGroupBy(groupyBy);

    Sql whereQueryClause = new DbSqlService().getWhereClause(queryRequest, kv);

    System.out.println(whereQueryClause.getWhere().toString());
    System.out.println(whereQueryClause.getParams());
  }

}
