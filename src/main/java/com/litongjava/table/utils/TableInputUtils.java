package com.litongjava.table.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.litongjava.db.TableInput;
import com.litongjava.tio.utils.name.CamelNameUtils;

/**
 * @author litong
 * @version 1.0
 * @date 2020年9月29日_下午1:19:01
 * @desc
 */
public class TableInputUtils {

  /**
   * 的上传kv中的空元素
   *
   * @param kv
   */
  public static TableInput removeEmptyValue(TableInput kv) {
    @SuppressWarnings("unchecked")
    Set<Object> keySet = kv.keySet();
    Set<Object> removeKeySet = new HashSet<>();
    for (Object k : keySet) {
      if (kv.isNull(k)) {
        removeKeySet.add(k);
      }
    }
    for (Object k : removeKeySet) {
      kv.remove(k);
    }
    return kv;
  }

  /**
   * 1.将Map中key由驼峰转为转为下划线 2.将Map转为Kv
   *
   * @return
   */
  public static <T> TableInput camelToUnderscore(Map<String, T> map) {
    TableInput kv = new TableInput();

    for (Map.Entry<String, T> entry : map.entrySet()) {
      String newKey = CamelNameUtils.toUnderscore(entry.getKey());
      kv.set(newKey, entry.getValue());
    }
    return kv;
  }

  /**
   * 将kv中的键为is_开头的值为true转为1
   *
   * @param kv
   */
  @SuppressWarnings("unchecked")
  public static void true21(TableInput kv) {
    Set<Map.Entry<String, Object>> entrySet = kv.entrySet();
    for (Map.Entry<String, Object> e : entrySet) {
      String key = e.getKey();
      if (key.startsWith("is")) {
        Object object = kv.get(key);
        if (object instanceof String) {
          String str = kv.getStr(key);
          // boolean b1 = Boolean.getBoolean(str); //str命名为true,但是返回false
          if ("true".equalsIgnoreCase(str) || "1".equals(str)) {
            kv.put(key, 1);
          } else {
            kv.put(key, 0);
          }
        }
      }
    }
  }

  /**
   * json_fields
   */
  public static String[] getJsonFields(TableInput kv) {
    String[] jsonFields = null;
    Object jsonFieldsObject = kv.remove("json_fields");

    if (jsonFieldsObject instanceof String) {
      jsonFields = ((String) jsonFieldsObject).split(",");
    } else if (jsonFieldsObject instanceof String[]) {
      jsonFields = (String[]) jsonFieldsObject;
    } else if (jsonFieldsObject instanceof List) {
      List<?> list = (List<?>) jsonFieldsObject;
      jsonFields = list.toArray(new String[list.size()]);
    }
    return jsonFields;
  }

  public static Map<String, String> getEmbeddingMap(TableInput kv) {
    Map<String, String> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    Set<Map.Entry<String, Object>> entrySet = kv.entrySet();
    Iterator<Entry<String, Object>> iterator = entrySet.iterator();

    while (iterator.hasNext()) {
      Entry<String, Object> entry = iterator.next();

      String key = entry.getKey();
      if (key.endsWith("embedding")) {
        int lastIndexOf = key.lastIndexOf("_");
        map.put(key.substring(0, lastIndexOf), (String) entry.getValue());
        iterator.remove();
      }
    }
    return map;
  }

}
