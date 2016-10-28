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

import org.apache.log4j.Logger;
import org.nebula.service.message.CommonMessage;
import org.nebula.service.message.Message;
import org.nebula.service.message.consumer.Consumer;
import org.nebula.service.message.consumer.ConsumerFactory;
import org.nebula.service.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageManager {

  private final static Logger logger = Logger
      .getLogger(MessageManager.class);

  private Consumer consumer;

  @Autowired
  public MessageManager(ConsumerFactory consumerFactory) {
    consumer = consumerFactory.getWorkflowCompletedConsumer();
  }

  public Message pollMessage() {
    try {
      CommonMessage message = (CommonMessage) consumer.get(10);

      if (message != null && message.getPayload() != null) {
        CommonMessage commonMsg = MessageUtils.toMessage(
            message.getPayload(), CommonMessage.class);

        return commonMsg;
      }
    } catch (Exception e) {
      logger.warn(
          "Failed to get instance id from WorkflowCompletedQueue.", e);
    }
    return null;
  }

  public void ackMessage(String messageId) {
    consumer.ack(messageId);
  }

}
