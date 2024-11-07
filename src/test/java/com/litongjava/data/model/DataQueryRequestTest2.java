package com.litongjava.data.model;

import org.junit.Test;

import com.jfinal.kit.Kv;

public class DataQueryRequestTest2 {

  @Test
  public void test() {
    Kv kv = Kv.create();
    Boolean isAsc = Boolean.parseBoolean((String) kv.remove("is_asc"));
    System.out.println(isAsc);
  }

}
