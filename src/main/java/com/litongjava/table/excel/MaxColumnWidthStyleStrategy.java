package com.litongjava.table.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;

public class MaxColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

  private final int maxColumnWidth;
  private final int minColumnWidth;

  public MaxColumnWidthStyleStrategy(int minColumnWidth, int maxColumnWidth) {
    this.minColumnWidth = minColumnWidth;
    this.maxColumnWidth = maxColumnWidth;
  }

  @Override
  protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
      Head head, Integer relativeRowIndex, Boolean isHead) {
    Sheet sheet = writeSheetHolder.getSheet();
    int columnWidth = getCellDataLength(cell);

    if (columnWidth < minColumnWidth) {
      columnWidth = minColumnWidth;
    } else if (columnWidth > maxColumnWidth) {
      columnWidth = maxColumnWidth;
    }

    sheet.setColumnWidth(cell.getColumnIndex(), columnWidth * 256); // poi 列宽单位是 1/256 字符宽
  }

  private int getCellDataLength(Cell cell) {
    if (cell == null) {
      return 0;
    }
    switch (cell.getCellType()) {
    case STRING:
      return cell.getStringCellValue().getBytes().length;
    case BOOLEAN:
      return String.valueOf(cell.getBooleanCellValue()).getBytes().length;
    case NUMERIC:
      return String.valueOf(cell.getNumericCellValue()).getBytes().length;
    default:
      return 0;
    }
  }
}
