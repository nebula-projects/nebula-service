/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nebula.service.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Pattern;

@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PaginationInterceptor implements Interceptor {

  private final static Logger logger = Logger
      .getLogger(PaginationInterceptor.class);
  private final static String SQL_SELECT_REGEX = "(?is)^\\s*SELECT.*$";
  private final static String
      SQL_COUNT_REGEX =
      "(?is)^\\s*SELECT\\s+COUNT\\s*\\(\\s*(?:\\*|\\w+)\\s*\\).*$";

  // @Override
  public Object intercept(Invocation inv) throws Throwable {

    StatementHandler target = (StatementHandler) inv.getTarget();
    BoundSql boundSql = target.getBoundSql();
    String sql = boundSql.getSql();
    if (StringUtils.isBlank(sql)) {
      return inv.proceed();
    }
    logger.debug("origin sql>>>>>" + sql.replaceAll("\n", ""));
    // 只有为select查询语句时才进行下一步
    if (sql.matches(SQL_SELECT_REGEX)
        && !Pattern.matches(SQL_COUNT_REGEX, sql)) {
      Object obj = FieldUtils.readField(target, "delegate", true);
      // 反射获取 RowBounds 对象。
      RowBounds rowBounds = (RowBounds) FieldUtils.readField(obj,
                                                             "rowBounds", true);
      // 分页参数存在且不为默认值时进行分页SQL构造
      if (rowBounds != null && rowBounds != RowBounds.DEFAULT) {
        FieldUtils.writeField(boundSql, "sql", newSql(sql, rowBounds),
                              true);
        logger.debug("new sql>>>>>"
                     + boundSql.getSql().replaceAll("\n", ""));
        // 一定要还原否则将无法得到下一组数据(第一次的数据被缓存了)
        FieldUtils.writeField(rowBounds, "offset",
                              RowBounds.NO_ROW_OFFSET, true);
        FieldUtils.writeField(rowBounds, "limit",
                              RowBounds.NO_ROW_LIMIT, true);
      }
    }
    return inv.proceed();
  }

  public String newSql(String oldSql, RowBounds rowBounds) {
    return getLimitString(oldSql, rowBounds.getOffset(),
                          Integer.toString(rowBounds.getOffset()),
                          Integer.toString(rowBounds.getLimit()));
  }

  // @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  // @Override
  public void setProperties(Properties arg0) {

  }

  private String getLimitString(String sql, int offset,
                                String offsetPlaceholder, String limitPlaceholder) {
    StringBuilder stringBuilder = new StringBuilder(sql);
    stringBuilder.append(" limit ");
    if (offset > 0) {
      stringBuilder.append(offsetPlaceholder).append(",")
          .append(limitPlaceholder);
    } else {
      stringBuilder.append(limitPlaceholder);
    }
    return stringBuilder.toString();
  }

}
