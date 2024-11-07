package com.litongjava.table.convert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class LocalDateTimeConverter implements Converter<LocalDateTime> {

  @Override
  public Class<?> supportJavaTypeKey() {
    return LocalDateTime.class;
  }

  @Override
  public CellDataTypeEnum supportExcelTypeKey() {
    return CellDataTypeEnum.STRING;
  }

  @Override
  public LocalDateTime convertToJavaData(@SuppressWarnings("rawtypes") CellData cellData,
      ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String stringValue = cellData.getStringValue();
    return LocalDateTime.parse(stringValue, formatter);
  }

  @Override
  public CellData<LocalDateTime> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty,
      GlobalConfiguration globalConfiguration) throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedValue = value.format(formatter);
    return new CellData<>(formattedValue);
  }

}
