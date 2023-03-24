package com.litong.jfinal.db;

import java.sql.SQLException;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.mysql.jdbc.PreparedStatement;

/**
 * 打印执行的sql语句
 */
public class SqlLogFilter extends FilterAdapter {

  private static final SQLUtils.FormatOption FORMAT_OPTION = new SQLUtils.FormatOption(false, false);

  @Override
  public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
    if (statement.getRawObject() instanceof PreparedStatement) {
      PreparedStatement preparedStatement = (PreparedStatement) statement.getRawObject();
      String sql = preparedStatement.asSql();
      // 定制sql输出格式,可以去掉
      sql = SQLUtils.formatMySql(sql, FORMAT_OPTION);
      System.out.println("sql:" + sql);
    }
    super.statement_close(chain, statement);
  }
}