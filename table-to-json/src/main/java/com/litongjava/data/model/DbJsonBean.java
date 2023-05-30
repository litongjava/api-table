package com.litongjava.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DbJsonBean<T> {

  private int code = 0; // 状态码

  private String msg = ""; // 状态说明

  private T data; // 数据内容

  public DbJsonBean(T data) {
    this.data = data;
  }

  public DbJsonBean(int code) {
    this.code = code;
  }

  public DbJsonBean(String msg) {
    this.msg = msg;
  }

  public DbJsonBean(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

}
