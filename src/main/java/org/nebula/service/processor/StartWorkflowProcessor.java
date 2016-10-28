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

import org.nebula.framework.client.request.RegisterRequest;
import org.nebula.framework.client.request.StartWorkflowRequest;
import org.nebula.framework.client.response.StartWorkflowResponse;
import org.nebula.framework.event.WorkflowScheduledEvent;
import org.nebula.service.config.DomainConfiguration;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.entity.Registration;
import org.nebula.service.dao.mapper.RegistrationMapper;
import org.nebula.service.message.WorkflowScheduledMessage;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.util.RealmUtils;
import org.nebula.service.validator.StartWorkflowRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class StartWorkflowProcessor extends
                                    EventProcessor<StartWorkflowRequest, StartWorkflowResponse> {

  @Autowired
  private RegistrationMapper registrationMapper;

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  private DomainConfiguration domainConfiguration;


  @Autowired
  public StartWorkflowProcessor(StartWorkflowRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(StartWorkflowRequest request) {
    WorkflowScheduledEvent event = new WorkflowScheduledEvent();
    event.setWorkflowProfile(request.getWorkflowProfile());
    event.setInput(request.getInput());
    event.setInstanceId(
        domainConfiguration.getName() + "-" + request.getAccessId() +"-" + UUID.randomUUID().toString());
    event.setPrecedingId(WITHOUT_PRECEDING_ID);
    event.setRealms(request.getRealms());
    event.setStartProfile(request.getStartProfile());
    event.setStartMode(request.getStartMode());

    String registrationId = request.getRegistrationId();
    if (request.getStartMode() == StartWorkflowRequest.StartMode.NORMAL) {
      registrationId = findRegistrationId(request.getWorkflowProfile()
                                              .getName(), request.getWorkflowProfile().getVersion(),
                                          request.getUser());
    }
    event.setRegistrationId(registrationId);

    return persist(event);
  }

  protected void createMessageAndSend(StartWorkflowRequest request,
                                      Event event) {

    String properRealm = RealmUtils.getProperRealm(request.getRealms());

    WorkflowScheduledMessage message = new WorkflowScheduledMessage(
        event.getRegistrationId(), event.getInstanceId(),
        event.getId(), properRealm);

    message.setId(UUID.randomUUID().toString());

    getLogger().info("Send WorkflowScheduledMessage:" + toJson(message));

    providerFactory.getProvider(new WorkflowRealm(request.getUser(), properRealm)).send(message);

  }

  protected StartWorkflowResponse buildResponse(
      StartWorkflowRequest startWorkflowRequest, Event event) {
    StartWorkflowResponse response = new StartWorkflowResponse();
    response.setInstanceId(event.getInstanceId());
    return response;
  }

  private String findRegistrationId(String name, String version, String user) {
    Registration registration = new Registration();
    registration.setName(name);
    registration.setVersion(version);
    registration.setUser(user);
    registration.setType(RegisterRequest.NodeType.WORKFLOW.name());

    Registration persistedRegistration = registrationMapper
        .findEnabled(registration);

    if (persistedRegistration == null) {
      String warnMsg = "No workflow registration info found or is disabled for workflowName "
                       + name + ", verison " + version + ", user " + user;
      getLogger().warn(warnMsg);
      throw new IllegalArgumentException(warnMsg);
    }

    return persistedRegistration.getId();
  }

}
