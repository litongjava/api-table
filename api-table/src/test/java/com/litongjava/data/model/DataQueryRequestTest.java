package com.litongjava.data.model;

import org.junit.Test;

public class DataQueryRequestTest {

  @Test
  public void test_StrigToBoolean() {
    String isAsc="false";
    boolean parseBoolean = Boolean.parseBoolean(isAsc);
    
//    System.out.println((boolean)isAsc);
//    System.out.println((Boolean)isAsc);
    System.out.println(parseBoolean);
    
  }

}
