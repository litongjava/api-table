package com.litongjava.table.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class StringArrayConverter implements Converter<String[]> {
  @Override
  public Class<?> supportJavaTypeKey() {
    return String[].class;
  }

  @Override
  public CellDataTypeEnum supportExcelTypeKey() {
    return CellDataTypeEnum.STRING;
  }

  @Override
  public CellData<String> convertToExcelData(String[] value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
    return new CellData<>(String.join(",", value));
  }

  @Override
  public String[] convertToJavaData(@SuppressWarnings("rawtypes") CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
    return cellData.getStringValue().split(",");
  }
}
