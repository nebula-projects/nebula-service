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

import org.nebula.framework.client.request.CancelTimerRequest;
import org.nebula.framework.client.response.CancelTimerResponse;
import org.nebula.framework.event.TimerCancelledEvent;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.mapper.ActivityTimerMapper;
import org.nebula.service.message.WorkflowScheduledMessage;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.util.RealmUtils;
import org.nebula.service.validator.CancelTimerRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class CancelTimerProcessor extends
                                  EventProcessor<CancelTimerRequest, CancelTimerResponse> {

  @Autowired
  private ActivityTimerMapper activityTimerMapper;

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  public CancelTimerProcessor(CancelTimerRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(CancelTimerRequest request) {
    TimerCancelledEvent event = new TimerCancelledEvent();
    event.setInstanceId(request.getInstanceId());
    event.setPrecedingId(request.getTimerId());
    event.setRegistrationId(request.getRegistrationId());

    PersistenceResult persisted = persist(event);

    deleteTimer(request);

    return persisted;
  }

  protected void createMessageAndSend(CancelTimerRequest request,
                                      Event event) {

    String properRealm = RealmUtils.getProperRealm(request.getRealms());

    WorkflowScheduledMessage message = new WorkflowScheduledMessage(
        event.getRegistrationId(), event.getInstanceId(),
        event.getId(), properRealm);

    message.setId(UUID.randomUUID().toString());
    try {
      getLogger()
          .info("Send WorkflowScheduledMessage:" + toJson(message));
      providerFactory.getProvider(new WorkflowRealm(request.getAccessId(), properRealm)).send(message);
    } catch (Exception e) {
      getLogger().error("Failed to send message.", e);
    }

  }

  protected CancelTimerResponse buildResponse(CancelTimerRequest request,
                                              Event event) {

    CancelTimerResponse response = new CancelTimerResponse();
    response.setInstanceId(request.getInstanceId());
    response.setEventId(event.getId());

    return response;
  }

  private void deleteTimer(CancelTimerRequest request) {
    activityTimerMapper
        .deleteTimerByInstanceIdAndEventId(request.getInstanceId(), request.getTimerId());
    getLogger().info(
        "Deleted timer: instanceId - " + request.getInstanceId() + ", eventId - " + request
            .getTimerId());
  }
}
