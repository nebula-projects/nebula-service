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

import org.nebula.framework.client.request.CompleteWorkflowRequest;
import org.nebula.framework.client.request.StartWorkflowRequest;
import org.nebula.framework.client.response.CompleteWorkflowResponse;
import org.nebula.framework.event.WorkflowCompletedEvent;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.entity.WorkflowTimer;
import org.nebula.service.dao.mapper.WorkflowTimerMapper;
import org.nebula.service.message.CommonMessage;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.util.CronUtils;
import org.nebula.service.util.JsonUtils;
import org.nebula.service.validator.CompleteWorkflowRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class CompleteWorkflowProcessor
    extends
    EventProcessor<CompleteWorkflowRequest, CompleteWorkflowResponse> {

  @Autowired
  private WorkflowTimerMapper workflowTimerMapper;

  @Autowired
  private ConsumerFactory consumerFactory;

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  public CompleteWorkflowProcessor(CompleteWorkflowRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(CompleteWorkflowRequest request) {
    WorkflowCompletedEvent event = new WorkflowCompletedEvent();

    event.setInstanceId(request.getInstanceId());
    event.setPrecedingId(request.getEventId());
    event.setRegistrationId(request.getRegistrationId());

    PersistenceResult result = persist(event);

    ackWorkflowScheduledMessage(request);

    fireWorkflowCompleted(request.getInstanceId());

    getLogger().debug(
        "Complete workflow. Instance - " + request.getInstanceId() + ", startMode - " + request
            .getStartMode().name());
    if (request.getStartMode() == StartWorkflowRequest.StartMode.TIMER_START_SERIAL) {
      notifyTimerWorkflowCompletion(request);
    }

    return result;
  }

  protected CompleteWorkflowResponse buildResponse(
      CompleteWorkflowRequest request, Event event) {

    CompleteWorkflowResponse response = new CompleteWorkflowResponse();
    response.setInstanceId(request.getInstanceId());
    return response;
  }

  private void fireWorkflowCompleted(String instanceId) {
    CommonMessage message = new CommonMessage();
    message.setId(UUID.randomUUID().toString());
    message.setInstanceId(instanceId);
    providerFactory.getWorkflowCompletedProvider().send(message);
  }

  private void notifyTimerWorkflowCompletion(CompleteWorkflowRequest request) {
    WorkflowTimer
        workflowTimer =
        workflowTimerMapper.findByRegistrationId(request.getRegistrationId());

    if (workflowTimer != null) {
      workflowTimerMapper.updateNextFireTime(workflowTimer.getId(), CronUtils
          .getNextFireTime(workflowTimer.getCronExpression(), new Date()));
      getLogger().info("Update timer: " + JsonUtils.toJson(workflowTimer));
    }

  }

  private void ackWorkflowScheduledMessage(CompleteWorkflowRequest request) {
    consumerFactory.getConsumer(new WorkflowRealm(request.getAccessId(), request.getRealm())).ack(request.getRealmActId());
  }
}
