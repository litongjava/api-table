package nexus.io.table.utils;

import java.util.List;

import com.jfinal.kit.Kv;

import nexus.io.db.TableResult;
import nexus.io.db.activerecord.Row;
import nexus.io.kit.RowUtils;
import nexus.io.model.page.DbPage;
import nexus.io.model.page.Page;

public class TableResultUtils {

  public static TableResult<Kv> recordToKv(TableResult<Row> jsonBean) {
    Row data = jsonBean.getData();
    if (data == null) {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg());
    } else {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), RowUtils.toKv(data, false));
    }
  }

  public static TableResult<Kv> recordToKv(TableResult<Row> jsonBean, boolean underscoreToCamel) {
    Row data = jsonBean.getData();
    if (data == null) {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg());
    } else {
      return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), RowUtils.toKv(data, underscoreToCamel));
    }
  }

  public static TableResult<DbPage<Kv>> pageToDbPage(TableResult<Page<Row>> jsonBean, boolean underscoreToCamel) {
    int totalRow = jsonBean.getData().getTotalRow();
    List<Row> list = jsonBean.getData().getList();
    List<Kv> newList = RowUtils.toKv(list, underscoreToCamel);

    DbPage<Kv> pageData = new DbPage<>();
    pageData.setTotal(totalRow);
    pageData.setList(newList);
    return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), pageData);
  }

  public static TableResult<List<Kv>> recordsToKv(TableResult<List<Row>> jsonBean, boolean underscoreToCamel) {
    return new TableResult<>(jsonBean.getCode(), jsonBean.getMsg(), RowUtils.toKv(jsonBean.getData(), underscoreToCamel));
  }
}
