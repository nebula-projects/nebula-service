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

package org.nebula.service.validator;

import org.apache.commons.lang.Validate;
import org.nebula.framework.client.request.CompleteWorkflowRequest;
import org.nebula.framework.client.request.GetEventsRequest;
import org.nebula.service.util.IdUtils;
import org.springframework.stereotype.Component;

@Component
public class GetEventsRequestValidator extends
                                       AbstractValidator<GetEventsRequest> {

  @Override
  protected void verify(GetEventsRequest request) {

    Validate.notNull(request,
                     "The getEvents request should not be null.");

    Validate.notEmpty(request.getInstanceId(),
                      "The instance id should not be empty.");

    Validate.isTrue(request.getPageNo() > 0, "The offset should be positive.");

    Validate.isTrue(request.getPageSize() > 0, "The pageSize should be positive.");

  }

  @Override
  protected void verifyAuthorization(GetEventsRequest request){
    Validate.isTrue(request.getAccessId().equals(IdUtils.extractAccessId(request.getInstanceId())),
                    "No authorization for the request.");
  }

}
