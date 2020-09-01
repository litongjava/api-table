package com.litong.jfinal.controler;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;

/**
 * @author bill robot
 * @date 2020年8月17日_下午5:20:56 
 * @version 1.0 
 * @desc
 */
public class IndexController extends Controller {
  public void index(Kv kv) {
    redirect("/index.html");
  }
}
