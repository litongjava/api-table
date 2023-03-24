package com.litong.jfinal.utils;

import java.util.HashSet;
import java.util.Set;

import com.jfinal.kit.Kv;

/**
 * @author litong
 * @date 2020年9月29日_下午1:19:01 
 * @version 1.0 
 * @desc
 */
public class KvUtils {

  /**
   * 的上传kv中的空元素
   * @param kv
   */
  public static Kv removeEmptyValue(Kv kv) {
    @SuppressWarnings("unchecked")
    Set<Object> keySet = kv.keySet();
    Set<Object> removeKeySet=new HashSet<>();
    for (Object k : keySet) {
      if (kv.isNull(k)) {
        removeKeySet.add(k);
      }
    }
    for(Object k:removeKeySet) {
      kv.remove(k);
    }
    return kv;
  }

}
