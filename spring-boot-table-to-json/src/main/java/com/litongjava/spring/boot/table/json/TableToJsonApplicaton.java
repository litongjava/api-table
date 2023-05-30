package com.litongjava.spring.boot.table.json;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.litongjava.hotswap.wrapper.spring.boot.SpringApplicationWrapper;

@SpringBootApplication
public class TableToJsonApplicaton {

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    SpringApplicationWrapper.run(TableToJsonApplicaton.class, args);
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "(ms)");

  }
}