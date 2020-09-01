var projectName = '/litongjava-table-to-json';
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
//var w = ($(window).width() * 0.7);
//var h = ($(window).height() - 50);
  var width = (window.cols[0].length - 2 )* 50;
  debugger;
  var index = layer.open({
    type: 2,
    title: title,
    area: ['60%', width+'px'],
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
 * 弹出没有登录提示
 */
function toLogin() {
  //提示,调整到登录界面
  layer.alert('检测到没有登录,请重新登录', { skin: 'layui-layer-molv', closeBtn: 0, },
    function() {
      layer.close(layui.index);
      saveToSessionStorage({});
      var url = projectName + '/login.html';
      window.parent.open(url, '_self');
    });
}
/**
 * 如果没有登录,弹出提示框,依赖user.js
 */
$.ajaxSetup({
  complete: function(xMLHttpRequest, textStatus) {
    if(textStatus == "parsererror") {
      toLogin();
    }
    try {
      var resp = JSON.parse(xMLHttpRequest.responseText);
    } catch(error) {
      toLogin();
    }

    if(textStatus == "error") {
      $.messager.alert('提示信息', "请求超时！请稍后再试！", 'info');
    }
  }
});

function layuiTableRender(uri, title, cols, formPageName, table, layer, form, laypage) {

  var listUrl = uri + "/list?tableName=" + tableName;
  if(orderBy) {
    listUrl += "&orderBy=" + orderBy + "&isAsc=" + isAsc;
  }
  //显示加载进度
  var index = layer.load(1);
  table.render({
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
      $('th').css({ 'background-color': '#272822', 'color': '#fff', 'font-weight': 'bold' });
      layer.close(index);
    }
  });

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
          layer.msg(resp.msg, { icon: 0, time: 1000 });
          console.log(resp.msg);
          return false;
        }
      },
      error: function(resp) {
        layer.alert(resp.responseText, { icon: 2 });
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