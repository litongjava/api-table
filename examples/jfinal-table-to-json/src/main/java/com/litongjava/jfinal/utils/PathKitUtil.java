package com.litongjava.jfinal.utils;

import java.net.URL;

public class PathKitUtil {
  /**
   * 返回静态目录的真实文件夹
   */
  private static String realResourcePath = null;

  public static String getResourcePath() {
    String key = PropKitUtil.get("undertow.resourcePath");
    String[] split = key.split(":");
    String retval = null;
    if (split.length > 1) {
      if (split[0].equals("file")) {
        return split[1];
      } else {
        if (realResourcePath == null) {
          URL resource = PathKitUtil.class.getClassLoader().getResource(split[1]);
          realResourcePath = resource.getFile();
        }
        retval = realResourcePath;
      }
    }
    return retval;
  }
}