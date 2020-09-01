package com.litong.jfinal.utils;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class PropKitUtil {
  public static String configFileName = "undertow.properties";
  public static Prop prop;

  static {
    loadConfig();
  }

  public static void loadConfig() {
    if (prop == null) {
      prop = PropKit.use(configFileName);
    }
  }

  public static int getInt(String key) {
    return prop.getInt(key);
  }

  public static String get(String key) {
    return prop.get(key);
  }

  public static Boolean getBoolean(String key) {
    return prop.getBoolean(key, null);
  }
}