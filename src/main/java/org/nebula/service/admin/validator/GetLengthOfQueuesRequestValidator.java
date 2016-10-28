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

package org.nebula.service.admin.validator;

import org.apache.commons.lang.Validate;
import org.nebula.admin.client.request.mgmt.GetLengthOfQueuesRequest;
import org.nebula.service.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class GetLengthOfQueuesRequestValidator implements
                                               Validator<GetLengthOfQueuesRequest> {

  public void validate(GetLengthOfQueuesRequest request) {

    Validate.notNull(request,
                     "The getLengthOfQueues request should not be null.");

    Validate.notEmpty(request.getQueueName(),
                      "The queue name should not be empty.");

    Validate.isTrue(
        request.getQueueType() == GetLengthOfQueuesRequest.QUEUE_TYPE.WORKFLOW
        || request.getQueueType() == GetLengthOfQueuesRequest.QUEUE_TYPE.ACTIVITY
        || request.getQueueType() == GetLengthOfQueuesRequest.QUEUE_TYPE.WORKFLOW_COMPLETED,
        "The queue type should be WORKFLOW, ACTIVITY or WORKFLOW_COMPLETED.");
  }
}
