package com.litongjava.table.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.litongjava.db.activerecord.Record;
import com.litongjava.kit.RecordUtils;
import com.litongjava.table.convert.LocalDateTimeConverter;
import com.litongjava.table.convert.TimestampStringConverter;

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
  public static <T> void write(OutputStream outputStream, String filename, String sheetName, Class<T> head,
      List<T> data) throws IOException {
    // 输出 Excel
    if (head != null) {
      getExcelWriteBuilder(outputStream)
          // 写入数据到sheet
          .sheet(sheetName).head(head).doWrite(data);
    } else {
      getExcelWriteBuilder(outputStream).sheet(sheetName).doWrite(data);
    }
  }

  public static void write(OutputStream outputStream, String sheetName, List<Record> records) {
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
    List<List<Object>> columnValues = RecordUtils.getListData(records, size);

    getExcelWriteBuilder(outputStream)
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
  public static void write(OutputStream outputStream, Map<String, List<Record>> allTableData) {
    // 写入
    ExcelWriterBuilder excelWriterBuilder = getExcelWriteBuilder(outputStream);

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
        continue;
      }
      List<List<String>> heads = head(columnNames);

      List<List<Object>> columnValues = RecordUtils.getListData(records, listSize);

      // 写入数据
      // excelWriterBuilder.sheet(sheetName).head(heads).doWrite(columnValues);
      // excelWriterBuilder.sheet().sheetName(sheetName).head(heads).doWrite(columnValues);
      // excelWriterBuilder.sheet().sheetName(sheetName).head(heads).doFill(columnValues);
      WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).head(heads).build();
      excelWriter.write(columnValues, writeSheet);
    }
    excelWriter.finish();

  }

  private static ExcelWriterBuilder getExcelWriteBuilder(OutputStream outputStream) {
    ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream)
        // 不要自动关闭，交给 Servlet 自己处理
        .autoCloseStream(false)
        // 基于 column 长度，自动适配。最大 255 宽度
        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
        // 日期格式转换
        .registerConverter(new LocalDateTimeConverter()).registerConverter(new TimestampStringConverter());
    return excelWriterBuilder;
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
