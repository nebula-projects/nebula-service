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

import org.nebula.framework.client.request.SignalWorkflowRequest;
import org.nebula.framework.client.response.SignalWorkflowResponse;
import org.nebula.framework.event.WorkflowSignaledEvent;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.EventMapper;
import org.nebula.service.message.WorkflowScheduledMessage;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.util.RealmUtils;
import org.nebula.service.validator.SignalWorkflowRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class SignalWorkflowProcessor extends
                                     EventProcessor<SignalWorkflowRequest, SignalWorkflowResponse> {

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  private EventMapper eventMapper;

  @Autowired
  public SignalWorkflowProcessor(SignalWorkflowRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(SignalWorkflowRequest request) {
    WorkflowSignaledEvent event = new WorkflowSignaledEvent();
    event.setInput(request.getInput());
    event.setInstanceId(request.getInstanceId());
    event.setPrecedingId(WITHOUT_PRECEDING_ID);
    event.setRealms(request.getRealms());
    event.setSignalProfile(request.getSignalProfile());

    String registrationId = findRegistrationId(request.getInstanceId());
    event.setRegistrationId(registrationId);

    return persist(event);
  }

  protected void createMessageAndSend(SignalWorkflowRequest request,
                                      Event event) {

    String properRealm = RealmUtils.getProperRealm(request.getRealms());

    WorkflowScheduledMessage message = new WorkflowScheduledMessage(
        event.getRegistrationId(), event.getInstanceId(),
        event.getId(), properRealm);

    message.setId(UUID.randomUUID().toString());
    message.setSignal(true);

    getLogger().info(
        "Send WorkflowScheduledMessage with signal:" + toJson(message));

    providerFactory.getProvider(new WorkflowRealm(request.getAccessId(), properRealm)).send(message);

  }

  protected SignalWorkflowResponse buildResponse(
      SignalWorkflowRequest request, Event event) {
    SignalWorkflowResponse response = new SignalWorkflowResponse();
    response.setInstanceId(request.getInstanceId());
    return response;
  }

  private String findRegistrationId(String instanceId) {
    String registrationId = eventMapper
        .findRegistrationIdByInstanceId(instanceId);

    if (registrationId == null) {
      String errorMsg = "No workflow registration info found for instanceId "
                        + instanceId;
      getLogger().error(errorMsg);
      throw new IllegalArgumentException(errorMsg);
    }

    return registrationId;
  }

}
