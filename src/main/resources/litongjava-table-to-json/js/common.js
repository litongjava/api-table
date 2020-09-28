/**
 * 格式化时间
 * @param {Object} timestamp
 * @param {Object} b
 */
formatTime = function(timestamp, b) {
  if(!timestamp) {
    return '';
  }
  if(b == 'yyyy-MM-dd hh:mm:ss') {
    return getTimeYYYYMMDDHHMMSS(timestamp);
  } else if(b == 'MM-dd hh:mm:ss') {
    return getTimeMMDDHHMMSS(timestamp);
  }
  var a = new Date(timestamp);
  var c = {
    "M+": a.getMonth() + 1,
    "d+": a.getDate(),
    "h+": a.getHours(),
    "m+": a.getMinutes(),
    "s+": a.getSeconds(),
    "q+": Math.floor((a.getMonth() + 3) / 3),
    S: a.getMilliseconds()
  };
  /(y+)/.test(b) && (b = b.replace(RegExp.$1, (a.getFullYear() + "").substr(4 - RegExp.$1.length)));
  for(var d in c) new RegExp("(" + d + ")").test(b) && (b = b.replace(RegExp.$1, 1 == RegExp.$1.length ? c[d] : ("00" + c[d]).substr(("" + c[d]).length)));
  return b
}

/**
 * 获取请求参数
 * @param {Object} variable
 */
function getParamter(variable) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for(var i = 0; i < vars.length; i++) {
    var pair = vars[i].split("=");
    if(pair[0] == variable) {
      return pair[1];
    }
  }
  return(false);
}

function layerOpenForm(layer, title, content) {
  // 减去2个字段是多选和操作,一个字段的高度是54 create_time和update_time2个字段用于标题栏和下面的操作栏
  var width = (window.cols[0].length - 1) * 54;
  var index = layer.open({
    type: 2,
    title: title,
    area: ['60%', width + 'px'],
    fix: false,
    maxmin: true,
    shadeClose: true,
    shade: 0.4,
    skin: 'layui-layer-lan',
    content: content,
  });
}

function layer_close() {
  var index = parent.layer.getFrameIndex(window.name);
  if(index) {
    parent.layer.close(index);
  } else { //关闭当前界面
    window.opener = null;
    window.open('', '_self');
    window.close();
  }
}

/**
 * 获取data中第一个值
 * @param {Object} data
 */
function getIdValue(data) {
  return data[idField];
}

/**
 * 如果没有登录,弹出提示框,依赖user.js
 */
$.ajaxSetup({
  complete: function(xMLHttpRequest, textStatus) {
    if(textStatus == "parsererror") {
      toLogin(layer);
    }
    try {
      var resp = JSON.parse(xMLHttpRequest.responseText);
    } catch(error) {
      toLogin(layer);
    }

    if(textStatus == "error") {
      $.messager.alert('提示信息', "请求超时！请稍后再试！", 'info');
    }
  }
});

/**
 * 添加页被激活后自动刷新
 * @param {Object} table
 */
function addEvenetListenerForFlush(table) {
  // table也别激活后重新刷新数据
  var  hiddenProperty = 'hidden' in document  ?  'hidden'  : 'webkitHidden' in document  ? 'webkitHidden' : 'mozHidden' in document ? 'mozHidden' : null;
  var visibilityChangeEvent = hiddenProperty.replace(/hidden/i,  'visibilitychange');
  var  onVisibilityChange = function() {    
    if (!document[hiddenProperty])  {
      //console.log('页面激活');//刷新table数据
      if(table) {
        table.reload('data-table');    
      }
    }
  }
  document.addEventListener(visibilityChangeEvent,  onVisibilityChange);
}

