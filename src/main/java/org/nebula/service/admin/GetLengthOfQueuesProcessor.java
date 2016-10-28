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

package org.nebula.service.admin;

import org.nebula.admin.client.request.mgmt.GetLengthOfQueuesRequest;
import org.nebula.admin.client.response.mgmt.GetLengthOfQueuesResponse;
import org.nebula.service.admin.validator.GetLengthOfQueuesRequestValidator;
import org.nebula.service.core.ActivityRealm;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.message.consumer.Consumer;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetLengthOfQueuesProcessor extends
                                        AdminProcessor<GetLengthOfQueuesRequest, GetLengthOfQueuesResponse> {

  @Autowired
  private ConsumerFactory consumerFactory;

  @Autowired
  public GetLengthOfQueuesProcessor(
      GetLengthOfQueuesRequestValidator validator) {
    super(validator);
  }

  @Override
  protected GetLengthOfQueuesResponse processInternal(
      GetLengthOfQueuesRequest request) {

    String queueName = request.getQueueName();

    String accessId = request.getAccessId();

    Consumer consumer = null;
    if (request.getQueueType() == GetLengthOfQueuesRequest.QUEUE_TYPE.WORKFLOW) {
      consumer = consumerFactory.getConsumer(new WorkflowRealm(accessId,
                                                               queueName));
    } else if (request.getQueueType() == GetLengthOfQueuesRequest.QUEUE_TYPE.ACTIVITY) {
      consumer = consumerFactory.getConsumer(new ActivityRealm(accessId,
                                                               request.getActivity(),
                                                               request.getVersion(), queueName));
    } else if (request.getQueueType() == GetLengthOfQueuesRequest.QUEUE_TYPE.WORKFLOW_COMPLETED) {
      consumer = consumerFactory.getWorkflowCompletedConsumer();
    }

    GetLengthOfQueuesResponse response = new GetLengthOfQueuesResponse();

    response.setLengthOfQueue(consumer.lengthOfQueue());
    response.setLengthOfBackupQueue(consumer.lengthOfBackupQueue());
    response.setLengthOfAckQueue(consumer.lengthOfAckQueue());
    response.setNumberOfItems(consumer.numberOfMessage());

    return response;

  }
}
