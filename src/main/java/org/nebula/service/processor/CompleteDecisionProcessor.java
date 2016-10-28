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

import org.nebula.framework.client.request.CompleteDecisionRequest;
import org.nebula.framework.client.response.CompleteDecisionResponse;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.nebula.service.validator.CompleteDecisionRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompleteDecisionProcessor
    extends
    AbstractProcessor<CompleteDecisionRequest, CompleteDecisionResponse, Event> {

  @Autowired
  private ConsumerFactory consumerFactory;

  @Autowired
  public CompleteDecisionProcessor(CompleteDecisionRequestValidator validator) {
    super(validator);
  }

  protected CompleteDecisionResponse buildResponse(
      CompleteDecisionRequest request, Event event) {

    ackWorkflowScheduledMessage(request);

    CompleteDecisionResponse response = new CompleteDecisionResponse();
    response.setInstanceId(request.getInstanceId());
    return response;
  }

  private void ackWorkflowScheduledMessage(CompleteDecisionRequest request) {
    consumerFactory.getConsumer(new WorkflowRealm(request.getAccessId(), request.getRealm())).ack(request.getRealmActId());
  }

}
