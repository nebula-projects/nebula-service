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
import org.nebula.admin.client.request.mgmt.EnableRegistrationRequest;
import org.nebula.admin.client.request.mgmt.GetHistoryEventsRequest;
import org.nebula.service.util.IdUtils;
import org.nebula.service.validator.AbstractValidator;
import org.nebula.service.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class EnableRegistrationRequestValidator extends
                                                AbstractValidator<EnableRegistrationRequest> {

  @Override
  protected void verify(EnableRegistrationRequest request) {

    Validate.notNull(request,
                     "The enableRegistration request should not be null.");

    Validate.notEmpty(request.getId(), "The id should not be empty.");
  }

  @Override
  protected void verifyAuthorization(EnableRegistrationRequest request){
    Validate.isTrue(request.getAccessId().equals(
                        IdUtils.extractAccessId(request.getId())),
                    "No authorization for the request.");
  }
}
