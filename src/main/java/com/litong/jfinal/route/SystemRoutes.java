package com.litong.jfinal.route;

import com.jfinal.config.Routes;
import com.litong.jfinal.controler.IndexController;
import com.litong.jfinal.controler.PathController;
import com.litong.jfinal.controler.UndertowController;
import com.litong.jfinal.controler.VersionController;

/**
 * @author bill robot
 * @date 2020年8月16日_下午5:08:19 
 * @version 1.0 
 * @desc
 */
public class SystemRoutes extends Routes {

  @Override
  public void config() {
    add("/", IndexController.class);
    add("index", IndexController.class);
    add("undertow", UndertowController.class);
    add("path", PathController.class);
    add("version", VersionController.class);
  }

}
