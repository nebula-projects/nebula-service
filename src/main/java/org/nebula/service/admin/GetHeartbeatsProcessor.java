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

import org.nebula.admin.client.model.nebula.HeartbeatSummary;
import org.nebula.admin.client.request.mgmt.GetHeartbeatsRequest;
import org.nebula.admin.client.response.mgmt.GetHeartbeatsResponse;
import org.nebula.service.admin.validator.GetHeartbeatsRequestValidator;
import org.nebula.service.dao.entity.Heartbeat;
import org.nebula.service.dao.mapper.HeartbeatMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetHeartbeatsProcessor extends
                                    AdminProcessor<GetHeartbeatsRequest, GetHeartbeatsResponse> {

  @Autowired
  private HeartbeatMapper heartbeatMapper;

  @Autowired
  public GetHeartbeatsProcessor(GetHeartbeatsRequestValidator validator) {
    super(validator);
  }

  public GetHeartbeatsResponse processInternal(
      GetHeartbeatsRequest request) {

    List<Heartbeat> heartbeats = heartbeatMapper.findByRegistrationId(request.getRegistrationId());

    GetHeartbeatsResponse response = new GetHeartbeatsResponse();
    response.setHeartbeatSummaries(extractSummaries(heartbeats));

    return response;
  }

  private List<HeartbeatSummary> extractSummaries(
      List<Heartbeat> heartbeats) {
    List<HeartbeatSummary> summaries = new ArrayList<HeartbeatSummary>();

    for (Heartbeat heartbeat : heartbeats) {
      HeartbeatSummary summary = new HeartbeatSummary();

      BeanUtils.copyProperties(heartbeat, summary);

      summaries.add(summary);
    }

    return summaries;
  }
}
