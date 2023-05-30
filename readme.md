## table-to-json

### 简介

table-to-json 是自动化的Java CRUD框架

创建完成表之后,前端传入表名到后端进行curd操作只需要将表名传入后台,后台自动返回json数据

### 软件架构
完全使用jfinal开发 jfinal+mysql,目前仅支持mysql,
### 演示视频

https://www.bilibili.com/video/bv1cV411m752

### 导入eclipse
这是一个maven项目,直接导入eclipse即可

### API接口
1.导入数据  
创建数据库  

```
CREATE DATABASE litongjava_table_to_json DEFAULT CHARACTER SET utf8
```
导入数据
```
litongjava-table-to-json\src\main\db\ask-log.sql
```
2.获取表信息接口  
2.1.获取所有表  
请求地址  
http://192.168.0.10:11023/litongjava-table-to-json/api/db/tables  
返回

```
[
  {
    "Tables_in_litongjava_table_to_json": "om_log_ask_detail_202008"
  }
]
```

2.2.获取表字段  
请求地址  
http://192.168.0.10:11023/litongjava-table-to-json/api/db/cloumns?tableName=om_log_ask_detail_202008  
返回

```
[
  {
    "Field": "ID",
    "Type": "varchar(96)",
    "Null": "YES",
    "Extra": "",
    "Default": null,
    "Key": ""
  },
]
```

2.3.判断字段是否在表中  
请求地址  
http://192.168.0.10:11023/litongjava-table-to-json/api/db/isExists?cloumn=ID&tableName=om_log_ask_detail_202008  
返回  
true  

2.4.清楚表字段缓存  
在判断字段是否在表中时会缓存表字段,下次不会从数据库中查询,如果修改了表结构,需要执行clear接口  
http://192.168.0.10:11023/litongjava-table-to-json/api/db/clearColumns?tableName=om_log_ask_detail_202008  
返回  
true  

2.5.获取表主键  
如果表不存在主键,返回id  
如果表是复合主键返回1个  
请求  
http://192.168.0.10:11023/litongjava-table-to-json/api/db/getPrimaryKey?tableName=om_log_ask_detail_202008  
返回  
id  
3.CURD接口  
3.1.查询list和搜索  
3.1.1.分页查询  
请求参数  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/list?pageNo=1&pageSize=10&tableName=om_log_ask_detail_202008  
请求参数

```
key	isRequired	desc	remark
pageNo	false	默认1	x
pageSize	false	默认10	x
tableName	true	表名	x
columns	false	字段名	x
orderBy	false	排序字段	x
isAsc	false	是否升序排序	x
xx	false	搜索字段	x
start.xxx	false	xxx是字段名 用于范围查询	eg.start.create_time
end.xxx	false	xxx是字段名 用于范围查询	eg.end.create_time
like.xxx	false	xxx 字典名,用于模糊查询	like.QUESTION
可以使用多个字段组合查询,也可以使用单一字段组合查询
```

返回信息

```
{
  "count": 54472,
  "code": 0,
  "data": [
    {
      "CATEGORY_ID": null,
      "USER_ID": "7ac110d480b343789c87a35e8d3933cb",
      "KEYWORD": "社保卡 注销 ",
      "SENTIMENT": 0,
      "AUDIO_PATH": null,
      "QUESTION": "社保卡注销",
      "PLATFORM": "weixin",
      "STAT_KEY": "20200801",
      "MATCHED_QUESTION": null,
      "RECOG_COST": 0.0,
      "PROCESS_COST": 7,
      "ID": "001596211526000191790242ac110006",
      "MODULE_ID": null,
      "ANSWER_TYPE": 11,
      "IVR_APPID": null,
      "ANSWER": "您的意思我没太明白，请您换一个问法试试，例如“如何办理居民养老”，或许我就能明白啦！",
      "RECOG_DATA_ID": null,
      "QUESTION_TYPE": 8,
      "CUSTOM3": null,
      "CUSTOM1": null,
      "CUSTOM2": null,
      "FAQ_ID": "0015331119870071857788d7f6ae76b3",
      "RECOG_ENGINE": null,
      "FAQ_NAME": null,
      "BRAND": "",
      "VISIT_TIME": "2020-08-01 00:05:26",
      "RECOG_CONFIDENCE": 0,
      "EX": null,
      "CITY": "",
      "RECOG_GRAMMAR": null,
      "IP_ADDRESS": null,
      "INPUT_TYPE": 1,
      "SESSION_ID": "295d415c87524e59a7f988325432a175",
      "SIMILARITY": 0,
      "TAGS": null
     },
      ....
  ],
  "msg": "执行成功"
}
```

3.1.2.排序查询  
排序字段是VISIT_TIME  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/list?pageNo=1&pageSize=10&tableName=om_log_ask_detail_202008&orderBy=VISIT_TIME&isAsc=false  

3.1.3.范围查询  
时间范围查询  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/list?pageNo=1&pageSize=10&tableName=om_log_ask_detail_202008&orderBy=VISIT_TIME&isAsc=false&start.VISIT_TIME=2020-07-20 00:00:00&end.VISIT_TIME=2020-08-31 23:59:59  

