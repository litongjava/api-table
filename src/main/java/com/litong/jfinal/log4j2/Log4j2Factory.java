package com.litong.jfinal.log4j2;
import com.jfinal.log.ILogFactory;
import com.jfinal.log.Log;
public class Log4j2Factory implements ILogFactory{
  public Log getLog(Class<?> clazz) {return  new Log4j2Log(clazz);}
  public Log getLog(String name) {return  new Log4j2Log(name);}
}