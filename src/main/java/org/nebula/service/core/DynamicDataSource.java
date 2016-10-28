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

package org.nebula.service.core;

import org.apache.commons.dbcp.BasicDataSource;
import org.nebula.service.config.DbUrlChangeListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Service
public class DynamicDataSource extends AbstractRoutingDataSource implements DbUrlChangeListener {

  private final static String LOOKUP_KEY = "NEBULA";
  private Map<Object, Object> datasources = new HashMap<Object, Object>();
  @Value("${jdbc.maxActive}")
  private int jdbcMaxActive;

  @Value("${jdbc.maxIdle}")
  private int jdbcMaxIdle;

  @Value("${jdbc.initialSize}")
  private int jdbcInitialSize;

  @Value("${jdbc.username}")
  private String jdbcUsername;

  @Value("${jdbc.password}")
  private String jdbcPassword;

  public DynamicDataSource() {
    this.setTargetDataSources(datasources);
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return LOOKUP_KEY;
  }

  protected DataSource determineTargetDataSource() {
    Object lookupKey = this.determineCurrentLookupKey();
    DataSource dataSource = (DataSource) this.datasources.get(lookupKey);

    if (dataSource == null) {
      throw new IllegalStateException(
          "Cannot determine target DataSource for lookup key [" + lookupKey + "]");
    }
    return dataSource;
  }

  public void onChange(String dbUrl) {

    logger.info("Change target dbUrl to: " + dbUrl);

    Object oldDataSource = this.datasources.remove(LOOKUP_KEY);

    if (oldDataSource != null) {
      try {
        ((BasicDataSource) oldDataSource).close();
      } catch (SQLException e) {
        //ignore
      }
    }

    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setMaxActive(jdbcMaxActive);
    dataSource.setMaxIdle(jdbcMaxIdle);
    dataSource.setInitialSize(jdbcInitialSize);
    dataSource.setUrl(dbUrl);
    dataSource.setUsername(jdbcUsername);
    dataSource.setPassword(jdbcPassword);
    dataSource.setTestOnBorrow(true);
    dataSource.setValidationQuery("SELECT 1");

    this.datasources.put(LOOKUP_KEY, dataSource);
  }

}