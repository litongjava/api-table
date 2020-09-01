package com.litong.jfinal.controler;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.litong.jfinal.utils.PathKitUtil;

public class PathController extends Controller {
  public void index() {
    Map<String, String> map = new HashMap<>();
    map.put("rootClassPath", PathKit.getRootClassPath());
    map.put("webRootPath", PathKit.getWebRootPath());
    map.put("realResourcePath", PathKitUtil.getResourcePath());
    renderJson(map);
  }
}