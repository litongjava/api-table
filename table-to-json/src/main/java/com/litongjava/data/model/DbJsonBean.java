package com.litongjava.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DbJsonBean<T> {

  private int code = 0; // 状态码

  private String msg; // 状态说明

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

  public static <T> DbJsonBean<T> fail() {
    return new DbJsonBean<T>(-1, "fail");
  }

  public static <T> DbJsonBean<T> fail(int code) {
    return new DbJsonBean<T>(code, "fail");
  }

  public static <T> DbJsonBean<T> fail(String message) {
    return new DbJsonBean<T>(-1, message);
  }

  public static <T> DbJsonBean<T> fail(T data) {
    return new DbJsonBean<T>(-1, "fail", data);
  }

  public static <T> DbJsonBean<T> fail(int code, String msg) {
    return new DbJsonBean<T>(code, msg);
  }

  public static <T> DbJsonBean<T> ok() {
    return new DbJsonBean<T>();
  }

  public static <T> DbJsonBean<T> ok(T kv) {
    return new DbJsonBean<T>(kv);
  }

}
