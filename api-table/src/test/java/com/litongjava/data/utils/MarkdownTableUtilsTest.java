package com.litongjava.data.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.litongjava.jfinal.plugin.activerecord.Record;
import com.litongjava.table.utils.MarkdownTableUtils;

public class MarkdownTableUtilsTest {

  @Test
  public void test() {
    List<Record> records = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      Record record = new Record();
      record.set("name", "litong" + i);
      record.set("age", i);
      records.add(record);
    }

    String string = MarkdownTableUtils.to(records);

    System.out.println(string);
  }
}
