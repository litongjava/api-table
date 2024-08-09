package com.litongjava.table.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.litongjava.db.activerecord.Record;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.server.util.Resps;

import lombok.Cleanup;

public class EasyExcelResponseUtils {
  public static HttpResponse exportRecords(HttpRequest request, String filename, String sheetName, List<Record> records)
      throws IOException {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    EasyExcelUtils.write(outputStream, sheetName, records);

    // 将输出流转换为字节数组
    byte[] bytes = outputStream.toByteArray();

    // 使用 Resps 工具类创建一个包含二维码图片的响应
    HttpResponse response = Resps.bytesWithContentType(request, bytes, "application/vnd.ms-excel;charset=UTF-8");
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    return response;
  }

  public static void exportAllTableRecords(HttpResponse response, String filename,
      LinkedHashMap<String, List<Record>> allTableData) throws IOException {
    @Cleanup
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    EasyExcelUtils.write(outputStream, allTableData);
    byte[] bytes = outputStream.toByteArray();
    Resps.bytesWithContentType(response, bytes, "application/vnd.ms-excel;charset=UTF-8");
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
  }

  /**
   * 自定义导出
   */
  public static <T> HttpResponse export(HttpRequest request, String filename, String sheetName, List<Record> records,
      Class<T> clazz) throws UnsupportedEncodingException, IOException {
    List<T> exportDatas = records.stream().map(e -> e.toBean(clazz)).collect(Collectors.toList());

    @Cleanup
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    EasyExcelUtils.write(outputStream, filename, sheetName, clazz, exportDatas);
    byte[] bytes = outputStream.toByteArray();
    HttpResponse response = Resps.bytesWithContentType(request, bytes, "application/vnd.ms-excel;charset=UTF-8");
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    return response;
  }

}
