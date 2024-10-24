package com.litongjava.table.utils;

import java.util.List;

import com.litongjava.db.activerecord.Record;
import com.litongjava.kit.RecordUtils;

public class MarkdownTableUtils {

  public static String to(List<Record> records) {
    // 获取head
    String[] head = null;
    int size = records.size();
    if (size > 0) {
      Record record = records.get(0);
      head = record.getColumnNames();
    } else {
      return null;
    }

    // 获取body
    List<List<Object>> body = RecordUtils.getListData(records, size);

    return toMarkdownTable(head, body);
  }

  public static String toMarkdownTable(String[] head, List<List<Object>> body) {
    StringBuilder table = new StringBuilder();

    // Add header
    table.append("| ");
    for (String column : head) {
      table.append(column).append(" | ");
    }
    table.append("\n");

    // Add separator
    table.append("| ");
    for (int i = 0; i < head.length; i++) {
      table.append("--- | ");
    }
    table.append("\n");

    // Add rows
    for (List<Object> row : body) {
      table.append("| ");
      for (Object cell : row) {
        if (cell != null) {
          table.append(cell.toString()).append(" | ");
        } else {
          table.append("NULL").append(" | ");
        }
      }
      table.append("\n");
    }

    return table.toString();
  }

}
