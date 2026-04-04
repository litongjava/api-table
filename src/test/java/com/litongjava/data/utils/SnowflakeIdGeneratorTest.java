package com.litongjava.data.utils;

import org.junit.Test;

import nexus.io.tio.utils.snowflake.SnowflakeIdUtils;

public class SnowflakeIdGeneratorTest {

  @Test
  public void test() {
    // 18为
    long generateId = SnowflakeIdUtils.id();
    System.out.println(generateId);
  }

}
