package com.litongjava.table.model;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DbPage<T> {
  
  private Integer total;
  private List<T> list;

}
