package com.litong.jfinal;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.paragetter.ParaProcessorBuilder;
import com.jfinal.kit.Kv;
import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.litong.jfinal.db.DbConfig;
import com.litong.jfinal.getter.KvGetter;
import com.litong.jfinal.route.AdminRoutes;
import com.litong.jfinal.route.ApiRoutes;
import com.litong.jfinal.route.FrontRoutes;
import com.litong.jfinal.route.SystemRoutes;
import com.litong.jfinal.utils.PropKitUtil;
import com.litong.jfinal.utils.UndertowUtil;
import com.litong.utils.ip.IPUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application extends JFinalConfig {
  private static String configFileName = PropKitUtil.configFileName;

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    // 创建server
    log.info("create server");
    UndertowUtil.server = UndertowServer.create(Application.class, configFileName);
    // 启动Server全局共享
    UndertowUtil.server.addSystemClassPrefix("com.litong.jfinal.utils.UndertowUtil");
    log.info("start server");
    UndertowUtil.server.start();
    info(start);
//    startFrp();
  }

  /**
   * 输出启动信息
   * @param start
   */
  private static void info(long start) {
    UndertowConfig undertowConfig = UndertowUtil.server.getUndertowConfig();
    int port = undertowConfig.getPort();
    String contextPath = undertowConfig.getContextPath();
    long end = System.currentTimeMillis();
    System.out.println(IPUtils.getThisUrl(port, contextPath));
    System.out.println("启动完成,共使用了" + (end - start) + "ms");
  }

  public void configConstant(Constants me) {
    me.setInjectDependency(true);
    me.setInjectSuperClass(true);
    ParaProcessorBuilder.me.regist(Kv.class, KvGetter.class, null);
    // ParaProcessorBuilder.me.regist(HashMap<String,String>.class,
    // HashMapGetter.class, null);
  }

  public void configRoute(Routes me) {
    me.setMappingSuperClass(true);
    me.add(new FrontRoutes()); // 前端路由
    me.add(new AdminRoutes()); // 后端路由
    me.add(new SystemRoutes()); // 系统路由
    me.add(new ApiRoutes()); // API路由
  }

  @Override
  public void configEngine(Engine me) {
  }

  @Override
  public void configPlugin(Plugins me) {
    DbConfig.config(me);
  }

  @Override
  public void configInterceptor(Interceptors me) {
  }

  @Override
  public void configHandler(Handlers me) {
  }
}