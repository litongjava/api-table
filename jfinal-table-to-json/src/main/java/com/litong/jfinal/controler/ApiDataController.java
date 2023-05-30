package com.litong.jfinal.controler;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.litong.jfinal.validate.TableNameValidator;
import com.litongjava.data.model.DataPageRequest;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.services.DbDataService;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.data.services.PrimaryKeyService;
import com.litongjava.data.services.TableColumnService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bill robot
 * @date 2020年8月24日_下午3:05:36 
 * @version 1.0 
 * @desc
 */
@Slf4j
@Before(TableNameValidator.class)
public class ApiDataController extends Controller {

  @Inject
  private DbDataService apiFormService;
  @Inject
  private PrimaryKeyService primaryKeyService;
  @Inject
  private TableColumnService tableColumnService;
  @Inject
  private DbJsonService dbJsonService;

  /**
   * 返回所有数据
   */
  public void listAll(Kv kv) {
    renderJson(apiFormService.listAll(kv));
  }

  /**
   * 数据列表
   * @param kv 普通情况下的数据 
   * {pageNo=1, orderBy=update_time, pageSize=10, isAsc=false, tableName=cron4j_task}
   * 搜索情况下的数据
   * {end.create_time=2020-08-27 00:00:00, pageNo=1, orderBy=update_time, pageSize=10, start.create_time=2020-08-01 00:00:00, 
  //   * end.update_time=2020-08-27 00:00:00, isAsc=false, id=null, start.update_time=2020-08-01 00:00:00, tableName=cron4j_task}
   */
  public void page(String tableName, DataPageRequest pageRequest, Kv kv) {
    log.info("kv : " + kv);
    renderJson(dbJsonService.page(tableName, pageRequest, kv));
    return;
  }

  /**
   * 
   * @param kv
   * {id=1, tableName=cron4j_task}
   */
  public void getById(String tableName, Kv kv) {
    log.info("kv : " + kv);
    renderJson(dbJsonService.getById(tableName, kv));
    return;
  }

  public void delById(String tableName, Kv kv) {
    if (kv.size() == 0) {
      renderJson(new DbJsonBean<Void>(-1, "no params"));
      return;
    }
    log.info("kv : " + kv);
    renderJson(dbJsonService.delById(tableName, kv));
  }

  public void removeByIds(String tableName, Kv kv) {
    log.info("kv : " + kv);
    if (kv.size() == 0) {
      renderJson(new DbJsonBean<Void>(-1, "no params"));
      return;
    }
    renderJson(dbJsonService.removeByIds(tableName, kv));
  }

  /*
   * 请求数据 {tableName=cron4j_task,cron=* * * * *, task=xx.coas, name=同步数据, is_daemon=0, id=null, is_enable=0} 响应数据 主键id
   */
  @SuppressWarnings("unchecked")
  public void saveOrUpdate(String tableName, Kv kv) {
    log.info("kv : " + kv);
    renderJson(dbJsonService.saveOrUpdate(tableName,kv));
  }

}
