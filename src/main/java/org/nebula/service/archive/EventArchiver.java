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

package org.nebula.service.archive;

import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.EventMapper;
import org.nebula.service.dao.mapper.HistoryEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventArchiver {

  private final static Logger logger = Logger
      .getLogger(EventArchiver.class);

  private final static int PAGE_SIZE = 20;

  @Autowired
  private HistoryEventMapper historyEventMapper;

  @Autowired
  private EventMapper eventMapper;

  public void archive(String instanceId) {
    backupToHistory(instanceId);
    removeFromCurrent(instanceId);
  }

  private void backupToHistory(String instanceId) {

    int pageNo = 1;
    int pageSize = PAGE_SIZE;
    int total = eventMapper.countByInstanceId(instanceId);

    int processed = 0;
    boolean hasNext = true;
    while (hasNext) {

      RowBounds rowBounds = new RowBounds((pageNo - 1) * pageSize,
                                          pageSize);

      List<Event> events = eventMapper.findByInstanceIdByPage(rowBounds,
                                                              instanceId);
      if (events.size() == 0) {
        break;
      }

      insertIntoHistoryEvents(events);

      processed += events.size();

      hasNext = hasNext(total, processed);

      pageNo++;

    }
  }

  private boolean hasNext(int total, int processed) {
    if (processed < total) {
      return true;
    }
    return false;

  }

  private void insertIntoHistoryEvents(List<Event> events) {
    for (Event event : events) {
      try {
        historyEventMapper.insertEvent(event);
      } catch (DuplicateKeyException e) {
        logger.warn("Duplicate insert for instanceId="
                    + event.getInstanceId() + ", eventId=" + event.getId(), e);
      }
    }
  }

  private void removeFromCurrent(String instanceId) {
    eventMapper.deleteInstance(instanceId);
  }
}
