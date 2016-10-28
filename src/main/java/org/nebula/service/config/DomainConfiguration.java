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

import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;
import org.nebula.service.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class DomainConfiguration {

  @JsonIgnore
  private final static Logger logger = Logger
      .getLogger(DomainConfiguration.class);

  @JsonIgnore
  private final static String USER_HOME = System.getProperty("user.home");
  @JsonIgnore
  private final static String DOMAIN_CONFIG_FILE = USER_HOME + File.separator + "domain.cfg";

  @JsonIgnore
  @Autowired
  private DbUrlChangeListener dbUrlChangeListener;

  @JsonIgnore
  @Autowired
  private RedisChangeListener redisChangeListener;

  private String name;

  private String dbUrl;

  private String redisHost;

  private int redisPort;

  private int status;

  private int hashCode;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public void setDbUrl(String dbUrl) {
    if(dbUrl!=null && ! dbUrl.equals(this.dbUrl)) {
      this.dbUrl = dbUrl;
      if(dbUrlChangeListener!=null) {
        dbUrlChangeListener.onChange(dbUrl);
      }
    }
  }

  public String getRedisHost() {
    return redisHost;
  }

  public void setRedisHostAndPort(String redisHost, int redisPort) {
    if((redisHost!=null && !redisHost.equals(this.redisHost)) ||  redisPort != this.redisPort) {
      this.redisHost = redisHost;
      this.redisPort = redisPort;

      if(redisChangeListener!=null) {
        redisChangeListener.onChange(redisHost, redisPort);
      }
    }
  }

  public int getRedisPort() {
    return redisPort;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getHashCode() {
    return hashCode;
  }

  public void setHashCode(int hashCode) {
    this.hashCode = hashCode;
  }

  @JsonIgnore
  public boolean isReady() {
    return  isNotBlank(dbUrl) && isNotBlank(redisHost) && redisPort > 0 && redisPort< 65535;
  }

  public void load(){
    if(FileUtils.fileExists(DOMAIN_CONFIG_FILE)){
      try {
        String domainJson = FileUtils.fileRead(DOMAIN_CONFIG_FILE, "UTF-8");
        if (StringUtils.isNotBlank(domainJson)) {
          DomainConfiguration domainConfig = JsonUtils.toObject(domainJson, DomainConfiguration.class);
          toThis(domainConfig);
        }
      } catch (Exception e) {
        logger.error("Failed to read domain configuration file.", e);
      }
    }
  }

  public void persist(){
    try {
      FileUtils.fileWrite(DOMAIN_CONFIG_FILE, "UTF-8", JsonUtils.toJson(this));
    } catch (IOException e) {
      logger.error("Failed to write domain configuration file.", e);
    }
  }

  private void toThis(DomainConfiguration domainConfiguration) {
    setName(domainConfiguration.getName());
    setDbUrl(domainConfiguration.getDbUrl());
    setRedisHostAndPort(domainConfiguration.getRedisHost(), domainConfiguration.getRedisPort());
    setStatus(domainConfiguration.getStatus());
    setHashCode(domainConfiguration.getHashCode());
  }

}