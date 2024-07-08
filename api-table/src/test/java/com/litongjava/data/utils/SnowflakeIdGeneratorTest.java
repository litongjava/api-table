package com.litongjava.data.utils;

import org.junit.Test;

public class SnowflakeIdGeneratorTest {

  @Test
  public void test() {
    //18为
    long generateId = new SnowflakeIdGenerator(0, 0).generateId();
    System.out.println(generateId);
  }

}
