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

import org.nebula.framework.client.Response.Status;
import org.nebula.framework.client.request.PollWorkflowRequest;
import org.nebula.framework.client.response.PollWorkflowResponse;
import org.nebula.service.core.WorkflowRealms;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.message.Message;
import org.nebula.service.message.WorkflowScheduledMessage;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.nebula.service.message.consumer.Consumers;
import org.nebula.service.validator.PollWorkflowRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PollWorkflowProcessor
    extends
    AbstractProcessor<PollWorkflowRequest, PollWorkflowResponse, Event> {

  @Value("${workflow.poll.timeout.secs:10}")
  private int workflowPollTimeoutSecs;

  @Autowired
  private ConsumerFactory consumerFactory;

  @Autowired
  public PollWorkflowProcessor(PollWorkflowRequestValidator validator) {
    super(validator);
  }

  protected PollWorkflowResponse buildResponse(
      PollWorkflowRequest pollWorkflowRequest, Event event) {

    Consumers consumers = new Consumers(consumerFactory,
                                        new WorkflowRealms(pollWorkflowRequest.getAccessId(),
                                                           pollWorkflowRequest.getRealms()));

    PollWorkflowResponse response = new PollWorkflowResponse();

    Message message = null;
    try {
      message = consumers.get(workflowPollTimeoutSecs);
    } catch (Exception e) {
      getLogger().error(
          "Failed to get WorkflowScheduledMessage message.", e);
      return response;
    }

    if (message != null) {
      response.setRegistrationId(((WorkflowScheduledMessage) message).getRegistrationId());
      response.setRealm(message.getRealm());
      response.setRealmActId(message.getId());
      response.setInstanceId(message.getInstanceId());
      response.setStatus(Status.SUCCESS);
    } else {
      response.setStatus(Status.POLL_TIMEOUT);
    }
    return response;
  }

}