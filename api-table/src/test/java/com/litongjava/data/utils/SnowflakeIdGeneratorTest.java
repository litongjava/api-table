package com.litongjava.data.utils;

import org.junit.Test;

import com.litongjava.table.utils.SnowflakeIdGenerator;

public class SnowflakeIdGeneratorTest {

  @Test
  public void test() {
    //18为
    long generateId = new SnowflakeIdGenerator(0, 0).generateId();
    System.out.println(generateId);
  }

}
