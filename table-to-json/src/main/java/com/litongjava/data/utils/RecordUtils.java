package com.litongjava.data.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.litongjava.jfinal.plugin.activerecord.Record;

public class RecordUtils {
  public static List<List<Object>> getListData(List<Record> records, int size) {
    List<List<Object>> columnValues = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      Object[] columnValuesForRow = records.get(i).getColumnValues();
      for (int j = 0; j < columnValuesForRow.length; j++) {
        if (columnValuesForRow[j] instanceof BigInteger) {
          columnValuesForRow[j] = columnValuesForRow[j].toString();
        }
      }
      List<Object> asList = Arrays.asList(columnValuesForRow);
      columnValues.add(asList);
    }
    return columnValues;
  }

}
