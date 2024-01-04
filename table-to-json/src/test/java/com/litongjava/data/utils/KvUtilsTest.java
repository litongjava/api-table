package com.litongjava.data.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.litongjava.jfinal.plugin.kit.Kv;

public class KvUtilsTest {

  @Test
  public void test() {
    Map<String, Object> map = new HashMap<>();
    map.put("like.shipName", "救助");
    map.put("start.createDate", "2021-10-27 00:00:00");
    map.put("end.createDate", "2021-10-27 23:59:59");
    map.put("gt.longitude", "64000000");
    map.put("lt.longitude", "64999999");

    Kv kv = KvUtils.camelToUnderscore(map);
    System.out.println(kv);
  }

}