function exportExcel(table, exportData) {
  if(!exportData) {
    layer.msg("空数据,无需导出");
    return false;
  }
  //加载插件
  var excel = layui.excel;

  //复制一份,防止修改exportData
  var exportDataCopy = exportData.slice(0);

  //表头设置
  var tableHeader = {};
  for(key in table.config.cols[0]) {
    var title = table.config.cols[0][key].title;
    //过滤多选和操作
    if(!title || "操作" === title) {
      continue;
    }

    var field = table.config.cols[0][key].field;
    var templet = table.config.cols[0][key].templet;
    if(templet && templet instanceof Object) {
      for(var i = 0; i < exportData.length; i++) {
        exportDataCopy[i][field] = templet(exportDataCopy[i]);
      }
    }
    if(title === "序号") {
      field = 'LAY_TABLE_INDEX';
    }
    //建立映射
    tableHeader[field] = title;
  }
  //修改序号
  for(var i = 0; i < exportDataCopy.length; i++) {
    exportDataCopy[i]["LAY_TABLE_INDEX"] = i + 1;
  }
  //补全其他表头
  for(key in exportDataCopy[0]) {
    if(!(key in tableHeader)) {
      if(key === 'LAY_TABLE_INDEX') {
        tableHeader[key] = '序号';
      } else if(key === 'is_del') {
        tableHeader[key] = '是否删除';
      } else {
        tableHeader[key] = key;
      }
    }
  }

  exportDataCopy.unshift(tableHeader);
  //给exportDatd添加序号

  //文件名称
  var title = table.config.title + "导出_" + new Date().toLocaleString() + '.xlsx';
  // 意思是：A列宽度120px，B列40px...,具体宽度根据字段值实际长度设定
  var colConf = excel.makeColConfig({
    'A': 100,
    'B': 100,
    'C': 100,
    'F': 100,
    'G': 100,
    'H': 100
  }, 100);

  console.log(exportDataCopy);
  excel.exportExcel({ sheet1: exportDataCopy }, title, 'xlsx', {
    extend: { '!cols': colConf }
  });
}

function layuiTableRender(uri, title, cols, formPageName, table, layer, form, laypage) {
  var listUrl = uri + "/list?tableName=" + tableName;
  if(orderBy) {
    listUrl += "&orderBy=" + orderBy + "&isAsc=" + isAsc;
  }
  //显示加载进度
  var index = layer.load(1);
  var exportData;
  var tablbRender = table.render({
    title: title,
    id: "data-table",
    elem: '#data-table',
    url: listUrl,
    page: true,
    method: 'post',
    toolbar: '#toolBar',
    limit: 20,
    request: { pageName: 'pageNo', limitName: 'pageSize' },
    response: { statusName: 'code', msgName: 'msg', dataName: 'data', countName: 'count' },
    cols: cols,
    limits: [5, 10, 15, 20, 25, 30, 35, 40, 45, 50],
    done: function(res, curr, count) {
      // #ef6800
      $('th').css({ 'background-color': '#1F395C', 'color': '#fff', 'font-weight': 'bold' });
      layer.close(index);
      exportData = res.data;
    }
  });

  addEvenetListenerForFlush(table);

  table.on('tool(data-table)', function(obj) {
    var data = obj.data;
    switch(obj.event) {
      case 'edit':
        //var data = obj.data;
        window.formData = obj.data;
        layerOpenForm(layer, title + "编辑页面", formPageName);
        break;
      case 'editNewTab':
        var idValue = obj.data.id;
        if(!idValue) {
          idValue = getIdValue(obj.data);
        }

        //layerOpenForm(layer, title + "编辑页面", formPageName);
        window.open(formPageName + "?id=" + idValue, '_blank')
        break;
      case 'del':
        var idValue = getIdValue(obj.data);
        var delIndex = layer.confirm('真的删除id为' + idValue + "的信息吗?", function(delIndex) {

          $.ajax({
            url: uri + "/removeById?tableName=" + tableName,
            data: { "id": idValue },
            type: "post",
            success: function(response) {
              if(response.code == 0) {
                obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                layer.close(delIndex);
                layer.msg('删除成功', { icon: 1, time: 1000 });
              } else {
                layer.msg("删除失败", { icon: 5 });
              }
            }
          });
          layer.close(delIndex);
        });
        break;
    }
  });

  //监听搜索
  form.on('submit(front-search)', function(data) {
    var field = data.field;
    //console.log("filed:", field);
    //执行重载
    table.reload('data-table', {
      where: field
    });
  });
  form.on('switch(status)', function(data) {
    // 得到开关的value值，实际是需要修改的ID值。
    var id = data.value;
    var status = this.checked ? '1' : '0';

    $.ajax({
      type: 'POST',
      url: uri + "/saveOrUpdate?tableName=" + tableName,
      data: { "id": id, "status": status },
      dataType: 'JSON',
      beforeSend: function() {
        index = layer.msg('正在切换中，请稍候', { icon: 16, time: false, shade: 0.8 });
      },
      error: function(resp) {
        console.log(resp);
        layer.alert(resp.responseText, { icon: 2, time: 3000 });
      },
      success: function(resp) {
        if(resp.code == 0) {
          layer.close(index);
          layer.msg('操作成功！', { icon: 1, time: 1000 });
        } else {
          console.log(resp);
          if(resp.msg) {
            layer.msg(resp.msg, { icon: 0, time: 3000 });
          } else {
            layer.msg("返回数据有误", { icon: 0, time: 3000 });
          }
        }
      },
    });
  });
  //事件
  active = {
    batchdel: function() {
      //debugger;
      var checkStatus = table.checkStatus('data-table'),
        checkData = checkStatus.data; //得到选中的数据
      if(checkData.length === 0) {
        return layer.msg('请选择数据');
      }

      layer.confirm('确定删除多条数据吗？', function(index) {
        var ids = new Array();
        for(var i = 0; i < checkData.length; i++) {
          ids.push(getIdValue(checkData[i]));
        }
        $.ajax({
          url: uri + "/removeByIds?tableName=" + tableName,
          type: "post",
          data: { ids: ids },
          success: function(resp) {
            if(resp.code == 0) {
              //执行 Ajax 后重载
              table.reload('data-table');
              layer.msg('删除成功', { icon: 1, time: 1000 });
            } else {
              layer.msg("删除失败:" + resp.msg, { icon: 5 });
              console.log(resp.msg);
            }
          },
        });
      });
    },
    add: function() {
      layerOpenForm(layer, title + "添加页面", formPageName);
    },
    addNewTab: function() {
      //window.location.href = formPageName;
      window.open(formPageName, '_blank')
    },
    exportExcel:function(){
      exportExcel(tablbRender, exportData);
    }
  };
  $("body").on('click', '.layui-btn-container .layui-btn', function() {
    var type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
  });
}

