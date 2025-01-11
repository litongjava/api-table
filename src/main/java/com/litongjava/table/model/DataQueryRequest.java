package com.litongjava.table.model;

import com.litongjava.db.TableInput;

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
  private Integer pageNo;
  private Integer pageSize;

  public DataQueryRequest(TableInput kv) {
    this.columns = (String) kv.remove(TableInput.columns);
    this.orderBy = (String) kv.remove(TableInput.orderBy);
    this.groupBy = (String) kv.remove(TableInput.groupBy);
    this.pageNo = (Integer) kv.remove(TableInput.pageNo);
    this.pageSize = (Integer) kv.remove(TableInput.pageSize);

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
