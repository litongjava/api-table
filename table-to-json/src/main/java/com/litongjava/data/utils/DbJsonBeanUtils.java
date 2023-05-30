package com.litongjava.data.utils;

import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.model.DbPage;

public class DbJsonBeanUtils {

  public static DbJsonBean<DbPage<Kv>> pageToDbPage(DbJsonBean<Page<Record>> pageResponse) {
    
    int totalRow = pageResponse.getData().getTotalRow();
    List<Record> list = pageResponse.getData().getList();
    List<Kv> newList = KvUtils.tecordToKv(list);
    
    DbPage<Kv> pageData = new DbPage<Kv>();
    pageData.setTotal(totalRow);
    pageData.setList(newList);

    return new DbJsonBean<DbPage<Kv>>(pageResponse.getCode(), pageResponse.getMsg(), pageData);
  }

}
