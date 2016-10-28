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

import org.nebula.framework.client.Request;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.EventMapper;
import org.nebula.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import static org.nebula.service.util.JsonUtils.toJson;

public abstract class EventProcessor<S extends Request, T> extends
                                           AbstractProcessor<S, T, Event> {

  protected final static int WITHOUT_PRECEDING_ID = -1;
  @Autowired
  protected EventMapper eventMapper;

  public EventProcessor(Validator validator) {
    super(validator);
  }

  protected PersistenceResult persist(org.nebula.framework.event.Event historyEvent) {

    Event event = new Event();
    event.setRegistrationId(historyEvent.getRegistrationId());
    event.setInstanceId(historyEvent.getInstanceId());
    event.setPrecedingId(historyEvent.getPrecedingId());
    event.setEventType(historyEvent.getEventType().name());
    event.setData(toJson(historyEvent));

    PersistenceResult result = null;
    try {
      eventMapper.insertEvent(event);
      result = new PersistenceResult(true, event);
    } catch (DuplicateKeyException e) {
      getLogger().warn("Duplicated event: " + toJson(event));

      event =
          eventMapper.findByInstanceIdAndTypeAndPrecedingId(historyEvent.getInstanceId(),
                                                            historyEvent.getEventType().name(),
                                                            historyEvent.getPrecedingId());
      result = new PersistenceResult(false, event);
    }

    getLogger().info(
        "persisted historyEvent id=" + event.getId() + ",instanceId="
        + event.getInstanceId());

    return result;
  }
}
