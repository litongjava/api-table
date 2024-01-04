package com.litongjava.tio.boot.table.to.json;

import com.litongjava.jfinal.aop.annotation.AComponentScan;
import com.litongjava.tio.boot.TioApplication;

@AComponentScan
public class TableToJsonApp {

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    TioApplication.run(TableToJsonApp.class, args);
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
  }
}
