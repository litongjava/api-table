package com.litongjava.data.utils;

import java.util.Date;

import org.junit.Test;

public class DateParseUtilsTest {

  @Test
  public void test() {
    String dataString = "2024-04-14T03:23:37.899Z";
    Date date = DateParseUtils.parseIso8601Date(dataString);
    System.out.println(date);
  }

}
