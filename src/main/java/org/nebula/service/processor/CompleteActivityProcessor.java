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

import org.nebula.framework.client.request.CompleteActivityRequest;
import org.nebula.framework.client.response.CompleteActivityResponse;
import org.nebula.framework.event.ActivityCompletedEvent;
import org.nebula.framework.model.ActivityProfile;
import org.nebula.service.core.ActivityRealm;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.message.WorkflowScheduledMessage;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.nebula.service.message.provider.ProviderFactory;
import org.nebula.service.validator.CompleteActivityRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class CompleteActivityProcessor
    extends
    EventProcessor<CompleteActivityRequest, CompleteActivityResponse> {

  @Autowired
  private ProviderFactory providerFactory;

  @Autowired
  private ConsumerFactory consumerFactory;

  @Autowired
  public CompleteActivityProcessor(CompleteActivityRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(CompleteActivityRequest request) {
    ActivityCompletedEvent event = new ActivityCompletedEvent();
    event.setActivityProfile(request.getActivityProfile());
    event.setMethodProfile(request.getMethodProfile());
    event.setInput(request.getInput());
    event.setInstanceId(request.getInstanceId());
    event.setPrecedingId(request.getEventId());
    event.setRegistrationId(request.getRegistrationId());

    return persist(event);
  }

  protected void createMessageAndSend(CompleteActivityRequest request,
                                      Event event) {
    WorkflowScheduledMessage message = new WorkflowScheduledMessage(
        request.getRegistrationId(), request.getInstanceId(),
        request.getEventId(), request.getRealm());

    message.setId(UUID.randomUUID().toString());

    getLogger().info("Send WorkflowScheduledMessage:" + toJson(message));

    String accessId = request.getAccessId();

    providerFactory.getProvider(
        new WorkflowRealm(accessId, request.getRealm())).send(message);

    ActivityProfile profile = request.getActivityProfile();
    ackStartActivityMessage(accessId, profile.getActivity(),
                            profile.getVersion(), request.getRealm(),
                            request.getRealmActId());
  }

  protected CompleteActivityResponse buildResponse(
      CompleteActivityRequest request, Event event) {

    CompleteActivityResponse response = new CompleteActivityResponse();
    response.setInstanceId(request.getInstanceId());
    return response;
  }

  private void ackStartActivityMessage(String username, String activity,
                                       String version, String realm, String realmActId) {

    consumerFactory.getConsumer(
        new ActivityRealm(username, activity, version, realm)).ack(
        realmActId);
  }

}
