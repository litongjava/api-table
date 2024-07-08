package com.litongjava.table.utils;

import java.util.Random;

public class SnowflakeIdUtils {

  // 单例的 SnowflakeIdGenerator 实例
  private static final SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(randomInt(1, 30),
      randomInt(1, 30));

  public static long id() {
    return snowflakeIdGenerator.generateId();
  }

  public static int randomInt(int min, int max) {
    Random random = new Random();
    int randomNumber = random.nextInt(max - min + 1) + min;
    return randomNumber;
  }
}
