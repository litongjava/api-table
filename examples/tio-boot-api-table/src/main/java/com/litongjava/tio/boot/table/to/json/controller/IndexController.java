package com.litongjava.tio.boot.table.to.json.controller;

import com.litongjava.annotation.RequestPath;

@RequestPath("/")
public class IndexController {
  @RequestPath()
  public String index() {
    return "index";
  }
}
