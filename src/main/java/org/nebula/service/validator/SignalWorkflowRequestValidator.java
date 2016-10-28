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
import org.nebula.framework.client.request.CancelWorkflowRequest;
import org.nebula.framework.client.request.SignalWorkflowRequest;
import org.nebula.service.util.IdUtils;
import org.springframework.stereotype.Component;

@Component
public class SignalWorkflowRequestValidator extends
                                            AbstractValidator<SignalWorkflowRequest> {

  @Override
  protected void verify(SignalWorkflowRequest request) {
    Validate.notNull(request,
                     "The signal workflow request should not be null.");

    Validate.notNull(request.getRealms(),
                     "The realms should not be null.");

    Validate.notEmpty(request.getInstanceId(),
                      "The workflow version should not be empty.");

    Validate.notNull(request.getSignalProfile(),
                     "The signal profile should not be null.");

    Validate.notEmpty(request.getSignalProfile().getName(),
                      "The signal profile name should not be empty.");
  }

  @Override
  protected void verifyAuthorization(SignalWorkflowRequest request){
    Validate.isTrue(request.getAccessId().equals(IdUtils.extractAccessId(request.getInstanceId())),
                    "No authorization for the request.");
  }

}
