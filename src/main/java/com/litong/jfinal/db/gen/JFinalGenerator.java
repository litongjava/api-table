package com.litong.jfinal.db.gen;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

public class JFinalGenerator {

  public static String configFileName = "undertow.properties";
  public static Prop p;

  public static void main(String[] args) {
    // model 所使用的包名 (MappingKit 默认使用的包名)
    String modelPackageName = "com.litong.modules.wechat.ss.common.model";
    // base model 所使用的包名
    String baseModelPackageName = modelPackageName + ".base";// "com.litong.db.monitoring.common.model.base";
    // base model 文件保存路径
    String baseModelOutputDir = getBaseModelOutputDir(baseModelPackageName);

    // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
    String modelOutputDir = baseModelOutputDir + "/..";

    // 创建生成器
    Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName,
        modelOutputDir);

    // 配置是否生成备注
    generator.setGenerateRemarks(true);

    // 设置数据库方言
    generator.setDialect(new MysqlDialect());

    // 设置是否生成链式 setter 方法
    generator.setGenerateChainSetter(false);

    // 添加不需要生成的表名
    // generator.addExcludedTable("t_db_connect_info");

    // 设置是否在 Model 中生成 dao 对象
    generator.setGenerateDaoInModel(false);

    // 设置是否生成字典文件
    generator.setGenerateDataDictionary(false);

    // 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀
    // "osc_"后生成的model名为"User"而非OscUser
    generator.setRemovedTableNamePrefixes("t_");

    // 生成
    generator.generate();
  }

  public static String getBaseModelOutputDir(String modelPackageName) {
    String replace = modelPackageName.replace('.', '/');
    return PathKit.getWebRootPath() + "/src/main/java/" + replace;
  }

  private static void loadConfig() {
    if (p == null) {
      p = PropKit.use(configFileName);
    }
  }

  public static DruidPlugin createDruidPlugin() {
    loadConfig();
    String url = p.get("jdbc.url").trim();
    String user = p.get("jdbc.user").trim();
    String pswd = p.get("jdbc.pswd");
    if (!StrKit.isBlank(pswd)) {
      pswd = pswd.trim();
    }
    return new DruidPlugin(url, user, pswd);
  }

  public static DataSource getDataSource() {
    DruidPlugin druidPlugin = createDruidPlugin();
    druidPlugin.start();
    return druidPlugin.getDataSource();
  }
}