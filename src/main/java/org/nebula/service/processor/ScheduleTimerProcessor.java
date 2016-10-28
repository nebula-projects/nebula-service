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

import org.nebula.framework.client.request.ScheduleTimerRequest;
import org.nebula.framework.client.response.ScheduleTimerResponse;
import org.nebula.framework.event.TimerScheduledEvent;
import org.nebula.service.dao.entity.ActivityTimer;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.ActivityTimerMapper;
import org.nebula.service.validator.ScheduleTimerRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class ScheduleTimerProcessor extends
                                    EventProcessor<ScheduleTimerRequest, ScheduleTimerResponse> {

  @Autowired
  private ActivityTimerMapper activityTimerMapper;

  @Autowired
  public ScheduleTimerProcessor(ScheduleTimerRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(ScheduleTimerRequest request) {
    TimerScheduledEvent event = new TimerScheduledEvent();
    event.setRegistrationId(request.getRegistrationId());
    event.setPeriod(request.getPeriod());
    event.setInstanceId(request.getInstanceId());
    event.setPrecedingId(request.getEventId());
    event.setRealms(request.getRealms());

    return persist(event);
  }


  protected void createMessageAndSend(ScheduleTimerRequest request,
                                      Event event) {

    ActivityTimer activityTimer = new ActivityTimer();
    activityTimer.setRegistrationId(request.getRegistrationId());
    activityTimer.setInstanceId(request.getInstanceId());
    activityTimer.setUsername(request.getAccessId());
    activityTimer.setRealms(toJson(request.getRealms()));
    activityTimer.setEventId(event.getId());
    activityTimer.setInterval(request.getPeriod());

    activityTimerMapper.insertTimer(activityTimer);

    getLogger().info("Save timer: " + toJson(activityTimer));
  }

  protected ScheduleTimerResponse buildResponse(ScheduleTimerRequest request,
                                                Event event) {
    ScheduleTimerResponse response = new ScheduleTimerResponse();
    response.setEventId(event.getId());

    return response;
  }
}
