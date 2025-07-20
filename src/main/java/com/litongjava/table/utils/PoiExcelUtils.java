package com.litongjava.table.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PoiExcelUtils {

  public static List<Object> readFirstRow(byte[] excelBytes) {
    // 用字节流创建 Workbook
    try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
      Sheet sheet = workbook.getSheetAt(0);
      Row row = sheet.getRow(0);
      List<Object> values = new ArrayList<>();

      if (row != null) {
        for (Cell cell : row) {
          values.add(getCellValue(cell));
        }
      }
      return values;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object getCellValue(Cell cell) {
    switch (cell.getCellType()) {
    case STRING:
      return cell.getStringCellValue();
    case NUMERIC:
      if (DateUtil.isCellDateFormatted(cell)) {
        return cell.getDateCellValue();
      } else {
        return cell.getNumericCellValue();
      }
    case BOOLEAN:
      return cell.getBooleanCellValue();
    case FORMULA:
      return cell.getCellFormula();
    case BLANK:
      return null;
    default:
      return cell.toString();
    }
  }
}
