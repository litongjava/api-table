package com.litongjava.jfinal.table.json;

import com.jfinal.server.undertow.UndertowServer;
import com.litongjava.jfinal.table.json.config.TableToJsonConfig;

//@Slf4j
public class TableToJsonApplication {

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    UndertowServer.start(TableToJsonConfig.class);
    long end = System.currentTimeMillis();
    System.out.println("启动完成,共使用了" + (end - start) + "ms");
  }
}