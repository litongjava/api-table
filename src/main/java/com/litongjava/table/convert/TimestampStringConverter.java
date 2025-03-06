package com.litongjava.table.convert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class TimestampStringConverter implements Converter<Timestamp> {

  @Override
  public Class<?> supportJavaTypeKey() {
    return Timestamp.class;
  }

  @Override
  public CellDataTypeEnum supportExcelTypeKey() {
    return CellDataTypeEnum.STRING;
  }

  @Override
  public Timestamp convertToJavaData(@SuppressWarnings("rawtypes") ReadCellData cellData,
      ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
    String value = cellData.getStringValue();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return new Timestamp(formatter.parse(value).getTime());
  }

  @Override
  public WriteCellData<String> convertToExcelData(Timestamp value, ExcelContentProperty contentProperty,
      GlobalConfiguration globalConfiguration) throws Exception {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String string = formatter.format(value);
    return new WriteCellData<>(string);
  }

}
