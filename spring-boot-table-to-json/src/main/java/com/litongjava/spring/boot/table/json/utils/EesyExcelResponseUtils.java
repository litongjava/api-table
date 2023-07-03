package com.litongjava.spring.boot.table.json.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.utils.EasyExcelUtils;

import cn.hutool.core.bean.BeanUtil;

public class EesyExcelResponseUtils {
  public static void exportRecords(HttpServletResponse response, String filename, String sheetName,
      List<Record> records) throws IOException {
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    EasyExcelUtils.write(response.getOutputStream(), sheetName, records);
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
  }

  public static void exportAllTableRecords(HttpServletResponse response, String filename,
      LinkedHashMap<String, List<Record>> allTableData) throws IOException {
    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    EasyExcelUtils.write(response.getOutputStream(), allTableData);
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
  }

  /**
   * 自定义导出
   */
  public static <T> void export(HttpServletResponse response, String filename, String sheetName, List<Record> records,
      Class<T> clazz) throws UnsupportedEncodingException, IOException {
    List<Map<String, Object>> collect = records.stream().map(e -> e.toMap()).collect(Collectors.toList());
    List<T> exportDatas = BeanUtil.copyToList(collect, clazz);

    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    EasyExcelUtils.write(response.getOutputStream(), filename, sheetName, clazz, exportDatas);
    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
  }

}
