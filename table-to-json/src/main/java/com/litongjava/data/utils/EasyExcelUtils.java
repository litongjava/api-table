package com.litongjava.data.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

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
  public static <T> void write(OutputStream ouputStream, String filename, String sheetName, Class<T> head,
      List<T> data) throws IOException {
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
}
