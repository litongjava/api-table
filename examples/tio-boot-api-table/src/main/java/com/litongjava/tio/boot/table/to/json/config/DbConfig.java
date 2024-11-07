package com.litongjava.tio.boot.table.to.json.config;

import com.litongjava.annotation.AConfiguration;
import com.litongjava.annotation.Initialization;
import com.litongjava.db.activerecord.ActiveRecordPlugin;
import com.litongjava.db.activerecord.OrderedFieldContainerFactory;
import com.litongjava.db.activerecord.dialect.PostgreSqlDialect;
import com.litongjava.db.druid.DruidPlugin;
import com.litongjava.tio.boot.server.TioBootServer;
import com.litongjava.tio.boot.utils.TioRequestParamUtils;
import com.litongjava.tio.utils.environment.EnvUtils;

import lombok.extern.slf4j.Slf4j;

@AConfiguration
@Slf4j
public class DbConfig {

  @Initialization
  public void config() {
    String jdbcUrl = EnvUtils.get("jdbc.url");
    String jdbcUser = EnvUtils.get("jdbc.user");
    String jdbcPswd = EnvUtils.get("jdbc.pswd");
    log.info("url:{},user:{},pswd:{}", jdbcUrl, jdbcUser, jdbcPswd);
    DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, jdbcUser, jdbcPswd);
    druidPlugin.start();

    boolean showSql = EnvUtils.getBoolean("jdbc.showSql", false);

    ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
    arp.setContainerFactory(new OrderedFieldContainerFactory());
    arp.setShowSql(showSql);
    if (jdbcUrl.startsWith("jdbc:postgresql://")) {
      arp.setDialect(new PostgreSqlDialect());
    }

    //add
    TioRequestParamUtils.types.add("bigint");
    arp.start();

    TioBootServer.me().addDestroyMethod(() -> {
      druidPlugin.stop();
      arp.stop();
    });
  }
}
