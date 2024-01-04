package com.litongjava.jfinal.table.json.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.paragetter.ParaProcessorBuilder;
import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.litongjava.jfinal.db.JFinalToJsonDbConfig;
import com.litongjava.jfinal.getter.KvGetter;

//@Slf4j
public class TableToJsonConfig extends JFinalConfig {

  public void configConstant(Constants me) {
    me.setInjectDependency(true);
    me.setInjectSuperClass(true);
    ParaProcessorBuilder.me.regist(Kv.class, KvGetter.class, null);
  }

  public void configRoute(Routes me) {
    me.setMappingSuperClass(true);
    me.scan("com.litongjava.jfinal.table.json.controler.");

  }

  @Override
  public void configEngine(Engine me) {
  }

  @Override
  public void configPlugin(Plugins me) {
    JFinalToJsonDbConfig.config(me);
  }

  @Override
  public void configInterceptor(Interceptors me) {
  }

  @Override
  public void configHandler(Handlers me) {
  }
}