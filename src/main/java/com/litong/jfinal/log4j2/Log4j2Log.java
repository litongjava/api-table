package com.litong.jfinal.log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfinal.log.Log;
public class Log4j2Log extends Log {
  private Logger log;
  Log4j2Log(Class<?> clazz) {log = LogManager.getLogger(clazz);}
  Log4j2Log(String name) {log = LogManager.getLogger(name);}
  @Override
  public void debug(String message) {log.debug(message);}
  @Override
  public void debug(String message, Throwable t) {log.debug(message, t);}
  @Override
  public void info(String message) {log.info(message);}
  @Override
  public void info(String message, Throwable t) {log.info(message, t);}
  @Override
  public void warn(String message) {log.warn(message);}
  @Override
  public void warn(String message, Throwable t) {log.info(message, t);}
  @Override
  public void error(String message) {log.error(message);}
  @Override
  public void error(String message, Throwable t) {log.error(message, t);}
  @Override
  public void fatal(String message) {log.fatal(message);}
  @Override
  public void fatal(String message, Throwable t) {log.fatal(message, t);}
  @Override
  public boolean isDebugEnabled() {return log.isDebugEnabled();}
  @Override
  public boolean isInfoEnabled() {return log.isInfoEnabled();}
  @Override
  public boolean isWarnEnabled() {return log.isWarnEnabled();}
  @Override
  public boolean isErrorEnabled() {return log.isErrorEnabled();}
  @Override
  public boolean isFatalEnabled() {return log.isFatalEnabled();}
}