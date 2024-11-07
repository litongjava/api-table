package com.litongjava.tio.boot.table.to.json;

import com.litongjava.annotation.AComponentScan;
import com.litongjava.tio.boot.TioApplication;

@AComponentScan
public class ApiTableApp {

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    TioApplication.run(ApiTableApp.class, args);
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
  }
}
