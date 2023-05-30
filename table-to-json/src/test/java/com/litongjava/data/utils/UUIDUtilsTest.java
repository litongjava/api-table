package com.litongjava.data.utils;

import org.junit.Test;

public class UUIDUtilsTest {

  @Test
  public void test() {
    String random = UUIDUtils.random();
    System.out.println(random);
  }

}
