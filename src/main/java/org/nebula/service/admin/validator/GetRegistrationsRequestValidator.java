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
import org.nebula.admin.client.request.mgmt.GetRegistrationRequest;
import org.nebula.admin.client.request.mgmt.GetRegistrationsRequest;
import org.nebula.service.util.IdUtils;
import org.nebula.service.validator.AbstractValidator;
import org.nebula.service.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class GetRegistrationsRequestValidator extends
                                              AbstractValidator<GetRegistrationsRequest> {

  @Override
  protected void verify(GetRegistrationsRequest request) {

    Validate.notNull(request,
                     "The getRegistrations request should not be null.");

    Validate.isTrue(request.getPageSize() <= 100,
                    "The page size should be less than or equals 100.");

    Validate.isTrue(request.getPageNo() >= 0,
                    "The page No. should be greater than or equals zero.");

  }

  @Override
  protected void verifyAuthorization(GetRegistrationsRequest request){
    Validate.isTrue(request.getAccessId().equals(request.getUsername()),
                    "No authorization for the request.");
  }


}
