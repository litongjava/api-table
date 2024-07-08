package com.litongjava.table.vo;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.litongjava.table.constants.DateConstants;

import lombok.Data;

@Data
public class DateTimeReqVo {
  @DateTimeFormat(pattern = DateConstants.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
  private LocalDateTime[] date;

  @DateTimeFormat(pattern = DateConstants.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
  private LocalDateTime[] createTime;

  @DateTimeFormat(pattern = DateConstants.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
  private LocalDateTime[] createDate;
}