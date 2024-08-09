package com.litongjava.table.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.postgresql.util.PGobject;

import com.litongjava.db.activerecord.Record;
import com.litongjava.tio.utils.json.JsonUtils;

public class RecordUtils {
  public static List<List<Object>> getListData(List<Record> records, int size) {
    List<List<Object>> columnValues = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      Object[] columnValuesForRow = records.get(i).getColumnValues();
      for (int j = 0; j < columnValuesForRow.length; j++) {
        if (columnValuesForRow[j] instanceof BigInteger) {
          columnValuesForRow[j] = columnValuesForRow[j].toString();
        } else if (columnValuesForRow[j] instanceof Map) {
          columnValuesForRow[j] = JsonUtils.toJson(columnValuesForRow[j]);
        } else if (columnValuesForRow[j] instanceof List) {
          columnValuesForRow[j] = JsonUtils.toJson(columnValuesForRow[j]);
        } else if (columnValuesForRow[j] instanceof PGobject) {
          PGobject pgObject = (PGobject) columnValuesForRow[j];
          columnValuesForRow[j] = JsonUtils.toJson(pgObject.getValue());
        }
      }
      List<Object> asList = Arrays.asList(columnValuesForRow);
      columnValues.add(asList);
    }
    return columnValues;
  }

}
