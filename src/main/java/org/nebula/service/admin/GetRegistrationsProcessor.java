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

import org.apache.ibatis.session.RowBounds;
import org.nebula.admin.client.model.nebula.RegistrationSummary;
import org.nebula.admin.client.request.mgmt.GetRegistrationsRequest;
import org.nebula.admin.client.response.mgmt.GetRegistrationsResponse;
import org.nebula.service.admin.validator.GetRegistrationsRequestValidator;
import org.nebula.service.dao.entity.Registration;
import org.nebula.service.dao.mapper.RegistrationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetRegistrationsProcessor extends
                                       AdminProcessor<GetRegistrationsRequest, GetRegistrationsResponse> {

  @Autowired
  private RegistrationMapper registrationMapper;

  @Autowired
  public GetRegistrationsProcessor(GetRegistrationsRequestValidator validator) {
    super(validator);
  }

  public GetRegistrationsResponse processInternal(
      GetRegistrationsRequest request) {

    RowBounds
        rowBounds =
        new RowBounds((request.getPageNo() - 1) * request.getPageSize(), request.getPageSize());

    List<Registration> registrations = registrationMapper.findRegistrations(rowBounds,
                                                                            request.getUsername(),
                                                                            request
                                                                                .getWorkflowName(),
                                                                            request
                                                                                .getNodeType()
                                                                            == null ? null : request
                                                                                .getNodeType()
                                                                                .name(), request
                                                                                .getCreatedBefore(),
                                                                            request
                                                                                .getCreatedAfter());

    int total = registrationMapper.countRegistrations(
        request.getUsername(), request.getWorkflowName(), request
                                                              .getNodeType() == null ? null
                                                                                     : request
                                                              .getNodeType()
                                                              .name(), request.getCreatedBefore(),
        request.getCreatedAfter());

    GetRegistrationsResponse response = new GetRegistrationsResponse();
    response.setRegistrationSummaries(extractSummaries(registrations));

    response.setPageNo(request.getPageNo());
    response.setPageSize(request.getPageSize());
    response.setTotal(total);

    return response;
  }

  private List<RegistrationSummary> extractSummaries(
      List<Registration> registrations) {
    List<RegistrationSummary> summaries = new ArrayList<RegistrationSummary>();

    for (Registration registration : registrations) {
      RegistrationSummary summary = new RegistrationSummary();

      BeanUtils.copyProperties(registration, summary);

      summaries.add(summary);
    }

    return summaries;
  }
}