时间范围查询+指定字段范围查询  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/list?pageNo=1&pageSize=10&tableName=om_log_ask_detail_202008&orderBy=VISIT_TIME&isAsc=false&start.VISIT_TIME=2020-07-20 00:00:00&end.VISIT_TIME=2020-08-31 23:59:59&start.PROCESS_COST=7&start.PROCESS_COST=100  

3.1.4.指定字段搜索  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/list?pageNo=1&pageSize=10&tableName=om_log_ask_detail_202008&orderBy=VISIT_TIME&isAsc=false&start.VISIT_TIME=2020-07-20 00:00:00&end.VISIT_TIME=2020-08-31 23:59:59&start.PROCESS_COST=7&start.PROCESS_COST=100&QUESTION_TYPE=8  

3.1.5.模糊查询  
为了最大兼容性,使用like查询会丧失索引  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/list?pageNo=1&pageSize=10&tableName=om_log_ask_detail_202008&orderBy=VISIT_TIME&isAsc=false&like.QUESTION=失业补助金  
3.1.6.返回指定字段  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/list?pageNo=1&pageSize=10&tableName=om_log_ask_detail_202008&columns=QUESTION,ANSWER&orderBy=VISIT_TIME&isAsc=false&like.QUESTION=失业补助金  

3.2.根据ID查询  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/getById?tableName&id=001598884722000998170242ac110006

3.3.根据ID删除  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/removeById?tableName=om_log_ask_detail_202008&id=001598884722000998170242ac110006

3.4.新增数据  
请求地址  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/saveOrUpdate?tableName=om_log_ask_detail_202008  
请求参数    

```
key	isRequired	desc	eg
tableName	true	表名	x
xxx	true	字段名和字段值	x
```

请求参数样例  

```
USER_ID: 1e324e43aa5949e097c7f1af16f70013
SESSION_ID: c4da1e35a33d421ea4cda3e88fe219aa
IP_ADDRESS: 
QUESTION: 我申请的失业补助通过了，但是我想知道会补发几个月的给我
ANSWER: 您的意思我没太明白，请您换一个问法试试，例如“如何办理居民养老”，或许我就能明白啦！
FAQ_ID: 
FAQ_NAME: 
VISIT_TIME: 2020-08-31 22:49:54
QUESTION_TYPE: 8
ANSWER_TYPE: 0
KEYWORD: 我 申请 失业 补助 通过 但是 我想 知道 会 补发 几个月 给 我 
PLATFORM: weixin
```



3.5.更新数据

更新数据和保存时间是同一个接口,不同的是更新数据需要传入id  
请求地址  
http://192.168.0.10:11023/litongjava-table-to-json/api/form/saveOrUpdate?tableName=om_log_ask_detail_202008  

请求数据

```
ID: 001598885394000998480242ac110006
USER_ID: 1e324e43aa5949e097c7f1af16f70013
SESSION_ID: c4da1e35a33d421ea4cda3e88fe219aa
IP_ADDRESS: 
QUESTION: 我申请的失业补助通过了，但是我想知道会补发几个月的给我
ANSWER: 您的意思我没太明白，请您换一个问法试试，例如“如何办理居民养老”，或许我就能明白啦！
FAQ_ID: 
FAQ_NAME: 
VISIT_TIME: 2020-08-31 22:49:54
QUESTION_TYPE: 8
ANSWER_TYPE: 0
KEYWORD: 我 申请 失业 补助 通过 但是 我想 知道 会 补发 几个月 给 我 
PLATFORM: weixin
```




### 整合layui

整合示例\src\main\resources\litongjava-table-to-json\backend\askLog

需要修改List.html中的下面的值和header layui-form中的搜索表单

```
var title = "问答日志";
var tableName = "om_log_ask_detail_202008"
var uri = projectName + '/api/form';
document.title = title + '数据列表';
var formPageName = "askLogForm.html";
var orderBy = 'VISIT_TIME';
var isAsc = "false";
var idField="file_id";

var cols = [
  [
    { checkbox: true }, //开启多选框
    { field: 'ID', width: 80, title: 'ID' },
    { field: 'USER_ID', width: 100, title: 'USER_ID' },
    { field: 'SESSION_ID', width: 100, title: 'SESSION_ID' },
    { field: 'IP_ADDRESS', width: 100, title: 'IP_ADDRESS' },
    { field: 'QUESTION', width: 100, title: 'QUESTION' },
    { field: 'ANSWER', width: 100, title: 'ANSWER' },
    { field: 'FAQ_ID', width: 100, title: 'FAQ_ID' },
    { field: 'FAQ_NAME', width: 100, title: 'FAQ_NAME' },
    { field: 'VISIT_TIME', width: 100, title: 'VISIT_TIME' },
    { field: 'QUESTION_TYPE', width: 100, title: 'QUESTION_TYPE' },
    { field: 'ANSWER_TYPE', width: 100, title: 'ANSWER_TYPE' },
    { field: 'KEYWORD', width: 100, title: 'KEYWORD' },
    { field: 'PLATFORM', width: 100, title: 'PLATFORM' },
    { field: 'CITY', width: 100, title: 'CITY' },
    { fixed: 'right', width: 250, title: '操作', toolbar: '#operation-btns' }
  ]
];
```

需要修改Form.html中下面的值和from中表单项

```
var title = "问答日志";
var tableName = "om_log_ask_detail_202008"
var uri = projectName + '/api/form';
document.title = title + '数据表单';
var listPageName = "askLogList.html";
```







