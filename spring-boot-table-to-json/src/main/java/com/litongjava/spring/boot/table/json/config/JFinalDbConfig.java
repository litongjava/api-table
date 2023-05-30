package com.litongjava.spring.boot.table.json.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.litongjava.data.services.DbJsonService;

@Configuration
public class JFinalDbConfig {
  @Autowired
  private DataSource ds;

  @Autowired
  private Environment environment;

  @Primary
  @Bean(destroyMethod = "stop", initMethod = "start")
  public ActiveRecordPlugin activeRecordPlugin() throws Exception {
    String property = environment.getProperty("spring.profiles.active");
    ActiveRecordPlugin arp = new ActiveRecordPlugin(ds);
    if ("dev".equals(property)) {
      arp.setDevMode(true);
    }

    Engine engine = arp.getEngine();
    engine.setSourceFactory(new ClassPathSourceFactory());
    engine.setCompressorOn(' ');
    engine.setCompressorOn('\n');
    arp.addSqlTemplate("/sql/all_sqls.sql");
    return arp;
  }
  
  @Bean
  public DbJsonService dbJsonService() {
    return new DbJsonService();
  }
}