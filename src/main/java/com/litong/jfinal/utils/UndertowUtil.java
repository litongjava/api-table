package com.litong.jfinal.utils;

import com.jfinal.server.undertow.UndertowServer;

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
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            System.err.println("Undetow Server Restarting ......");
            Thread.sleep(500);// 延迟500ms, 避免Web来不及返回信息
            server.restart();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();
      return;
    }
  }
  
  /**
   * 检测
   */
}