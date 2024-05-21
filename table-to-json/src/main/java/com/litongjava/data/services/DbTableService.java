package com.litongjava.data.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.litongjava.data.model.DbTableStruct;
import com.litongjava.jfinal.plugin.activerecord.Db;
import com.litongjava.jfinal.plugin.activerecord.Record;

public class DbTableService {

  private DbService dbService = new DbService();
  private PrimaryKeyService primaryKeyService = new PrimaryKeyService();

  /**
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

  public Map<String, Object> proTableColumns(String f) {
    List<DbTableStruct> columns = dbService.getTableColumnsOfMysql(Db.use(), f);
    List<Map<String, Object>> tableItems = new ArrayList<>(columns.size());
    for (DbTableStruct record : columns) {
      String field = record.getField();
      String fieldType = record.getType();

      // {name: 'Name', key: 'name', type: 'el-input', placeholder: '请输入 Name'},
      String name = getName(field);
      String type = getType(fieldType);

      if (type.equals("date")) {
        type = "datetime";
      } else {
        type = "text";
      }

      Map<String, Object> tableItem = new LinkedHashMap<>();
      tableItem.put("title", name);
      tableItem.put("dataIndex", field);
      tableItem.put("valueType", type);
      tableItems.add(tableItem);
    }
    LinkedHashMap<String, Object> table = new LinkedHashMap<String, Object>();
    table.put("items", tableItems);

    Map<String, Object> config = new LinkedHashMap<>();
    config.put("table", table);
    return config;
  }

  /**
   * 获取表格配置信息
   *
   * @param tableName
   * @param lang
   * @return
   */
  public Map<String, Object> getTableConfig(String f, String tableName, String lang) {
    List<DbTableStruct> columns = dbService.getTableColumnsOfMysql(Db.use(), tableName);
    List<Map<String, Object>> queryItems = new ArrayList<>(columns.size());
    List<Map<String, Object>> tableItems = new ArrayList<>(columns.size());
    List<Map<String, Object>> formItems = new ArrayList<>(columns.size());
    Map<String, String> operator = new LinkedHashMap<>();

    for (DbTableStruct record : columns) {
      String field = record.getField();
      String fieldType = record.getType();

      // {name: 'Name', key: 'name', type: 'el-input', placeholder: '请输入 Name'},
      String name = getName(field);
      String key = getKey(field);
      String type = getType(fieldType);

      Map<String, Object> queryItem = new LinkedHashMap<>();
      queryItem.put("name", name);
      queryItem.put("key", key);
      queryItem.put("type", type);
      queryItem.put("show", true);

      Map<String, Object> tableItem = new LinkedHashMap<>();
      tableItem.put("name", name);
      tableItem.put("key", key);
      tableItem.put("type", type);
      tableItem.put("align", "center");
      tableItem.put("show", true);

      Map<String, Object> formItem = new LinkedHashMap<>();
      formItem.put("name", name);
      formItem.put("key", key);
      formItem.put("type", type);
      formItem.put("show", true);

      if ("date".equals(type)) {
        operator.put(key + "Op", "bt");
        queryItem.put("prop", getQueryItemDateProp(lang));
        formItem.put("prop", getFormItemDateProp(lang));
      } else {
        queryItem.put("placeholder", getPlaceholder(name, lang));
        formItem.put("placeholder", getPlaceholder(name, lang));
        if (fieldType.startsWith("varchar")) {
          operator.put(key + "Op", "ew");
        }
      }
      queryItems.add(queryItem);
      tableItems.add(tableItem);
      formItems.add(formItem);
    }

    String primaryKeyName = primaryKeyService.getPrimaryKeyName(tableName);
    String primaryKeyColumnType = primaryKeyService.getPrimaryKeyColumnType(tableName);

    LinkedHashMap<String, Object> query = new LinkedHashMap<String, Object>();
    query.put("show", false);
    query.put("items", queryItems);
    query.put("operator", operator);
    query.put("button", getButton(lang));

    LinkedHashMap<String, Object> toolBar = new LinkedHashMap<String, Object>();
    toolBar.put("show", true);
    toolBar.put("addButtonShow", true);
    toolBar.put("exportButtonShow", true);
    toolBar.put("exportAllButtonShow", true);
    toolBar.put("addButtonName", getAddButtonName(lang));
    toolBar.put("exportButtonName", getExportButtonName(lang));
    toolBar.put("exportAllButtonName", getExportAllButtonName(lang));

    LinkedHashMap<String, Object> table = new LinkedHashMap<String, Object>();
    table.put("selectionShow", true);
    table.put("numberShow", true);
    table.put("items", tableItems);
    table.put("operation", getOperation(lang));

    LinkedHashMap<String, Object> form = new LinkedHashMap<String, Object>();
    form.put("width", "1000px");
    form.put("labelWidth", "150px");
    form.put("items", formItems);
    form.put("button", getFormButton(lang));

    Map<String, Object> config = new LinkedHashMap<>();
    config.put("f", f);
    config.put("idName", primaryKeyName);
    config.put("idType", primaryKeyColumnType);
    config.put("tableAlias", getName(tableName));

    config.put("pageUri", "/table/json/" + f + "/page");
    config.put("getUri", "/table/json/" + f + "/get");
    config.put("createUri", "/table/json/" + f + "/create");
    config.put("updateUri", "/table/json/" + f + "/update");
    config.put("deleteUri", "/table/json/" + f + "/delete");
    config.put("exportExcelUri", "/table/json/" + f + "/export-excel");
    config.put("exportTableExcelUri", "/table/json/" + f + "/export-table-excel");

    config.put("query", query);
    config.put("toolBar", toolBar);
    config.put("table", table);
    config.put("form", form);
    return config;
  }

