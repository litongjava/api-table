package com.litongjava.data.utils;

import java.util.Random;

public class SnowflakeIdUtils {

  public static long id() {
    return new SnowflakeIdGenerator(randomInt(1, 30), randomInt(1, 30)).generateId();
  }

  public static int randomInt(int min, int max) {
    Random random = new Random();
    int randomNumber = random.nextInt(max - min + 1) + min;
    return randomNumber;
  }
}
