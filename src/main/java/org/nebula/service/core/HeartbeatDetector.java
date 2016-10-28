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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nebula.admin.client.NebulaAdminClient;
import org.nebula.admin.client.model.admin.DomainSummary;
import org.nebula.admin.client.model.admin.NebulaServerHeartbeatSummary;
import org.nebula.admin.client.model.admin.UserSummary;
import org.nebula.admin.client.request.admin.NebulaServerHeartbeatRequest;
import org.nebula.admin.client.response.admin.NebulaServerHeartbeatResponse;
import org.nebula.service.auth.UserCredentials;
import org.nebula.service.auth.UserCredentialsPool;
import org.nebula.service.config.DomainConfiguration;
import org.nebula.service.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class HeartbeatDetector implements Runnable {

  private final static Logger logger = Logger
      .getLogger(HeartbeatDetector.class);


  @Value("${nebula.service.host}")
  private String nebulaServiceHost;

  @Autowired
  private NebulaAdminClient nebulaAdminClient;

  @Autowired
  private DomainConfiguration domainConfiguration;

  @Autowired
  private UserCredentialsPool userCredentialsPool;

  @Autowired
  private ServiceContext serviceContext;

  public void preProcess() throws Exception {

    if (StringUtils.isEmpty(nebulaServiceHost)) {
      nebulaServiceHost = HostAddress.getLocalHost();
    }

    domainConfiguration.load();
  }

  public void run() {
    try {
      NebulaServerHeartbeatSummary heartbeatSummary = heartbeat();

      postProcess(heartbeatSummary);

    } catch (Exception e) {
      logger.error("Failed to heartbeat.", e);
    }

    if (domainConfiguration.isReady()) {
      serviceContext.changeToReadyStatus();
    } else {
      serviceContext.changeToNotReadyStatus();
    }
    
    logger.info("The service status is " + serviceContext.getStatus());
  }

  private NebulaServerHeartbeatSummary heartbeat() throws Exception {

    NebulaServerHeartbeatRequest request = new NebulaServerHeartbeatRequest();
    request.setHost(nebulaServiceHost);
    request.setDomainHashCode(domainConfiguration.getHashCode());
    request.setUserCredentialsHashCode(userCredentialsPool.getUserCredentialsHashCode());

    logger.info("NebulaServiceHeartbeat request " + JsonUtils.toJson(request));

    NebulaServerHeartbeatResponse response = nebulaAdminClient.heartbeatNebulaServer(request);

    logger.info("NebulaServiceHeartbeat response " + JsonUtils.toJson(response));
    return response.getNebulaServerHeartbeatSummary();
  }

  private boolean needUpdateDomainConfiguration(int latestDomainHashCode) {
    return domainConfiguration.getHashCode() != latestDomainHashCode;
  }

  private boolean needUpdateUserCredentials(int latestUserCredentialsHashCode) {
    return userCredentialsPool.getUserCredentialsHashCode() != latestUserCredentialsHashCode;
  }


  private void postProcess(NebulaServerHeartbeatSummary heartbeatSummary) {

    postProcessDomainConfiguration(heartbeatSummary);

    postProcessUserCredentials(heartbeatSummary);
  }

  private void postProcessDomainConfiguration(NebulaServerHeartbeatSummary heartbeatSummary) {

    logger.info("domainConfiguration.getHashCode()=" + domainConfiguration.getHashCode()
                + ",heartbeatSummary.getDomainHashCode()=" + heartbeatSummary.getDomainHashCode());
    if (needUpdateDomainConfiguration(heartbeatSummary.getDomainHashCode())) {

      DomainSummary domainSummary = heartbeatSummary.getDomainSummary();

      domainConfiguration.setName(domainSummary.getName());
      domainConfiguration.setDbUrl(domainSummary.getDbUrl());
      domainConfiguration.setRedisHostAndPort(domainSummary.getRedisHost(),
                                              domainSummary.getRedisPort());
      domainConfiguration.setStatus(domainSummary.getStatus());

      domainConfiguration.setHashCode(heartbeatSummary.getDomainHashCode());

      domainConfiguration.persist();
    }
  }

  private void postProcessUserCredentials(NebulaServerHeartbeatSummary heartbeatSummary) {
    if (needUpdateUserCredentials(heartbeatSummary.getUserCredentialsHashCode())) {

      List<UserSummary> userSummaries = heartbeatSummary.getUserSummaries();

      Map<String, UserCredentials> pool = new HashMap<String, UserCredentials>();
      for (UserSummary userSummary : userSummaries) {
        pool.put(userSummary.getAccessId(), translateToUserCredentials(userSummary));
      }
      userCredentialsPool.setUserCredentialsPool(pool);

      userCredentialsPool.setUserCredentialsHashCode(heartbeatSummary.getUserCredentialsHashCode());

    }
  }

  private UserCredentials translateToUserCredentials(UserSummary userSummary) {
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setAccessId(userSummary.getAccessId());
    userCredentials.setSecretKey(userSummary.getSecretKey());
    userCredentials.setAdmin(userSummary.isAdmin());

    return userCredentials;
  }

}