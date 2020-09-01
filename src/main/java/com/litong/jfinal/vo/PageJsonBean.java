package com.litong.jfinal.vo;


import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.litong.utils.vo.JsonBean;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PageJsonBean<T> extends JsonBean<List<T>> {

  private long count;

  public long getCount() {
    return count;
  }

  public PageJsonBean(Page<T> listPage) {
    super(listPage.getList());
    this.count=listPage.getTotalRow();
  }

}
