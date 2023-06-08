package com.litongjava.data.vo;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class AlarmAiExcelVO {

  @ExcelProperty("id")
  private Long id;

  @ExcelProperty("报警时间")
  private LocalDateTime date;

  @ExcelProperty("报警类型")
  private Integer type;

  @ExcelProperty("预警内容")
  private String text;

  @ExcelProperty("施工区域")
  private String area;

  @ExcelProperty("施工船舶")
  private String shipName;

  @ExcelProperty("图片")
  private String img;

  @ExcelProperty("创建时间")
  private LocalDateTime createTime;

}
