package com.litongjava.data.utils;

import java.util.Map;

import org.junit.Test;

import com.litongjava.tio.utils.json.Json;

public class TioRequestParamUtilsTest {

  @Test
  public void test() {
    String json="        {\r\n"
        + "            \"creator\": \"admin\",\r\n"
        + "            \"sex\": 1,\r\n"
        + "            \"deptId\": \"103\",\r\n"
        + "            \"mobile\": \"15612345678\",\r\n"
        + "            \"loginDate\": \"2023-11-30 09:16:00\",\r\n"
        + "            \"remark\": \"管理员\",\r\n"
        + "            \"updateTime\": \"2024-03-23 08:49:55\",\r\n"
        + "            \"avatar\": \"http://127.0.0.1:48080/admin-api/infra/file/4/get/37e56010ecbee472cdd821ac4b608e151e62a74d9633f15d085aee026eedeb60.png\",\r\n"
        + "            \"updater\": null,\r\n"
        + "            \"password\": \"$2a$10$mRMIYLDtRHlf6.9ipiqH1.Z.bh/R9dO9d5iHiGYPigi6r5KOoR2Wm\",\r\n"
        + "            \"deleted\": false,\r\n"
        + "            \"createTime\": \"2021-01-05 17:03:47\",\r\n"
        + "            \"postIds\": \"[1]\",\r\n"
        + "            \"loginIp\": \"127.0.0.1\",\r\n"
        + "            \"nickname\": \"乒乒\",\r\n"
        + "            \"tenantId\": \"1\",\r\n"
        + "            \"id\": \"1\",\r\n"
        + "            \"email\": \"aoteman@126.com\",\r\n"
        + "            \"username\": \"admin\",\r\n"
        + "            \"status\": 0\r\n"
        + "        },";
    Map<String, String> parseToMap = Json.getJson().parseToMap(json,String.class,String.class);
    System.out.println(parseToMap);
  }

}
