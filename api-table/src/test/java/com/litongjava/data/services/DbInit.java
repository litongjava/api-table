package com.litongjava.data.services;

import com.litongjava.db.activerecord.ActiveRecordPlugin;
import com.litongjava.db.druid.DruidPlugin;

public class DbInit {

  public static void init() {

    String jdbcUrl = "jdbc:mysql://192.168.3.9/ruoyi-vue-pro";
    String jdbcUser = "root";
    String jdbcPswd = "robot_123456#";
    DruidPlugin dp = new DruidPlugin(jdbcUrl, jdbcUser, jdbcPswd);
    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
    dp.start();
    arp.start();
  }
}
