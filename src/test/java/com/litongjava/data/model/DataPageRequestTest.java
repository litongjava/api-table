package com.litongjava.data.model;

import org.junit.Test;

public class DataPageRequestTest {

  @Test(expected = NumberFormatException.class)
  public void emptyPageNumberIsNotAnInteger() {
    Integer.parseInt("");
  }

}
