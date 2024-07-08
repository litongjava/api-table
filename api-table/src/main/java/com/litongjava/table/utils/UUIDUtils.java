package com.litongjava.table.utils;

import java.util.UUID;

/**
 * 
 * @author bill robot
 * @date 2020年8月29日 下午6:41:14
 * @desc
 */
public class UUIDUtils {
  /**
   * 生成随机的32位uuid
   * @return
   */
  public static final String random() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
