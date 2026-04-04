package com.litongjava.data.utils;

import org.junit.Test;

import nexus.io.table.utils.ObjectUtils;

public class ObjectUtilsTest {

  @Test
  public void test() {
    boolean empyt = ObjectUtils.isEmpty("");
    System.out.println(empyt);
  }
}
