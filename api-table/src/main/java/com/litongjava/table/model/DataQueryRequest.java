package com.litongjava.table.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataQueryRequest {

  private String columns;
  private String orderBy;
  private Boolean isAsc;
  private String groupBy;

  public DataQueryRequest(TableInput kv) {
    this.columns = (String) kv.remove("columns");
    this.orderBy = (String) kv.remove("order_by");

    this.groupBy = (String) kv.remove("group_by");

    Object remove = kv.remove("is_asc");
    if (remove == null) {
      this.isAsc = null;
    } else {
      if (remove instanceof String) {
        this.isAsc = Boolean.parseBoolean((String) remove);
      } else if (remove instanceof Boolean) {
        this.isAsc = (Boolean) remove;
      }

    }
  }
}
