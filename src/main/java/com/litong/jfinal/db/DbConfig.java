package com.litong.jfinal.db;

import com.jfinal.config.Plugins;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.litong.jfinal.utils.PropKitUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author litong
 * @date 2019年8月6日_下午4:34:38
 * @version 1.0
 * @desc 数据库配置
 */
@Slf4j
public class DbConfig {
  private static Plugins me;

  /**
   * 配置插件和数据源
   * @param me
   */
  public static void config(Plugins me) {
    DbConfig.me = me;
    DruidPlugin dp = initDruidPlugin();
    ActiveRecordPlugin ar = initActiveRecordPlugin(dp);
    me.add(dp);
    me.add(ar);
    log.info("添加主数据源完成");
  }

  public static ActiveRecordPlugin initActiveRecordPlugin(DruidPlugin dp) {
    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
    if (DBInfo.getDBType().equals("oracle")) {
      arp.setDialect(new OracleDialect());
    }
    arp.setShowSql(PropKitUtil.getBoolean("jdbc.showSql"));
    // 系统类添加映射
    // CommonMappingKit.mapping(arp);
    // 业务类添加映射
    //_MappingKit.mapping(arp);

    return arp;
  }

  public static DruidPlugin initDruidPlugin() {
    String jdbcUrl = PropKitUtil.get("jdbc.url");
    String jdbcUser = PropKitUtil.get("jdbc.user");
    String jdbcPswd = PropKitUtil.get("jdbc.pswd");
    DruidPlugin dp = new DruidPlugin(jdbcUrl, jdbcUser, jdbcPswd);
    return dp;
  }

  /**
   * 添加其他数据源
   * @param name
   * @param jdbcUrl
   * @param jdbcUser
   * @param jdbcPswd
   */
  public static void addOtherDataSource(String name, String jdbcUrl, String jdbcUser, String jdbcPswd) {
    String dbType = DBInfo.getDBType();
    DruidPlugin dp = new DruidPlugin(jdbcUrl, jdbcUser, jdbcPswd);
    ActiveRecordPlugin ar = new ActiveRecordPlugin(name, dp);
    if (dbType.equals("oracle")) {
      ar.setDialect(new OracleDialect());
    }
    me.add(ar);
  }
}
