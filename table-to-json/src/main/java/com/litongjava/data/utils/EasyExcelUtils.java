package com.litongjava.data.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.jfinal.plugin.activerecord.Record;

/**
 * Excel 工具类
 *
 * @author litongjava
 */
public class EasyExcelUtils {

  /**
   * 将列表以 Excel 响应给前端
   *
   * @param response 响应
   * @param filename 文件名
   * @param sheetName Excel sheet 名
   * @param head Excel head 头
   * @param data 数据列表哦
   * @param <T> 泛型，保证 head 和 data 类型的一致性
   * @throws IOException 写入失败的情况
   */
  public static <T> void write(OutputStream ouputStream, String filename, String sheetName, Class<T> head, List<T> data)
      throws IOException {
    // 输出 Excel
    if (head != null) {
      EasyExcel.write(ouputStream, head)
          // 不要自动关闭，交给 Servlet 自己处理
          .autoCloseStream(false)
          // 基于 column 长度，自动适配。最大 255 宽度
          .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
          // 写入数据到sheet
          .sheet(sheetName).doWrite(data);
    } else {
      EasyExcel.write(ouputStream).autoCloseStream(false)
          .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(data);
    }
  }

  public static void write(ServletOutputStream outputStream, String sheetName, List<Record> records) {
    // 获取head
    String[] columnNames = null;
    int size = records.size();
    if (size > 0) {
      Record record = records.get(0);
      columnNames = record.getColumnNames();
    } else {
      return;
    }
    List<List<String>> heads = head(columnNames);

    // 获取body
    List<List<Object>> columnValues = getListData(records, size);

    // 写入
    EasyExcel.write(outputStream)
        //
        .autoCloseStream(false)
        //
        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
        //
        .sheet(sheetName)
        //
        .head(heads)
        //
        .doWrite(columnValues);

  }

  /**
   * 所有表格的数据
   * @param outputStream
   * @param allTableData
   */
  public static void write(ServletOutputStream outputStream, Map<String, List<Record>> allTableData) {
    // 写入
    ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream)
        //
        .autoCloseStream(false)
        //
        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
    ExcelWriter excelWriter = excelWriterBuilder.build();

    Set<String> sheetNames = allTableData.keySet();
    for (String sheetName : sheetNames) {
      List<Record> records = allTableData.get(sheetName);
      int listSize = records.size();
      // 获取head
      String[] columnNames = null;
      if (listSize > 0) {
        Record record = records.get(0);
        columnNames = record.getColumnNames();
      } else {
        return;
      }
      List<List<String>> heads = head(columnNames);

      List<List<Object>> columnValues = getListData(records, listSize);

      // 写入数据
      // excelWriterBuilder.sheet(sheetName).head(heads).doWrite(columnValues);
      // excelWriterBuilder.sheet().sheetName(sheetName).head(heads).doWrite(columnValues);
      // excelWriterBuilder.sheet().sheetName(sheetName).head(heads).doFill(columnValues);
      WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).head(heads).build();
      excelWriter.write(columnValues, writeSheet);
    }
    excelWriter.finish();

  }

  /**
   * 获取数据部分
   * @param records
   * @param size
   * @return
   */
  private static List<List<Object>> getListData(List<Record> records, int size) {
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

  public static List<List<String>> head(String... heads) {
    // 写入表头
    List<List<String>> list = new ArrayList<>();
    for (String headString : heads) {
      List<String> head = new ArrayList<>();
      head.add(headString);
      list.add(head);
    }
    return list;
  }

}
