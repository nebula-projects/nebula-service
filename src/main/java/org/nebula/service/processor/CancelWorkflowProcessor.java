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

import org.nebula.framework.client.request.CancelWorkflowRequest;
import org.nebula.framework.client.response.CancelWorkflowResponse;
import org.nebula.framework.event.WorkflowCancelledEvent;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.ActivityTimerMapper;
import org.nebula.service.message.WorkflowScheduledMessage;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.util.RealmUtils;
import org.nebula.service.validator.CancelWorkflowRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class CancelWorkflowProcessor extends
                                     EventProcessor<CancelWorkflowRequest, CancelWorkflowResponse> {

  @Autowired
  private ActivityTimerMapper activityTimerMapper;

  @Autowired
  private ConsumerFactory consumerFactory;

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  public CancelWorkflowProcessor(CancelWorkflowRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(CancelWorkflowRequest request) {
    WorkflowCancelledEvent event = new WorkflowCancelledEvent();

    event.setInstanceId(request.getInstanceId());
    event.setPrecedingId(WITHOUT_PRECEDING_ID);
    event.setRegistrationId(findRegistrationId(request.getInstanceId()));

    PersistenceResult result = persist(event);

    activityTimerMapper.deleteTimersByInstanceId(request.getInstanceId());

    return result;
  }

  protected void createMessageAndSend(CancelWorkflowRequest request,
                                      Event event) {

    String properRealm = RealmUtils.getProperRealm(request.getRealms());

    WorkflowScheduledMessage message = new WorkflowScheduledMessage(
        event.getRegistrationId(), event.getInstanceId(),
        event.getId(), properRealm);

    message.setId(UUID.randomUUID().toString());

    getLogger().info(
        "Send WorkflowScheduledMessage:" + toJson(message));

    providerFactory.getProvider(new WorkflowRealm(request.getAccessId(), properRealm)).send(message);

  }

  protected CancelWorkflowResponse buildResponse(
      CancelWorkflowRequest request, Event event) {

    CancelWorkflowResponse response = new CancelWorkflowResponse();
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
