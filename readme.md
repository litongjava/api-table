# ApiTable

在现代应用开发中，简化数据库操作、提高代码复用性和维护性是每个开发者追求的目标。**ApiTable** 作为一个基于 **java-db** 框架的通用数据表操作工具，旨在实现这一目标。本文将详细介绍如何封装 **ApiTableService** 以及如何在控制器中使用 **ApiTableService**，以实现高效、灵活的增删改查（CRUD）操作。

## 目录

1. [前言](#前言)
2. [项目结构与依赖配置](#项目结构与依赖配置)
3. [封装 ApiTableService](#封装-apitableservice)
4. [创建 ApiPostController](#创建-apipostcontroller)
5. [使用 ApiTableService](#使用-apitableservice)
6. [测试接口](#测试接口)
7. [总结](#总结)

## 前言

在实际开发过程中，频繁的数据库操作往往导致大量重复代码的出现，增加了维护难度。通过封装 **ApiTableService**，可以将常用的数据库操作集中管理，提升代码的可读性和可维护性。同时，结合 **tio-boot** 框架的特性，可以进一步简化开发流程，实现高效的接口管理。

## 项目结构与依赖配置

在开始封装 **ApiTableService** 之前，确保项目已经正确配置了所需的依赖。以下是 `pom.xml` 文件中需要添加的依赖项：

```xml
<dependencies>
  <dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.12</version>
  </dependency>

  <dependency>
    <groupId>com.litongjava</groupId>
    <artifactId>tio-boot</artifactId>
    <version>${tio.boot.version}</version>
  </dependency>

  <dependency>
    <groupId>com.litongjava</groupId>
    <artifactId>api-table</artifactId>
    <version>${tio.boot.version}</version>
  </dependency>

  <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>2.2.10</version>
  </dependency>

  <!-- 连接池 -->
  <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.10</version>
  </dependency>

  <!-- 数据库驱动 -->
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.46</version>
  </dependency>
</dependencies>
```

请确保所有依赖的版本号为最新版本，以获得最佳的功能和安全性。

## 封装 ApiTableService

**ApiTableService** 是封装 **ApiTable** 操作的核心服务类，负责处理与数据库表相关的所有增删改查操作。通过统一的接口，简化了控制器中的代码逻辑。

### ApiTableService 代码

以下是 **ApiTableService** 的完整代码：

```java
package com.litongjava.max.blog.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.Kv;
import com.litongjava.db.TableInput;
import com.litongjava.db.TableResult;
import com.litongjava.db.activerecord.Db;
import com.litongjava.db.activerecord.Record;
import com.litongjava.model.body.RespBodyVo;
import com.litongjava.model.page.DbPage;
import com.litongjava.model.page.Page;
import com.litongjava.table.services.ApiTable;
import com.litongjava.table.utils.EasyExcelResponseUtils;
import com.litongjava.table.utils.TableInputUtils;
import com.litongjava.table.utils.TableResultUtils;
import com.litongjava.tio.boot.http.TioRequestContext;
import com.litongjava.tio.boot.utils.TioRequestParamUtils;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiTableService {

  public RespBodyVo create(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    ApiTable.transformType(f, map);
    TableInput kv = TableInputUtils.camelToUnderscore(map);
    log.info("tableName:{},kv:{}", f, kv);
    TableResult<Kv> dbJsonBean = ApiTable.saveOrUpdate(f, kv);
    if (dbJsonBean.getCode() == 1) {
      return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
    } else {
      return RespBodyVo.fail(dbJsonBean.getMsg()).code(dbJsonBean.getCode()).data(dbJsonBean.getData());
    }
  }

  public RespBodyVo list(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    ApiTable.transformType(f, map);
    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);
    TableResult<List<Record>> list = ApiTable.list(f, kv);

    TableResult<List<Kv>> dbJsonBean = TableResultUtils.recordsToKv(list, false);

    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo listAll(String f) {
    log.info("tableName:{}", f);
    TableResult<List<Record>> listAll = ApiTable.listAll(f);
    TableResult<List<Kv>> dbJsonBean = TableResultUtils.recordsToKv(listAll, false);

    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo page(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    Object current = map.remove("current");
    if (current != null) {
      // 支持 Ant Design Pro Table
      map.put("pageNo", current);
    }

    ApiTable.transformType(f, map);

    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);
    TableResult<Page<Record>> page = ApiTable.page(f, kv);

    TableResult<DbPage<Kv>> dbJsonBean = TableResultUtils.pageToDbPage(page, false);
    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo get(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    ApiTable.transformType(f, map);
    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);
    TableResult<Record> jsonBean = ApiTable.get(f, kv);
    TableResult<Kv> dbJsonBean = TableResultUtils.recordToKv(jsonBean);

    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo update(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    ApiTable.transformType(f, map);
    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);
    TableResult<Kv> dbJsonBean = ApiTable.saveOrUpdate(f, kv);

    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo batchUpdate(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    ApiTable.transformType(f, map);
    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);
    TableResult<Kv> dbJsonBean = ApiTable.batchUpdateByIds(f, kv);

    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo remove(String f, String id) {
    log.info("tableName:{},id:{}", f, id);
    TableResult<Boolean> dbJsonBean = ApiTable.updateFlagById(f, id, "deleted", 1);
    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo delete(String f, String id) {
    log.info("tableName:{},id:{}", f, id);
    TableResult<Boolean> dbJsonBean = ApiTable.delById(f, id);
    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo total(String f) {
    log.info("tableName:{},id:{}", f);
    Long count = Db.count(f);
    return RespBodyVo.ok(count);
  }

  public HttpResponse exportExcel(String f, HttpRequest request) throws IOException {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    ApiTable.transformType(f, map);
    Object current = map.remove("current");
    if (current != null) {
      // 支持 Ant Design Pro Table
      map.put("pageNo", current);
    }
    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);
    String filename = f + "_export_" + System.currentTimeMillis() + ".xlsx";

    // 获取数据
    List<Record> records = ApiTable.list(f, kv).getData();
    HttpResponse response = TioRequestContext.getResponse();
    return EasyExcelResponseUtils.exportRecords(response, filename, f, records);
  }

  public HttpResponse exportAllExcel(String f, HttpRequest request) throws IOException, SQLException {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    map.remove("current");
    map.remove("pageNo");
    map.remove("pageSize");
    ApiTable.transformType(f, map);

    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);

    // 导出 Excel
    String filename = f + "-all_" + System.currentTimeMillis() + ".xlsx";

    // 获取数据
    List<Record> records = ApiTable.listAll(f, kv).getData();

    HttpResponse response = TioRequestContext.getResponse();
    EasyExcelResponseUtils.exportRecords(response, filename, f, records);
    log.info("finished");
    return response;
  }

  public HttpResponse exportAllTableExcel(HttpRequest request) throws IOException {
    String filename = "all-table_" + System.currentTimeMillis() + ".xlsx";
    String[] tables = ApiTable.getAllTableNames();
    LinkedHashMap<String, List<Record>> allTableData = new LinkedHashMap<>();

    for (String table : tables) {
      // 获取数据
      List<Record> records = ApiTable.listAll(table).getData();
      allTableData.put(table, records);
    }
    HttpResponse response = TioRequestContext.getResponse();
    EasyExcelResponseUtils.exportAllTableRecords(response, filename, allTableData);
    log.info("finished");
    return response;
  }

  public RespBodyVo pageDeleted(String f, HttpRequest request) {
    Map<String, Object> map = TioRequestParamUtils.getRequestMap(request);
    map.remove("f");
    ApiTable.transformType(f, map);
    TableInput kv = TableInputUtils.camelToUnderscore(map);

    log.info("tableName:{},kv:{}", f, kv);
    TableResult<DbPage<Kv>> dbJsonBean = TableResultUtils.pageToDbPage(ApiTable.page(f, kv), false);

    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo recover(String f, String id) {
    log.info("tableName:{},id:{}", f, id);
    TableResult<Boolean> dbJsonBean = ApiTable.updateFlagById(f, id, "deleted", 0);

    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo tableNames() throws IOException {
    String[] data = ApiTable.tableNames().getData();
    return RespBodyVo.ok(data);
  }

  public RespBodyVo fConfig(String f, String lang) {
    log.info("tableName:{}", f);
    TableResult<Map<String, Object>> dbJsonBean = ApiTable.tableConfig(f, f, lang);
    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }

  public RespBodyVo proTableColumns(String f) {
    TableResult<List<Map<String, Object>>> dbJsonBean = ApiTable.columns(f);
    return RespBodyVo.ok(dbJsonBean.getData()).code(dbJsonBean.getCode()).msg(dbJsonBean.getMsg());
  }
}
```

### 主要方法解析

1. **create**：用于创建新记录。接收请求参数，转换类型后调用 `ApiTable.saveOrUpdate` 方法保存数据。
2. **list**：根据条件查询符合的记录列表。
3. **listAll**：查询表中所有记录。
4. **page**：实现分页查询，支持前端分页参数（如 `current`）。
5. **get**：根据唯一条件（如主键）获取单条记录。
6. **update**：根据主键更新单条记录。
7. **batchUpdate**：批量更新多条记录。
8. **remove**：逻辑删除，通过更新 `deleted` 字段标记记录。
9. **delete**：物理删除，直接从数据库中移除记录。
10. **total**：获取指定表的记录总数。
11. **exportExcel**：导出当前查询的数据为 Excel 文件，不包含已删除的数据。
12. **exportAllExcel**：导出符合条件的所有数据为 Excel 文件，包括逻辑删除的数据。
13. **exportAllTableExcel**：导出数据库中所有表的数据为一个 Excel 文件。
14. **pageDeleted**：分页查询已逻辑删除的数据。
15. **recover**：恢复已逻辑删除的记录。
16. **tableNames**：获取数据库中所有表名。
17. **fConfig**：获取指定表的配置，用于前端展示等。
18. **proTableColumns**：获取指定表的字段信息。

## 创建 ApiPostController

**ApiPostController** 作为控制器，负责处理前端发送的请求，并调用 **ApiTableService** 中对应的方法完成具体操作。以下是 **ApiPostController** 的完整代码：

### ApiPostController 代码

```java
package com.litongjava.max.blog.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.litongjava.annotation.EnableCORS;
import com.litongjava.annotation.RequestPath;
import com.litongjava.db.activerecord.Db;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.max.blog.consts.TableNames;
import com.litongjava.max.blog.service.ApiTableService;
import com.litongjava.model.body.RespBodyVo;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;

import lombok.extern.slf4j.Slf4j;

@RequestPath("/api/post")
@Slf4j
@EnableCORS
public class ApiPostController {

  ApiTableService apiTableService = Aop.get(ApiTableService.class);
  String f = TableNames.tio_boot_admin_article;

  @RequestPath("/index")
  public String index() {
    return "ApiPostController";
  }

  @RequestPath("/create")
  public RespBodyVo create(HttpRequest request) {
    return apiTableService.create(f, request);
  }

  @RequestPath("/list")
  public RespBodyVo list(HttpRequest request) {
    return apiTableService.list(f, request);
  }

  @RequestPath("/listAll")
  public RespBodyVo listAll() {
    return apiTableService.listAll(f);
  }

  @RequestPath("/page")
  public RespBodyVo page(HttpRequest request) {
    return apiTableService.page(f, request);
  }

  @RequestPath("/get")
  public RespBodyVo get(HttpRequest request) {
    return apiTableService.get(f, request);
  }

  @RequestPath("/update")
  public RespBodyVo update(HttpRequest request) {
    return apiTableService.update(f, request);
  }

  @RequestPath("/batchUpdate")
  public RespBodyVo batchUpdate(HttpRequest request) {
    return apiTableService.batchUpdate(f, request);
  }

  @RequestPath("/remove/{id}")
  public RespBodyVo remove(String id) {
    return apiTableService.remove(f, id);
  }

  @RequestPath("/delete/{id}")
  public RespBodyVo delete(String id) {
    return apiTableService.delete(f, id);
  }

  @RequestPath("/total")
  public RespBodyVo total(String f) {
    log.info("tableName:{},id:{}", f);
    Long count = Db.count(f);
    return RespBodyVo.ok(count);
  }

  /**
   * 导出当前数据
   */
  @RequestPath("/export-excel")
  public HttpResponse exportExcel(HttpRequest request) throws IOException {
    return apiTableService.exportExcel(f, request);
  }

  /**
   * 导出所有数据
   */
  @RequestPath("/export-table-excel")
  public HttpResponse exportAllExcel(HttpRequest request) throws IOException, SQLException {
    return apiTableService.exportAllExcel(f, request);
  }

  @RequestPath("/pageDeleted")
  public RespBodyVo pageDeleted(HttpRequest request) {
    return apiTableService.pageDeleted(f, request);
  }

  @RequestPath("/recover")
  public RespBodyVo recover(String id) {
    return apiTableService.recover(f, id);
  }

  @RequestPath("/config")
  public RespBodyVo fConfig(String lang) {
    return apiTableService.fConfig(f, lang);
  }

  @RequestPath("/columns")
  public RespBodyVo proTableColumns(String f) {
    return apiTableService.proTableColumns(f);
  }
}
```

### 主要方法解析

1. **create**：处理创建新文章的请求。
2. **list**：处理获取文章列表的请求。
3. **listAll**：处理获取所有文章的请求。
4. **page**：处理分页获取文章的请求。
5. **get**：处理获取单篇文章详情的请求。
6. **update**：处理更新文章的请求。
7. **batchUpdate**：处理批量更新文章的请求。
8. **remove**：处理逻辑删除文章的请求。
9. **delete**：处理物理删除文章的请求。
10. **total**：获取文章表的总记录数。
11. **exportExcel**：导出当前查询的文章数据为 Excel 文件。
12. **exportAllExcel**：导出符合条件的所有文章数据为 Excel 文件。
13. **pageDeleted**：分页查询已逻辑删除的文章。
14. **recover**：恢复已逻辑删除的文章。
15. **fConfig**：获取文章表的配置，用于前端展示等。
16. **proTableColumns**：获取文章表的字段信息。

## 使用 ApiTableService

通过 **ApiPostController**，前端可以发送 HTTP 请求来完成文章的增删改查操作，而控制器则通过 **ApiTableService** 来处理具体的业务逻辑。以下是使用流程的简要说明：

1. **发送请求**：前端通过 HTTP 请求（如 POST、GET、DELETE）发送数据到相应的接口。
2. **控制器处理**：**ApiPostController** 接收请求，并调用 **ApiTableService** 中对应的方法。
3. **服务层操作**：**ApiTableService** 通过 **ApiTable** 提供的静态方法，执行数据库操作。
4. **返回响应**：操作结果通过 **RespBodyVo** 封装后返回给前端。

### 示例：创建新文章

**前端请求**：

- **请求方式**：POST
- **URL**：`http://localhost:10051/api/post/create`
- **请求体**：

  ```json
  {
    "title": "新文章标题",
    "content": "这是文章的内容。",
    "author": "admin",
    "status": "published"
  }
  ```

**控制器处理**：

1. **ApiPostController** 的 `create` 方法接收请求，并调用 **ApiTableService** 的 `create` 方法。
2. **ApiTableService** 的 `create` 方法解析请求参数，转换字段名，并调用 **ApiTable.saveOrUpdate** 方法保存数据。
3. 保存成功后，返回包含新记录 ID 的响应。

**响应示例**：

```json
{
  "data": {
    "id": "361177594135064576"
  },
  "code": 1,
  "msg": null,
  "ok": true
}
```

## 测试接口

为了确保 **ApiTableService** 和 **ApiPostController** 的功能正常，需对各个接口进行详细测试。以下是各个接口的测试说明，包括请求方式、URL、参数及预期响应。

### 测试 create

- **功能**：添加一条新数据。
- **请求方式**：POST
- **URL**：`http://localhost:10051/api/post/create`
- **请求体**：

  ```json
  {
    "title": "测试文章",
    "content": "这是一篇测试文章。",
    "author": "admin",
    "status": "draft"
  }
  ```

- **响应示例**：

  ```json
  {
    "data": {
      "id": "361177594135064576"
    },
    "code": 1,
    "msg": null,
    "ok": true
  }
  ```

### 测试 list

- **功能**：查询文章列表。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/list`
- **请求参数**：可选，如 `author=admin`。
- **响应示例**：

  ```json
  {
    "data": [
      {
        "title": "测试文章",
        "content": "这是一篇测试文章。",
        "author": "admin",
        "status": "draft",
        "id": "361177594135064576"
      }
      // 其他记录...
    ],
    "msg": null,
    "code": 1,
    "ok": true
  }
  ```

### 测试 listAll

- **功能**：查询所有文章。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/listAll`
- **响应示例**：

  ```json
  {
    "data": [
      {
        "title": "测试文章",
        "content": "这是一篇测试文章。",
        "author": "admin",
        "status": "draft",
        "id": "361177594135064576"
      }
      // 其他记录...
    ],
    "code": 1,
    "msg": null,
    "ok": true
  }
  ```

### 测试 page

- **功能**：分页查询文章。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/page`
- **请求参数**：`current=1&pageSize=10`
- **响应示例**：

  ```json
  {
    "data": {
      "total": 17,
      "list": [
        {
          "title": "测试文章",
          "content": "这是一篇测试文章。",
          "author": "admin",
          "status": "draft",
          "id": "361177594135064576"
        }
        // 其他记录...
      ]
    },
    "code": 1,
    "msg": null,
    "ok": true
  }
  ```

### 测试 get

- **功能**：根据条件获取单条数据。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/get?title=测试文章`
- **响应示例**：

  ```json
  {
    "data": {
      "title": "测试文章",
      "content": "这是一篇测试文章。",
      "author": "admin",
      "status": "draft",
      "id": "361177594135064576"
    },
    "code": 1,
    "msg": null,
    "ok": true
  }
  ```

### 测试 update

- **功能**：根据 ID 更新数据。
- **请求方式**：POST
- **URL**：`http://localhost:10051/api/post/update`
- **请求体**：

  ```json
  {
    "id": "361177594135064576",
    "title": "更新后的文章标题",
    "status": "published"
  }
  ```

- **响应示例**：

  ```json
  {
    "data": null,
    "code": 1,
    "msg": "更新成功",
    "ok": true
  }
  ```

### 测试 batchUpdate

- **功能**：批量更新多条记录。
- **请求方式**：POST
- **URL**：`http://localhost:10051/api/post/batchUpdate`
- **请求体**：

  ```json
  {
    "ids": ["361177594135064576", "361177594135064577"],
    "status": "archived"
  }
  ```

- **响应示例**：

  ```json
  {
    "data": null,
    "code": 1,
    "msg": "批量更新成功",
    "ok": true
  }
  ```

### 测试 remove

- **功能**：逻辑删除数据。
- **请求方式**：DELETE
- **URL**：`http://localhost:10051/api/post/remove/361177594135064576`
- **响应示例**：

  ```json
  {
    "data": null,
    "code": 1,
    "msg": "逻辑删除成功",
    "ok": true
  }
  ```

### 测试 delete

- **功能**：物理删除数据。
- **请求方式**：DELETE
- **URL**：`http://localhost:10051/api/post/delete/361177594135064576`
- **响应示例**：

  ```json
  {
    "data": null,
    "code": 1,
    "msg": "删除成功",
    "ok": true
  }
  ```

### 测试 total

- **功能**：获取指定表的记录总数。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/total`
- **响应示例**：

  ```json
  {
    "data": 17,
    "code": 1,
    "msg": null,
    "ok": true
  }
  ```

### 测试 exportExcel

- **功能**：导出当前查询的数据为 Excel 文件，不包含已删除的数据。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/export-excel`
- **响应**：下载 `.xlsx` 文件。

### 测试 exportAllExcel

- **功能**：导出符合条件的所有数据为 Excel 文件，包括逻辑删除的数据。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/export-table-excel`
- **响应**：下载 `.xlsx` 文件。

### 测试 exportAllTableExcel

- **功能**：导出数据库中所有表的数据为一个 Excel 文件，慎用。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/export-all-table-excel`
- **响应**：下载 `.xlsx` 文件。

### 测试 pageDeleted

- **功能**：分页查询已逻辑删除的数据。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/pageDeleted`
- **响应示例**：

  ```json
  {
    "data": {
      "total": 1,
      "list": [
        {
          "title": "测试文章",
          "content": "这是一篇测试文章。",
          "author": "admin",
          "status": "archived",
          "id": "361177594135064576"
        }
      ]
    },
    "code": 1,
    "msg": null,
    "ok": true
  }
  ```

### 测试 recover

- **功能**：恢复已逻辑删除的数据。
- **请求方式**：POST
- **URL**：`http://localhost:10051/api/post/recover`
- **请求体**：

  ```json
  {
    "id": "361177594135064576"
  }
  ```

- **响应示例**：

  ```json
  {
    "data": null,
    "code": 1,
    "msg": "恢复成功",
    "ok": true
  }
  ```

### 测试 tableNames

- **功能**：获取数据库中所有表名。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/tableNames`
- **响应示例**：

  ```json
  {
    "data": [
      "system_users",
      "tio_boot_admin_article"
      // 其他表名...
    ],
    "code": 1,
    "msg": null,
    "ok": true
  }
  ```

### 测试 fConfig

- **功能**：获取指定表的配置，用于前端展示等。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/config?lang=en`
- **请求参数**：
  - `lang=en` （可选，用于指定语言）
- **响应示例**：

  ```json
  {
    "code": 1,
    "data": {
      // 配置信息
    },
    "msg": null,
    "ok": true
  }
  ```

### 测试 proTableColumns

- **功能**：获取指定表的字段信息。
- **请求方式**：GET
- **URL**：`http://localhost:10051/api/post/columns`
- **响应示例**：

  ```json
  {
    "code": 1,
    "data": [
      {
        "title": "Id",
        "dataIndex": "id",
        "valueType": "text"
      },
      {
        "title": "Title",
        "dataIndex": "title",
        "valueType": "text"
      },
      {
        "title": "Content",
        "dataIndex": "content",
        "valueType": "textarea"
      }
      // 其他字段信息...
    ],
    "ok": true
  }
  ```

## 总结

通过封装 **ApiTableService**，实现了对 **ApiTable** 操作的集中管理，极大地简化了控制器中的代码逻辑，提高了代码的可读性和可维护性。结合 **tio-boot** 框架的特性，进一步提升了开发效率和系统的稳定性。

在实际项目中，开发者可以根据业务需求，扩展 **ApiTableService** 的功能，或者创建更多的控制器来处理不同的数据表操作。同时，合理配置数据库连接池和优化查询条件，可以进一步提升系统的性能和响应速度。
