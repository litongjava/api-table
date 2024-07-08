package com.litongjava.table.utils;

import java.util.List;

import com.jfinal.kit.Kv;
import com.litongjava.jfinal.plugin.activerecord.Page;
import com.litongjava.jfinal.plugin.activerecord.Record;
import com.litongjava.table.model.TableResult;
import com.litongjava.table.model.DbPage;

public class DbJsonBeanUtils {
  public static TableResult<DbPage<Kv>> pageToDbPage(TableResult<Page<Record>> jsonBean, boolean underscoreToCamel) {
    int totalRow = jsonBean.getData().getTotalRow();
    List<Record> list = jsonBean.getData().getList();
    List<Kv> newList = KvUtils.recordsToKv(list, underscoreToCamel);

    DbPage<Kv> pageData = new DbPage<>();
    pageData.setTotal(totalRow);
    pageData.setList(newList);

    return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), pageData);

  }

  public static TableResult<Kv> recordToKv(TableResult<Record> jsonBean) {
    Record data = jsonBean.getData();
    if (data == null) {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg());
    } else {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), KvUtils.recordToKv(data, false));
    }

  }

  public static TableResult<List<Kv>> recordsToKv(TableResult<List<Record>> jsonBean, boolean underscoreToCamel) {
    return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), KvUtils.recordsToKv(jsonBean.getData(), underscoreToCamel));
  }
}