  private Map<String, String> getButton(String lang) {
    HashMap<String, String> hashMap = new LinkedHashMap<String, String>();
    if ("zh-CN".equals(lang)) {
      hashMap.put("queryButtonName", "查询");
      hashMap.put("resetButtonName", "重置");
    } else {
      hashMap.put("queryButtonName", "Query");
      hashMap.put("resetButtonName", "Reset");
    }

    return hashMap;
  }

  private Object getFormButton(String lang) {
    HashMap<String, String> hashMap = new LinkedHashMap<String, String>();
    if ("zh-CN".equals(lang)) {
      hashMap.put("confimButtonName", "确认");
      hashMap.put("cancelButtonName", "取消");
    } else {
      hashMap.put("confimButtonName", "Comfirm");
      hashMap.put("cancelButtonName", "Cancel");
    }

    return hashMap;
  }

  /**
   * create_time 转为 Create Time
   *
   * @param field
   * @return
   */
  private String getName(String field) {
    return Arrays.stream(field.split("_")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
        .collect(Collectors.joining(" "));
  }

  /**
   * create_time 转为 createTime
   *
   * @param field
   * @return
   */
  private String getKey(String field) {
    String[] words = field.split("_");
    StringBuilder key = new StringBuilder(words[0]);
    for (int i = 1; i < words.length; i++) {
      key.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
    }
    return key.toString();
  }

  private String getPlaceholder(String name, String lang) {
    if ("zh-CN".equals(lang)) {
      return "请输入 " + name;
    } else {
      return "Please Input " + name;
    }
  }

  private String getType(String type) {
    if (type.startsWith("date") || type.startsWith("timestamp") || type.startsWith("datetime")) {
      return "date";
    } else if ("tinyint(1)".equals(type)) {
      return "bool";
    } else {
      return "varchar";
    }
  }

  private Map<String, Object> getQueryItemDateProp(String lang) {
    Map<String, Object> hashMap = new LinkedHashMap<String, Object>();
    hashMap.put("type", "daterange");
    hashMap.put("valueFormat", "yyyy-MM-dd HH:mm:ss");
    hashMap.put("rangeSeparator", "-");

    if ("zh-CN".equals(lang)) {
      hashMap.put("startPlaceholder", "开始日期");
      hashMap.put("endPlaceholder", "结束日期");
    } else {
      hashMap.put("startPlaceholder", "Start Date");
      hashMap.put("endPlaceholder", "End Date");
    }

    hashMap.put("defaultTime", new String[] { "00:00:00", "23:59:59" });

    return hashMap;

  }

  private Map<String, Object> getFormItemDateProp(String lang) {
    Map<String, Object> hashMap = new LinkedHashMap<String, Object>();
    hashMap.put("type", "datetime");
    hashMap.put("valueFormat", "yyyy-MM-dd HH:mm:ss");

    return hashMap;

  }

  private Map<String, Object> getOperation(String lang) {
    HashMap<String, Object> hashMap = new LinkedHashMap<>();
    hashMap.put("show", true);
    hashMap.put("align", "right");
    hashMap.put("updateButtonShow", true);
    hashMap.put("deleteButtonShow", true);

    if ("zh-CN".equals(lang)) {
      hashMap.put("updateButtonName", "修改");
      hashMap.put("deleteButtonName", "删除");
    } else {
      hashMap.put("updateButtonName", "Edit");
      hashMap.put("deleteButtonName", "Delete");
    }

    return hashMap;

  }

  private String getAddButtonName(String lang) {
    if ("zh-CN".equals(lang)) {
      return "增加";
    } else {
      return "Add";
    }
  }

  private String getExportButtonName(String lang) {
    if ("zh-CN".equals(lang)) {
      return "导出";
    } else {
      return "Export";
    }
  }

  private Object getExportAllButtonName(String lang) {
    if ("zh-CN".equals(lang)) {
      return "全部导出";
    } else {
      return "Export All";
    }
  }
}
