package com.litong.jfinal.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Aop;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;

/**
 * @author litong
 * @date 2020年9月23日_下午8:02:06 
 * @version 1.0 
 * @desc
 */
public class LayuiService {

  private DbService dbService = Aop.get(DbService.class);

  /**
   * 返回cols的值
  var cols = [
  [
    { checkbox: true }, //开启多选框
    { field: 'id', width: 100, title: 'id' },
    { field: 'name', width: 100, title: '名称' },
    { field: 'jdbc_url', width: 100, title: '数据库地址' },
    { field: 'jdbc_user', width: 100, title: '用户名' },
    { field: 'jdbc_pswd', width: 100, title: '密码' },
    { field: 'create_time', width: 160, title: '创建时间' },
    { field: 'update_time', width: 160, title: '更新时间' },
    { field: 'remarks', width: 100, title: '备注' },
    { fixed: 'right', width: 250, title: '操作', toolbar: '#operation-btns' }
  ]
  ];
   * @param kv
   * @return
   */
  public JSONArray getTableCols(Kv kv) {
    // 获取表明
    String tableName = kv.getStr("tableName");
    // 获取表中所有字段
    List<Record> cloumns = dbService.cloumns(tableName);
    // 获取 json array
    JSONArray firstArray = getTableColsByColumns(cloumns);
    // 返回值返回
    JSONArray cols = new JSONArray(1);
    cols.add(firstArray);
    return cols;
  }

  /**
   * 根据列名返回数组
   * @param cloumns
   * @return
   */
  private JSONArray getTableColsByColumns(List<Record> cloumns) {
    JSONArray jsonArray = new JSONArray();
    JSONObject checkbox = new JSONObject(1);
    checkbox.put("checkbox", true);
    jsonArray.add(checkbox);

    for (Record record : cloumns) {
      String field = record.getStr("Field");
      String type = record.getStr("Type");
      int width = 100;
      if (type.equals("datetime")) {
        width = 200;
      }
      JSONObject colsObject = getField(field, width, field);
      jsonArray.add(colsObject);
    }
    
    JSONObject operationBtns=new JSONObject();
    operationBtns.put("fixed", "right");
    operationBtns.put("width", 250);
    operationBtns.put("title", "操作");
    operationBtns.put("toolbar", "#operation-btns");
    jsonArray.add(operationBtns);

    return jsonArray;
  }

  private JSONObject getField(String field, int width, String title) {
    JSONObject id = new JSONObject(3);
    id.put("field", field);
    id.put("width", width);
    id.put("title", title);
    return id;
  }
}
