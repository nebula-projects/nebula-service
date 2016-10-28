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

import org.nebula.admin.client.request.mgmt.GetRegistrationRequest;
import org.nebula.admin.client.response.mgmt.GetRegistrationResponse;
import org.nebula.framework.client.request.RegisterRequest.RegistrationInfo;
import org.nebula.service.admin.validator.GetRegistrationRequestValidator;
import org.nebula.service.dao.entity.Registration;
import org.nebula.service.dao.mapper.RegistrationMapper;
import org.nebula.service.util.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetRegistrationProcessor extends
                                      AdminProcessor<GetRegistrationRequest, GetRegistrationResponse> {

  @Autowired
  private RegistrationMapper registrationMapper;

  @Autowired
  public GetRegistrationProcessor(GetRegistrationRequestValidator validator) {
    super(validator);
  }

  public GetRegistrationResponse processInternal(
      GetRegistrationRequest request) {

    Registration registration = registrationMapper
        .findById(request.getId());

    GetRegistrationResponse response = new GetRegistrationResponse();

    if (registration != null) {
      BeanUtils.copyProperties(registration, response);

      response.setRegistrationInfo((RegistrationInfo) JsonUtils.toObject(
          registration.getData(), RegistrationInfo.class));
    }

    return response;
  }

}
