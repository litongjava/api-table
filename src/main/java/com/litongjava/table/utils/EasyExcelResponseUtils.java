package com.litongjava.table.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.litongjava.db.activerecord.Record;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.server.util.Resps;

public class EasyExcelResponseUtils {
  public static HttpResponse exportRecords(HttpResponse response, String filename, String sheetName, List<Record> records) {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    EasyExcelUtils.write(outputStream, sheetName, records);

    // 将输出流转换为字节数组
    byte[] bytes = outputStream.toByteArray();

    // 使用 Resps 工具类创建一个包含二维码图片的响应
    Resps.bytesWithContentType(response, bytes, "application/vnd.ms-excel;charset=UTF-8");
    String headeValue = "attachment;filename=";
    try {
      headeValue += URLEncoder.encode(filename, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    response.addHeader("Content-Disposition", headeValue);
    return response;
  }

  public static HttpResponse exportAllTableRecords(HttpResponse response, String filename, LinkedHashMap<String, List<Record>> allTableData) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    EasyExcelUtils.write(outputStream, allTableData);
    byte[] bytes = outputStream.toByteArray();
    Resps.bytesWithContentType(response, bytes, "application/vnd.ms-excel;charset=UTF-8");
    String headeValue = "attachment;filename=";
    try {
      headeValue += URLEncoder.encode(filename, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    response.addHeader("Content-Disposition", headeValue);
    return response;
  }

  /**
   * 自定义导出
   * 
   * @param <T>
   */
  public static <T> HttpResponse export(HttpResponse response, String filename, String sheetName, List<Record> records, Class<T> clazz) {
    List<T> exportDatas = records.stream().map(e -> e.toBean(clazz)).collect(Collectors.toList());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      EasyExcelUtils.write(outputStream, filename, sheetName, clazz, exportDatas);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    byte[] bytes = outputStream.toByteArray();
    Resps.bytesWithContentType(response, bytes, "application/vnd.ms-excel;charset=UTF-8");
    String headeValue = "attachment;filename=";
    try {
      headeValue += URLEncoder.encode(filename, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    response.addHeader("Content-Disposition", headeValue);
    return response;
  }

}
