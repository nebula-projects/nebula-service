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
import org.nebula.framework.client.request.CancelTimerRequest;
import org.nebula.service.util.IdUtils;
import org.springframework.stereotype.Component;


@Component
public class CancelTimerRequestValidator extends
                                         AbstractValidator<CancelTimerRequest> {

  @Override
  protected void verify(CancelTimerRequest request) {

    Validate.notNull(request,
                     "The cancel timer  request should not be null.");

    Validate.notEmpty(request.getRealms(),
                      "The realms should not be empty.");

    Validate.notEmpty(request.getRegistrationId(),
                      "The registration id should not be empty.");

    Validate.notEmpty(request.getInstanceId(),
                      "The instance id should not be empty.");
  }

  @Override
  protected void verifyAuthorization(CancelTimerRequest request){
    Validate.isTrue(request.getAccessId().equals(
                        IdUtils.extractAccessId(request.getRegistrationId())) &&
                    request.getAccessId().equals(IdUtils.extractAccessId(request.getInstanceId())),
                    "No authorization for the request.");
  }

}
