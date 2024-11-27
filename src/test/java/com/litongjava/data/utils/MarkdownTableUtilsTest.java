package com.litongjava.data.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.litongjava.db.activerecord.Row;
import com.litongjava.table.utils.MarkdownTableUtils;

public class MarkdownTableUtilsTest {

  @Test
  public void test() {
    List<Row> records = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      Row record = new Row();
      record.set("name", "litong" + i);
      record.set("age", i);
      records.add(record);
    }

    String string = MarkdownTableUtils.to(records);

    System.out.println(string);
  }
}
