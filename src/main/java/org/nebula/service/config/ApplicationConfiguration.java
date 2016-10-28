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

package org.nebula.service.config;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.nebula.admin.client.NebulaAdminClient;
import org.nebula.service.core.DynamicDataSource;
import org.nebula.service.dao.PaginationInterceptor;
import org.nebula.service.dao.cache.HeartbeatCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@MapperScan("org.nebula.service.dao.mapper")
@EnableTransactionManagement
public class ApplicationConfiguration {

  @Value("${nebula.admin.service.username}")
  String nebulaAdminServiceUsername;

  @Value("${nebula.admin.service.password}")
  String nebulaAdminServicePassword;

  @Value("${nebula.admin.service.host}")
  String nebulaAdminServiceHost;

  @Value("${nebula.admin.service.port}")
  int nebulaAdminServicePort;

  @Value("${nebula.admin.service.contextPath}")
  String nebulaAdminServiceContextPath;

  @Autowired
  DynamicDataSource dynamicDataSource;

  @Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dynamicDataSource);
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dynamicDataSource);
    sessionFactory.setMapperLocations(
        new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
    sessionFactory.setTypeAliasesPackage("org.nebula.service.dao.entity");
    sessionFactory.setPlugins(new Interceptor[]{new PaginationInterceptor()});
    return sessionFactory.getObject();
  }


  @Bean
  HeartbeatCache heartbeatCache() {
    return new HeartbeatCache(60);
  }

  @Bean
  NebulaAdminClient nebulaAdminClient() {
    return new NebulaAdminClient(nebulaAdminServiceUsername, nebulaAdminServicePassword,
                                 nebulaAdminServiceHost, nebulaAdminServicePort,
                                 nebulaAdminServiceContextPath);
  }
}
