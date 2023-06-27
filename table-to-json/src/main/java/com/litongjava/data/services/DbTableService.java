package com.litongjava.data.services;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;

public class DbTableService {

  private DbService dbService = new DbService();

  /**
   * 
   * @return
   */
  public String[] getAllTableNames() {
    List<Record> tables = dbService.tables();
    int size = tables.size();
    String[] retval = new String[size];
    for (int i = 0; i < size; i++) {
      retval[i] = (String) tables.get(i).getColumnValues()[0];
    }
    return retval;
  }

}
