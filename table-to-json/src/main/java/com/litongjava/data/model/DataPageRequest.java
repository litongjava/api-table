package com.litongjava.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataPageRequest {
  private Integer pageNo = 1;
  private Integer pageSize = 10;
  private String columns, orderBy;
  private Boolean isAsc;
}
