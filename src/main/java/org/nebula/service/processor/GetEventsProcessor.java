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

package org.nebula.service.processor;

import org.apache.ibatis.session.RowBounds;
import org.nebula.framework.client.request.GetEventsRequest;
import org.nebula.framework.client.response.GetEventsResponse;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.EventMapper;
import org.nebula.service.validator.GetEventsRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.nebula.service.util.JsonUtils.toObject;

@Component
public class GetEventsProcessor
    extends
    AbstractProcessor<GetEventsRequest, GetEventsResponse, Event> {

  @Autowired
  protected EventMapper eventMapper;

  @Autowired
  public GetEventsProcessor(GetEventsRequestValidator validator) {
    super(validator);
  }

  protected GetEventsResponse buildResponse(
      GetEventsRequest request, Event aEvent) {
    String instanceId = request.getInstanceId();

    RowBounds rowBounds = new RowBounds((request.getPageNo() - 1)
                                        * request.getPageSize(), request.getPageSize());

    List<Event> pevents = eventMapper.findByInstanceIdByPage(rowBounds,
                                                             instanceId);

    List<org.nebula.framework.event.Event>
        events =
        new ArrayList<org.nebula.framework.event.Event>();
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

    GetEventsResponse response = new GetEventsResponse();
    response.setEvents(events);
    response.setPageNo(request.getPageNo());
    response.setSize(events.size());
    response.setTotal(eventMapper.countByInstanceId(instanceId));

    return response;
  }

}
