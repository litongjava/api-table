package com.litongjava.data.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson2.JSONArray;
import com.litongjava.tio.utils.json.Json;

public class TioRequestParamUtilsForDateTest {

  @Test
  public void test() {
    String json = "{\r\n" + "    \"display_name\": \"Tong Li\",\r\n"
        + "    \"created_at\":\"2024-04-14T03:23:37.899Z\",\r\n" + "    \"platform\": \"other\",\r\n"
        + "    \"avatar_url\": \"https://firebasestorage.googleapis.com/v0/b/imaginix-eda2e.appspot.com/o/public%2Fimages%2F369047325126995968.jpg?alt=media\"\r\n"
        + "}";

    Map<String, Object> parseToMap = Json.getJson().parseToMap(json, String.class, Object.class);
    Map<String, List<Object>> arrayParams = new HashMap<>();
    Map<String, Object> paramType = new HashMap<>();
    paramType.put("created_at_type", "ISO8601");

    TioRequestParamUtils.convertValueType(parseToMap, arrayParams, paramType);
    System.out.println(parseToMap);
  }

  @Test
  public void isJsonArrayInstanceOfObjectArray() {
    Object jsonArray = new JSONArray();
    if (jsonArray instanceof Object[]) {
      System.out.println("true");
    }
    if(jsonArray instanceof ArrayList) {
      System.out.println("true");
    }
    System.out.println();

  }
}
