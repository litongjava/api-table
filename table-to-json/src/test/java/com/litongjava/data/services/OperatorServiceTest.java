package com.litongjava.data.services;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class OperatorServiceTest {

  @Test
  public void test() {
    String fieldName = "column";
    String operator = "IN";
    Object[] valueArray = { 1, 2, 3, 4, 5 };

    String format = "%s %s (%s)";
    List<String> nCopies = Collections.nCopies(valueArray.length, "?");
    String questionMarks = String.join(",", nCopies);

    String sql = String.format(format, fieldName, operator, questionMarks);
    System.out.println(sql);

  }

}
