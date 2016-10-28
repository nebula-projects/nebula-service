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
import org.nebula.admin.client.request.mgmt.GetHistoryEventsRequest;
import org.nebula.admin.client.response.mgmt.GetHistoryEventsResponse;
import org.nebula.service.admin.validator.GetHistoryEventsRequestValidator;
import org.nebula.service.core.UserProcessCache;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.HistoryEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.nebula.service.util.JsonUtils.toObject;

@Component
public class GetHistoryEventsProcessor
    extends AdminProcessor<GetHistoryEventsRequest, GetHistoryEventsResponse> {

  private static Logger logger = LoggerFactory.getLogger(GetHistoryEventsProcessor.class);

  @Autowired
  private HistoryEventMapper historyEventMapper;

  @Autowired
  private UserProcessCache userProcessCache;

  @Autowired
  public GetHistoryEventsProcessor(GetHistoryEventsRequestValidator validator) {
    super(validator);
  }

  @Override
  protected GetHistoryEventsResponse processInternal(GetHistoryEventsRequest request) {
    String instanceId = request.getInstanceId();

    RowBounds rowBounds = new RowBounds((request.getPageNo() - 1)
                                        * request.getPageSize(), request.getPageSize());

    List<Event> pevents = historyEventMapper.findByInstanceIdByPage(rowBounds,
                                                                    instanceId);

    List<org.nebula.framework.event.Event>
        events =
        new ArrayList<org.nebula.framework.event.Event>();

    String accessId = request.getAccessId();
    if (!checkIfHasPrivilege(accessId, pevents)) {
      logger.warn("The user " + accessId + " doesn't has the instance " + instanceId);
      return createGetHistoryEventsResponse(events, request.getPageNo(), 0);
    }

    for (Event event : pevents) {

      org.nebula.framework.event.Event
          eve =
          (org.nebula.framework.event.Event) toObject(event.getData(),
                                                      "org.nebula.framework.event." + event
                                                          .getEventType());

      eve.setEventId(event.getId());
      eve.setPrecedingId(event.getPrecedingId());
      events.add(eve);

    }

    return createGetHistoryEventsResponse(events, request.getPageNo(),
                                          historyEventMapper.countByInstanceId(instanceId));
  }

  private GetHistoryEventsResponse createGetHistoryEventsResponse(
      List<org.nebula.framework.event.Event> events, int pageNo, int total) {
    GetHistoryEventsResponse response = new GetHistoryEventsResponse();
    response.setEvents(events);
    response.setPageNo(pageNo);
    response.setSize(events.size());
    response.setTotal(total);

    return response;
  }

  private boolean checkIfHasPrivilege(String accessId, List<Event> pevents) {
    if (pevents.size() == 0) {
      return false;
    }
    return userProcessCache.hasProcess(accessId, pevents.get(0).getRegistrationId());
  }
}
