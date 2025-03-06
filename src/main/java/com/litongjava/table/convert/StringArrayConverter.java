package com.litongjava.table.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
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
  public String[] convertToJavaData(@SuppressWarnings("rawtypes") ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
    return cellData.getStringValue().split(",");
  }
  
  @Override
  public WriteCellData<String> convertToExcelData(String[] value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
    return new WriteCellData<>(String.join(",", value));
  }

 
}
