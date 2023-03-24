package com.litong.jfinal.controler;

import com.jfinal.core.Controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "首页模块")
public class IndexController extends Controller {

  @ApiOperation(value = "向客人问好")
  
  //设置完成后不显示
  //@ApiImplicitParams({ @ApiImplicitParam(value = "姓名", name = "name"), @ApiImplicitParam(name = "address", value = "地址") })
  

  public void sayHi(String name) {
    renderText("Hi:" + name);
  }
}
