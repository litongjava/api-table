package com.litongjava.tio.boot.table.to.json.config;

import com.litongjava.data.utils.TioRequestParamUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.aop.annotation.ABean;
import com.litongjava.jfinal.aop.annotation.AConfiguration;
import com.litongjava.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.litongjava.jfinal.plugin.activerecord.OrderedFieldContainerFactory;
import com.litongjava.jfinal.plugin.druid.DruidPlugin;
import com.litongjava.tio.utils.environment.EnvironmentUtils;

@AConfiguration
public class ActiveRecordPluginConfig {

  @ABean(priority = 10)
  public DruidPlugin druidPlugin() {
    String jdbcUrl = EnvironmentUtils.get("jdbc.url");
    String jdbcUser = EnvironmentUtils.get("jdbc.user");
    String jdbcPswd = EnvironmentUtils.get("jdbc.pswd");

    DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, jdbcUser, jdbcPswd);
    druidPlugin.start();
    return druidPlugin;
  }

  @ABean
  public ActiveRecordPlugin activeRecordPlugin() {
    boolean showSql = EnvironmentUtils.getBoolean("jdbc.showSql", false);

    DruidPlugin druidPlugin = Aop.get(DruidPlugin.class);
    ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
    arp.setContainerFactory(new OrderedFieldContainerFactory());
    arp.setShowSql(showSql);

    //add
    TioRequestParamUtils.types.add("bigint");
    arp.start();
    return arp;
  }
}
