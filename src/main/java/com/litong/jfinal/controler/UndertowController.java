package com.litong.jfinal.controler;

import com.jfinal.core.Controller;
import com.litong.jfinal.utils.UndertowUtil;

/**
 * @author bill robot
 * @date 2020年8月11日_下午9:04:17 
 * @version 1.0 
 * @desc
 */
public class UndertowController extends Controller {
  public void restart() {
    //UndertowUtil.server.restart();
    UndertowUtil.restart();
    renderText("OK");
  }

  public void stop() {
    UndertowUtil.server.stop();
    renderText("OK");
  }
  
  public void status() {
    boolean started = UndertowUtil.server.isStarted();
    renderJson(started);
  }
}
