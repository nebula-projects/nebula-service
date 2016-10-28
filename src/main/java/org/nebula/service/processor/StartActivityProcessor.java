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

import org.nebula.framework.client.request.StartActivityRequest;
import org.nebula.framework.client.response.StartActivityResponse;
import org.nebula.framework.event.ActivityScheduledEvent;
import org.nebula.framework.model.ActivityProfile;
import org.nebula.service.core.ActivityRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.message.ActivityScheduledMessage;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.util.RealmUtils;
import org.nebula.service.validator.StartActivityRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class StartActivityProcessor extends
                                    EventProcessor<StartActivityRequest, StartActivityResponse> {

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  public StartActivityProcessor(StartActivityRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(StartActivityRequest request) {
    ActivityScheduledEvent event = new ActivityScheduledEvent();
    event.setActivityProfile(request.getActivityProfile());
    event.setMethodProfile(request.getMethodProfile());
    event.setInput(request.getInput());
    event.setInstanceId(request.getInstanceId());
    event.setPrecedingId(request.getEventId());
    event.setRegistrationId(request.getRegistrationId());

    return persist(event);
  }

  protected void createMessageAndSend(StartActivityRequest request,
                                      Event event) {
    String realm = RealmUtils.getProperRealm(request.getRealms());

    ActivityScheduledMessage message = new ActivityScheduledMessage(
        event.getRegistrationId(), event.getInstanceId(),
        event.getId(), realm);

    ActivityProfile activityProfile = request.getActivityProfile();

    message.setId(UUID.randomUUID().toString());

    message.setActivityProfile(activityProfile);
    message.setMethodProfile(request.getMethodProfile());
    message.setInput(request.getInput());

    getLogger().info("Send ActivityScheduledMessage:" + toJson(message));

    providerFactory.getProvider(new ActivityRealm(request.getAccessId(), activityProfile.getActivity(),
                                                  activityProfile.getVersion(), realm))
        .send(message);

  }

  protected StartActivityResponse buildResponse(StartActivityRequest request,
                                                Event event) {
    StartActivityResponse response = new StartActivityResponse();
    response.setEventId(event.getId());

    return response;
  }
}
