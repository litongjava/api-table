package com.litongjava.data.utils;

public class SnowflakeIdGenerator {
  // 起始的时间戳，可以设置为程序开始运行的时间或者其他固定值
  private static final long START_TIMESTAMP = 1625076000000L; // 2021-07-01 00:00:00
  // 每个部分占用的位数
  private static final long SEQUENCE_BITS = 12; // 序列号占用的位数
  private static final long WORKER_ID_BITS = 5; // 工作机器ID占用的位数
  private static final long DATACENTER_ID_BITS = 5; // 数据中心ID占用的位数
  // 每个部分的最大值
  private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BITS);
  private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
  private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);
  // 每个部分向左的位移
  private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
  private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
  private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
  // 工作机器ID和数据中心ID
  private long workerId;
  private long datacenterId;
  // 序列号和上次生成ID的时间戳
  private long sequence = 0L;
  private long lastTimestamp = -1L;

  public SnowflakeIdGenerator(long workerId, long datacenterId) {
    if (workerId > MAX_WORKER_ID || workerId < 0) {
      throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
    }
    if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
      throw new IllegalArgumentException(
          "Datacenter ID can't be greater than " + MAX_DATACENTER_ID + " or less than 0");
    }
    this.workerId = workerId;
    this.datacenterId = datacenterId;
  }

  public synchronized long generateId() {
    long currentTimestamp = getTimestamp();
    if (currentTimestamp < lastTimestamp) {
      throw new RuntimeException("Clock moved backwards. Refusing to generate ID");
    }
    if (currentTimestamp == lastTimestamp) {
      sequence = (sequence + 1) & MAX_SEQUENCE;
      if (sequence == 0) {
        // 当前毫秒的序列号已经达到最大值，等待下一毫秒
        currentTimestamp = getNextTimestamp(lastTimestamp);
      }
    } else {
      sequence = 0L;
    }
    lastTimestamp = currentTimestamp;
    return (currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT | datacenterId << DATACENTER_ID_SHIFT
        | workerId << WORKER_ID_SHIFT | sequence;
  }

  private long getNextTimestamp(long lastTimestamp) {
    long currentTimestamp = getTimestamp();
    while (currentTimestamp <= lastTimestamp) {
      currentTimestamp = getTimestamp();
    }
    return currentTimestamp;
  }

  private long getTimestamp() {
    return System.currentTimeMillis();
  }
}
