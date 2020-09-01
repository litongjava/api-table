package com.litong.jfinal.controler;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.server.undertow.UndertowKit;

import io.undertow.Version;

/**
 * @author bill robot
 * @date 2020年8月21日_下午8:00:07 
 * @version 1.0 
 * @desc
 */
public class VersionController extends Controller {
  public void index() {
    Kv kv = new Kv();
    kv.set("java-version", System.getProperty("java.version"));
    kv.set("jfinal-version", UndertowKit.getJFinalVersion());
    kv.set("undertow-version", Version.getVersionString());
    kv.set("classpath", UndertowKit.getClassPathDirs());
    renderJson(kv);
  }
}
