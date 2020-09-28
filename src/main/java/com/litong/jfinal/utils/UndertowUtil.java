package com.litong.jfinal.utils;

import com.jfinal.server.undertow.UndertowServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndertowUtil {

  /**
   * 当前启动服务全局共享
   */
  public static UndertowServer server = null;

  /**
   * 重启当前服务
   */
  public static void restart() {
    if (server != null) {
      new Thread(() -> {
        log.info("Undetow Server Restarting ......");
        try {
          // 延迟500ms, 避免Web来不及返回信息
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        server.restart();
      }).start();
      return;
    }
  }
}