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
import org.nebula.framework.client.request.CompleteActivityRequest;
import org.nebula.framework.client.request.StartActivityRequest;
import org.nebula.service.util.IdUtils;
import org.springframework.stereotype.Component;

@Component
public class StartActivityRequestValidator extends
                                           AbstractValidator<StartActivityRequest> {

  @Override
  protected void verify(StartActivityRequest request) {

    Validate.notNull(request,
                     "The StartActivity  request should not be null.");

    Validate.notEmpty(request.getRealms(),
                      "The realms should not be empty.");

    Validate.notNull(request.getActivityProfile(),
                     "The activity artifact should not be null.");

    Validate.notEmpty(request.getActivityProfile().getActivity(),
                      "The activity name should not be empty.");

    Validate.notEmpty(request.getActivityProfile().getVersion(),
                      "The activity version should not be empty.");

    Validate.notNull(request.getMethodProfile(),
                     "The activity method profile should not be null.");

    Validate.notEmpty(request.getMethodProfile().getName(),
                      "The activity method profile name should not be empty.");

    Validate.notEmpty(request.getMethodProfile().getReturnType(),
                      "The activity method profile returnType should not be empty.");

    Validate.notNull(request.getMethodProfile().getParameterTypes(),
                     "The activity method profile parameters should not be null.");

    Validate.notEmpty(request.getRegistrationId(),
                      "The registration id should not be empty.");

    Validate.notEmpty(request.getInstanceId(),
                      "The instance id should not be empty.");
  }

  @Override
  protected void verifyAuthorization(StartActivityRequest request){
    Validate.isTrue(
        request.getAccessId().equals(IdUtils.extractAccessId(request.getRegistrationId())) &&
        request.getAccessId().equals(IdUtils.extractAccessId(request.getInstanceId())),
        "No authorization for the request.");
  }

}
