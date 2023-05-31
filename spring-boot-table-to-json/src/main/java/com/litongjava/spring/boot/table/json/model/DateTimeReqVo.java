package com.litongjava.spring.boot.table.json.model;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.litongjava.spring.boot.table.json.constants.DateConstants;

import lombok.Data;

@Data
public class DateTimeReqVo {
  @DateTimeFormat(pattern = DateConstants.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
  private LocalDateTime[] date;

  @DateTimeFormat(pattern = DateConstants.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
  private LocalDateTime[] createTime;
}