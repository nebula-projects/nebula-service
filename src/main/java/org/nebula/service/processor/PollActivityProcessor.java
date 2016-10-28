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

import org.nebula.framework.client.Response.Status;
import org.nebula.framework.client.Response;
import org.nebula.framework.client.request.PollActivityRequest;
import org.nebula.framework.client.response.PollActivityResponse;
import org.nebula.service.core.ActivityRealms;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.message.ActivityScheduledMessage;
import org.nebula.service.message.Message;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.nebula.service.message.consumer.Consumers;
import org.nebula.service.validator.PollActivityRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PollActivityProcessor
    extends
    AbstractProcessor<PollActivityRequest, PollActivityResponse, Event> {

  @Value("${activity.poll.timeout.secs:10}")
  private int activityPollTimeoutSecs;

  @Autowired
  private ConsumerFactory consumerFactory;

  @Autowired
  public PollActivityProcessor(PollActivityRequestValidator validator) {
    super(validator);
  }

  protected PollActivityResponse buildResponse(
      PollActivityRequest pollActivityRequest, Event event) {

    Consumers consumers = new Consumers(consumerFactory,
                                        new ActivityRealms(pollActivityRequest.getAccessId(), pollActivityRequest
                                            .getActivity(), pollActivityRequest.getVersion(),
                                                           pollActivityRequest.getRealms()));

    PollActivityResponse response = new PollActivityResponse();

    Message message = null;
    try {
      message = consumers.get(activityPollTimeoutSecs);
    } catch (Exception e) {
      getLogger().error(
          "Failed to get ActivityScheduledMessage message.", e);
      return response;
    }

    if (message != null) {
      ActivityScheduledMessage asm = (ActivityScheduledMessage) message;
      response.setStatus(Status.SUCCESS);
      response.setInstanceId(asm.getInstanceId());
      response.setActivityProfile(asm.getActivityProfile());
      response.setMethodProfile(asm.getMethodProfile());
      response.setInput(asm.getInput());
      response.setRealm(asm.getRealm());
      response.setRealmActId(asm.getId());
      response.setEventId(asm.getEventId());
      response.setRegistrationId(asm.getRegistrationId());
    } else {
      response.setStatus(Response.Status.POLL_TIMEOUT);
    }
    return response;

  }
}