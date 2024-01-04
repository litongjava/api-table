package com.litongjava.jfinal.render;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.FileRender;
import com.jfinal.render.TemplateRender;
import com.jfinal.template.Engine;

/***
 * 文件模版 渲染后 输出 , 应用场景: 拼接XLS文件 , 在线代码生成下载 , 等...
 *
 * @author 杜福忠 2018-12-14 10:53:12 改进
 */
public class FileTemplateRender extends TemplateRender {

  protected static Engine MyEngine = null;

  /*
   * 自定义 Engine
   */
  public static void setEngine(Engine jfEngine) {
    if (jfEngine == null) {
      throw new IllegalArgumentException("engine can not be null");
    }
    MyEngine = jfEngine;
  }

  protected static String downloadPath = getDownloadPath();

  /*
   * 临时文件地址
   */
  protected static String getDownloadPath() {
    String path = PathKit.getWebRootPath() + "/download/FileTemplateRender";
    new File(path).mkdirs();
    return path;
  }

  protected String downloadFileName = null;
  protected Callback callback = null;

  /**
   * 渲染文件
   *
   * @param view
   * @param downloadFileName
   */
  public FileTemplateRender(String view, String downloadFileName) {
    super(view);
    this.downloadFileName = downloadFileName;
  }

  /**
   * 文件渲染下载后会回调 run(File file)方法
   */
  public static abstract class Callback {

    public abstract void run(File file);
  }

  /**
   * 渲染后下载完回调文件处理
   *
   * <pre>
   * new FileTemplateRender(view, downloadFileName,
   *         new FileTemplateRender.Callback() {
   *
   *             public void run(File file) {
   *                 // 处理 file
   *             }
   *         });
   * </pre>
   *
   * @param view
   * @param downloadFileName
   * @param callback
   *            回调函数
   */
  public FileTemplateRender(String view, String downloadFileName, Callback callback) {
    this(view, downloadFileName);
    this.callback = callback;
  }

  @Override
  public void render() {
    File file = null;
    try {
      file = renderFile();
      // 转FileRender进行下载文件
      FileRender fr = new FileRender(file, downloadFileName);
      fr.setContext(request, response);
      fr.render();
    } finally {
      // 下载完是否回调, 不回调就删除掉临时文件
      if (null != callback) {
        callback.run(file);
      } else if (null != file) {
        file.delete();
      }
    }
  }

  /**
   * 渲染模版文件
   *
   * @return 渲染后的文件
   */
  protected File renderFile() {
    Map<Object, Object> data = new HashMap<>();
    for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
      String attrName = attrs.nextElement();
      data.put(attrName, request.getAttribute(attrName));
    }

    StringBuilder pathname = new StringBuilder(downloadPath).append("/").append(StrKit.getRandomUUID())
        .append(downloadFileName);
    File file = new File(pathname.toString());

    getEngine().getTemplate(view).render(data, file);
    return file;
  }

  /**
   * 优先使用自定义Engine没有设置的时候使用全局的
   *
   * @return
   */
  protected Engine getEngine() {
    return null == MyEngine ? engine : MyEngine;
  }

  /***
   * 文件名称 自动加上时间戳
   */
  public FileTemplateRender setDate() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm_");
    String dateStr = dateFormat.format(new Date());
    downloadFileName = dateStr.concat(downloadFileName);
    return this;
  }

}