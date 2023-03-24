package com.litong.jfinal;

import com.github.xiaoymin.knife4j.jfinal.JFinalDocument;
import com.github.xiaoymin.knife4j.jfinal.JFinalDocument.Builder;
import com.github.xiaoymin.knife4j.jfinal.JFinalSwagger;
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
import com.litong.jfinal.db.TableToJsonDbConfig;
import com.litong.jfinal.getter.KvGetter;
import com.litong.jfinal.route.ApiRoutes;
import com.litong.jfinal.utils.PropKitUtil;
import com.litong.jfinal.utils.UndertowUtil;

//@Slf4j
public class TableToJsonApplication extends JFinalConfig {
  private static String configFileName = PropKitUtil.configFileName;

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    // 创建server
    UndertowUtil.server = UndertowServer.create(TableToJsonApplication.class, configFileName);
    // 启动Server全局共享
    UndertowUtil.server.addSystemClassPrefix("com.litong.jfinal.utils.UndertowUtil");
    UndertowUtil.server.start();
    long end = System.currentTimeMillis();
    System.out.println("启动完成,共使用了" + (end - start) + "ms");
//    startFrp();
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
    // 创建JFinalSwaggerDocument
    Builder builder = new JFinalDocument.Builder();
    JFinalDocument jFinalDocuments = builder.basePath("/").name("API").description("jfinal整合knife4j文档")
        .title("jfinal整合knife4j文档").paths("com.litong.jfinal.controler").order(1).contact("xxx@xx.com").build();
    // 添加路由
    me.add(new ApiRoutes()); // API路由
    me.add("/jf-swagger", com.litong.jfinal.controler.JFinalSwaggerController.class);

    // 添加JFinalDocument
    JFinalSwagger.me.addDocs(jFinalDocuments);
  }

  @Override
  public void configEngine(Engine me) {
  }

  @Override
  public void configPlugin(Plugins me) {
    TableToJsonDbConfig.config(me);
  }

  @Override
  public void configInterceptor(Interceptors me) {
  }

  @Override
  public void configHandler(Handlers me) {
  }

  @Override
  public void onStart() {
    JFinalSwagger.me.start();
  }
}