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

package org.nebula.service.processor;

import org.nebula.framework.client.request.HeartbeatRequest;
import org.nebula.framework.client.response.HeartbeatResponse;
import org.nebula.service.config.DomainConfiguration;
import org.nebula.service.dao.entity.Heartbeat;
import org.nebula.service.dao.mapper.HeartbeatMapper;
import org.nebula.service.validator.HeartbeatRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HeartbeatProcessor extends
                                AbstractProcessor<HeartbeatRequest, HeartbeatResponse, Heartbeat> {

  @Autowired
  private DomainConfiguration domainConfiguration;

  @Autowired
  private HeartbeatMapper heartbeatMapper;

  @Autowired
  public HeartbeatProcessor(HeartbeatRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(HeartbeatRequest request) {

    Heartbeat heartbeat = new Heartbeat();

    heartbeat.setRegistrationId(request.getRegistrationId().toString());
    heartbeat.setHost(request.getHost());
    heartbeat.setIp(request.getIp());
    heartbeat.setProcessId(request.getProcessId());
    heartbeat.setWorkingDir(request.getWorkingDir());

    return persist(request, heartbeat);
  }

  private PersistenceResult persist(HeartbeatRequest request, Heartbeat heartbeat) {
    PersistenceResult result = null;

    Heartbeat persisted = heartbeatMapper.find(heartbeat);
    if (persisted == null) {
      heartbeat.setId(domainConfiguration.getName() + "-" + request.getAccessId() + "-" + UUID.randomUUID().toString());
      heartbeatMapper.insertHeartbeat(heartbeat);
      result = new PersistenceResult(true, heartbeat);
    } else {
      persisted.setProcessId(request.getProcessId());
      heartbeatMapper.update(persisted);
      result = new PersistenceResult(true, persisted);
    }
    return result;
  }

  protected HeartbeatResponse buildResponse(HeartbeatRequest request,
                                            Heartbeat heartbeat) {

    HeartbeatResponse response = new HeartbeatResponse();
    response.setRegistrationId(request.getRegistrationId());
    // TODO put more info into the response

    return response;
  }
}
