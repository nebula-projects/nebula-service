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

package org.nebula.service.timer;

import org.apache.log4j.Logger;
import org.nebula.framework.event.TimerCompletedEvent;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.ActivityTimer;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.ActivityTimerMapper;
import org.nebula.service.dao.mapper.EventMapper;
import org.nebula.service.message.WorkflowScheduledMessage;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.util.RealmUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;
import static org.nebula.service.util.JsonUtils.toList;

@Component
public class ActivityTimerExecutor {

  private final static Logger logger = Logger
      .getLogger(ActivityTimerExecutor.class);

  @Autowired
  protected EventMapper eventMapper;

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  private ActivityTimerMapper activityTimerMapper;

  public void execute(ActivityTimer activityTimer) {

    int eventId = persistTimerCompletedEvent(activityTimer);

    createMessageAndSend(activityTimer, eventId);
    activityTimerMapper.deleteTimerById(activityTimer.getId());

    logger.info("Delete Timer:" + toJson(activityTimer.getId()));
  }


  private int persistTimerCompletedEvent(ActivityTimer activityTimer) {
    TimerCompletedEvent timerCompletedEvent = new TimerCompletedEvent();
    timerCompletedEvent.setInstanceId(activityTimer.getInstanceId());
    timerCompletedEvent.setPrecedingId(activityTimer.getEventId());
    timerCompletedEvent.setRegistrationId(activityTimer.getRegistrationId());

    Event event = new Event();
    event.setEventType(timerCompletedEvent.getEventType().name());
    event.setInstanceId(activityTimer.getInstanceId());
    event.setPrecedingId(activityTimer.getEventId());
    event.setRegistrationId(activityTimer.getRegistrationId());

    event.setData(toJson(timerCompletedEvent));

    eventMapper.insertEvent(event);

    logger.info("Persist TimerCompletedEvent:" + toJson(event));

    return event.getId();
  }

  private void createMessageAndSend(ActivityTimer activityTimer, int eventId) {

    List<String> realms = toList(activityTimer.getRealms(), String.class);
    String properRealm = RealmUtils.getProperRealm(realms);

    WorkflowScheduledMessage
        message =
        new WorkflowScheduledMessage(activityTimer.getRegistrationId(),
                                     activityTimer.getInstanceId(), eventId, properRealm);
    message.setId(UUID.randomUUID().toString());
    providerFactory.getProvider(new WorkflowRealm(activityTimer.getUsername(), properRealm))
        .send(message);

    logger.info("Send WorkflowScheduledMessage:" + toJson(message));
  }
}
