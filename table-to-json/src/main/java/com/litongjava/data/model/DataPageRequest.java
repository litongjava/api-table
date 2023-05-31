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
  private String columns, orderBy;
  private Boolean isAsc;

  public DataPageRequest(Kv kv) {
    Object pageNo = kv.remove("page_no");
    this.pageNo = pageNo == null ? 1 : Integer.parseInt((String) pageNo);
    
    Object pageSize = kv.remove("page_size");
    this.pageSize = pageSize == null ? 10 : Integer.parseInt((String) pageSize);
    
    this.columns = (String) kv.remove("columns");
    this.orderBy = (String) kv.remove("order_by");
    this.isAsc = (Boolean) kv.remove("is_asc");

  }
}
