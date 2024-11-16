package com.litongjava.table.utils;

import java.util.List;

import com.jfinal.kit.Kv;
import com.litongjava.db.TableResult;
import com.litongjava.db.activerecord.Record;
import com.litongjava.kit.RecordUtils;
import com.litongjava.model.page.DbPage;
import com.litongjava.model.page.Page;

public class TableResultUtils {

  public static TableResult<Kv> recordToKv(TableResult<Record> jsonBean) {
    Record data = jsonBean.getData();
    if (data == null) {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg());
    } else {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), RecordUtils.recordToKv(data, false));
    }
  }

  public static TableResult<Kv> recordToKv(TableResult<Record> jsonBean, boolean underscoreToCamel) {
    Record data = jsonBean.getData();
    if (data == null) {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg());
    } else {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), RecordUtils.recordToKv(data, underscoreToCamel));
    }
  }

  public static TableResult<DbPage<Kv>> pageToDbPage(TableResult<Page<Record>> jsonBean, boolean underscoreToCamel) {
    int totalRow = jsonBean.getData().getTotalRow();
    List<Record> list = jsonBean.getData().getList();
    List<Kv> newList = RecordUtils.recordsToKv(list, underscoreToCamel);

    DbPage<Kv> pageData = new DbPage<>();
    pageData.setTotal(totalRow);
    pageData.setList(newList);
    return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), pageData);
  }

  public static TableResult<List<Kv>> recordsToKv(TableResult<List<Record>> jsonBean, boolean underscoreToCamel) {
    return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), RecordUtils.recordsToKv(jsonBean.getData(), underscoreToCamel));
  }
}
