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
import org.nebula.framework.client.request.HeartbeatRequest;
import org.nebula.service.util.IdUtils;
import org.springframework.stereotype.Component;

@Component
public class HeartbeatRequestValidator extends
                                       AbstractValidator<HeartbeatRequest> {

  @Override
  protected void verify(HeartbeatRequest request) {

    Validate.notNull(request, "The heatbeat request should not be null.");

    Validate.notNull(request.getRegistrationId(),
                     "The registration id should not be null.");

    Validate.notEmpty(request.getHost(),
                      "The hostname should not be empty.");

    Validate.notEmpty(request.getIp(), "The ip should not be empty.");

    Validate.notEmpty(request.getProcessId(),
                      "The name  should not be empty.");
  }

  @Override
  protected void verifyAuthorization(HeartbeatRequest request){
    Validate.isTrue(
        request.getAccessId().equals(IdUtils.extractAccessId(request.getRegistrationId())),
        "No authorization for the request.");
  }

}
