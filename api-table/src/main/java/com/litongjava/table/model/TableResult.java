package com.litongjava.table.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TableResult<T> {
  // 状态码 1 success 0 fail
  private int code = 1;

  // 状态说明
  private String msg;

  // 数据内容
  private T data;

  public TableResult(T data) {
    this.data = data;
  }

  public TableResult(int code) {
    this.code = code;
  }

  public TableResult(String msg) {
    this.msg = msg;
  }

  public TableResult(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static <T> TableResult<T> fail() {
    return new TableResult<T>(-1, "fail");
  }

  public static <T> TableResult<T> fail(int code) {
    return new TableResult<T>(code, "fail");
  }

  public static <T> TableResult<T> fail(String message) {
    return new TableResult<T>(-1, message);
  }

  public static <T> TableResult<T> fail(T data) {
    return new TableResult<T>(-1, "fail", data);
  }

  public static <T> TableResult<T> fail(int code, String msg) {
    return new TableResult<T>(code, msg);
  }

  public static <T> TableResult<T> ok() {
    return new TableResult<T>();
  }

  public static <T> TableResult<T> ok(T kv) {
    return new TableResult<T>(kv);
  }

}
