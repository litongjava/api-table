package com.litongjava.spring.boot.table.json.init;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.hikaricp.HikariCpPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

public class TableToJsonInit {

  public static void initActiveRecord() {
    PropKit.use("undertow.txt");
    String jdbcUrl = PropKit.get("jdbc.url");
    String jdbcUser = PropKit.get("jdbc.user");
    String jdbcPswd = PropKit.get("jdbc.pswd");
    //DruidPlugin dp = new DruidPlugin(jdbcUrl, jdbcUser, jdbcPswd);
    HikariCpPlugin hikariCpPlugin = new HikariCpPlugin(jdbcUrl, jdbcUser, jdbcPswd);
    ActiveRecordPlugin arp = new ActiveRecordPlugin(hikariCpPlugin);
    arp.setShowSql(true);
    arp.setDialect(new PostgreSqlDialect());

    Engine engine = arp.getEngine();
    engine.setSourceFactory(new ClassPathSourceFactory());
    engine.setCompressorOn(' ');
    engine.setCompressorOn('\n');
    arp.addSqlTemplate("/sql/all_sqls.sql");
//    arp.setDialect(new PostgreSqlDialect());

    hikariCpPlugin.start();
    arp.start();
  }
}
