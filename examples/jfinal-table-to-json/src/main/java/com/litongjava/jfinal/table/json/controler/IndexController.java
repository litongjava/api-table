package com.litongjava.jfinal.table.json.controler;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;

@Path("/")
public class IndexController extends Controller {

  public void index() {
    renderText("index");
  }
}
