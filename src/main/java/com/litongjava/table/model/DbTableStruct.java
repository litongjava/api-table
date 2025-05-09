package com.litongjava.table.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author create by ping-e-lee on 2021年4月14日 上午11:59:05 
 * @version 1.0 
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbTableStruct {
  /**
   * type
   * bigint
  integer
  character varying
  boolean
  text
  jsonb
  timestamp with time zone
  USER-DEFINED
  tsvector
  smallint
  
   */
  private String field, type, isNull, extra, defaultValue, key;
}