function layuiFormRender(uri, form, layer) {
  form.render();
  var formData;
  var id = getParamter("id");
  if(window.parent.formData) {
    formData = JSON.parse(JSON.stringify(window.parent.formData));
    window.parent.formData = null;
    form.val('data-form', formData);
    //console.log(formData);
  } else if(id) {
    //  console.log(id);
    $.ajax({
      type: 'post',
      url: uri + "/getById?tableName=" + tableName,
      data: { "id": id },
      success: function(resp) {
        if(resp.code > -1) {
          form.val('data-form', resp.data);
        } else {
          layer.msg(resp.msg, { icon: 0, time: 3000 });
          console.log(resp.msg);
          return false;
        }
      },
      error: function(resp) {
        layer.alert(resp.responseText, { icon: 2, time: 3000 });
      }
    });

  }

  form.on('submit(front-submit)', function(data) {
    $.ajax({
      type: 'post',
      url: uri + "/saveOrUpdate?tableName=" + tableName,
      data: data.field,
      success: function(resp) {
        if(resp.code > -1) {
          layer.msg(resp.msg, { icon: 1, time: 1000 });
          if(parent.layui.table) {
            parent.layui.table.reload('data-table');
          }
          layer_close();
        } else {
          layer.msg(resp.msg, { icon: 0, time: 1000 });
          console.log(resp.msg);
          return false;
        }
      },
      error: function(resp) {
        //console.log(resp.responseText);
        layer.alert(resp.responseText, { icon: 2 });
      }
    })
    //防止表单刷新
    return false;
  });
}

function layuiSelectAddChild(url, selectId, form) {
  $.ajax(url).then(function(resp) {
    if(resp.code >= 0) {
      var city = document.getElementById(selectId); //select定义的id
      for(var p in resp.data) {
        var option = document.createElement("option"); // 创建添加option属性
        option.setAttribute("value", p); // 给option的value添加值
        option.innerText = resp.data[p]; // 打印option对应的纯文本 
        city.appendChild(option); //给select添加option子标签
        form.render('select');
      }
    }
  });
}

function layuiSelectAddChildForObject(url, selectId, form) {
  $.ajax(url).then(function(resp) {
    if(resp.code >= 0) {
      var city = document.getElementById(selectId); //select定义的id
      for(var p in resp.data) {
        var option = document.createElement("option"); // 创建添加option属性
        option.setAttribute("value", resp.data[p].siteName); // 给option的value添加值
        option.innerText = resp.data[p].siteName; // 打印option对应的纯文本 
        city.appendChild(option); //给select添加option子标签
        if(form) {
          form.render('select');
        }
      }
    }
  });
}

var timeFormat = function(row) {
  return new Date(row.time).toLocaleString();
}

var statusFormat = function(row) {

}
var request = {
  getParamter: function(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for(var i = 0; i < vars.length; i++) {
      var pair = vars[i].split("=");
      if(pair[0] == variable) {
        return pair[1];
      }
    }
    return(false);
  }
}