package com.litongjava.data.utils;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.jfinal.kit.Kv;
import com.litongjava.jfinal.plugin.activerecord.Record;

/**
 * @author litong
 * @version 1.0
 * @date 2020年9月29日_下午1:19:01
 * @desc
 */
public class KvUtils {

  /**
   * 的上传kv中的空元素
   *
   * @param kv
   */
  public static Kv removeEmptyValue(Kv kv) {
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
   * 1.将Map中key由驼峰转为转为下划线
   * 2.将Map转为Kv
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Kv camelToUnderscore(Map<String, Object> map) {
    Kv kv = new Kv();
    // map.replaceAll((key, value) -> CamelNameUtils.convertCamelToUnderscore(key));

    map.forEach((key, value) -> kv.put(CamelNameUtils.convertCamelToUnderscore(key), value));

    return kv;
  }

  @SuppressWarnings("unchecked")
  public static Kv underscoreToCamel(Map<String, Object> map) {
    Kv kv = new Kv();
    map.forEach((key, value) -> kv.put(CamelNameUtils.convertUnderscoreToCamel(key), value));
    return kv;
  }

  public static List<Kv> recordsToKv(List<Record> list,boolean underscoreToCamel) {
    return list.stream().map(record -> {
      return recordToKv(record, underscoreToCamel);
    }).collect(Collectors.toList());
  }

  public static Kv recordToKv(Record record, boolean underscoreToCamel) {
    if (record == null) {
      return null;
    }
    Map<String, Object> map = record.toMap();
    // 将Long转为String
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getValue() instanceof Long) {
        map.put(entry.getKey(), Long.toString((Long) entry.getValue()));
      }

      if (entry.getValue() instanceof BigInteger) {
        map.put(entry.getKey(), entry.getValue().toString());
      }
    }
    if (underscoreToCamel) {
      return KvUtils.underscoreToCamel(map);
    } else {
      return Kv.create().set(map);
    }

  }

  public static List<Map<String, Object>> recordsToMap(List<Record> records) {
    return records.stream().map(record -> record.toMap()).collect(Collectors.toList());
  }
}
