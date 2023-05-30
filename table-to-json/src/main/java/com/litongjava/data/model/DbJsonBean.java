package com.litongjava.data.model;


public class DbJsonBean<T> {

  private int code = 0; // 状态码

  private String msg = "执行成功"; // 状态说明

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

  public DbJsonBean() {
  }

  public DbJsonBean(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "JsonBean{" + "code=" + code + ", msg='" + msg + '\'' + ", data=" + data + '}';
  }
}
