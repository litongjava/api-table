package com.litongjava.data.model;

import com.jfinal.kit.Kv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataPageRequest {
  private Integer pageNo, pageSize;

  public DataPageRequest(Kv kv) {
    Object pageNo = kv.remove("page_no");
    this.pageNo = getIntegerValue(pageNo);

    Object pageSize = kv.remove("page_size");
    this.pageSize = getIntegerValue(pageSize);

  }

  private Integer getIntegerValue(Object v) {
    if (v == null) {
      return 1;
    } else {
      if (v instanceof Integer) {
        return (Integer) v;
      } else {
        return Integer.parseInt((String) v);
      }
    }

  }
}
