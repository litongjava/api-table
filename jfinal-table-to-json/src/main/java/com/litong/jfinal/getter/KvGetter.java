package com.litong.jfinal.getter;

import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.ParaGetter;
import com.jfinal.kit.Kv;

//@Slf4j
public class KvGetter extends ParaGetter<Kv> {

  public KvGetter(String parameterName, String defaultValue) {
    super(parameterName, defaultValue);
  }

  @Override
  public Kv get(Action action, Controller c) {
    return c.getKv();
  }

  @Override
  protected Kv to(String v) {
    return null;
  }
}