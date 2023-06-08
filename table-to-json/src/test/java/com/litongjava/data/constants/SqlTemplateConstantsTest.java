package com.litongjava.data.constants;

import org.junit.Test;

public class SqlTemplateConstantsTest {

  @Test
  public void test() {
    String format = String.format(SqlTemplateConstants.SELECT_COLUMNS_FROM, null, null);
    System.out.println(format);

    format = String.format(SqlTemplateConstants.SELECT_COLUMNS_FROM, null, "table");
    System.out.println(format);

    format = String.format(SqlTemplateConstants.SELECT_COLUMNS_FROM, "f1 f2", "table");
    System.out.println(format);
  }

}
