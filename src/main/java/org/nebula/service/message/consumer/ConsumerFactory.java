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

package org.nebula.service.message.consumer;

import org.apache.log4j.Logger;
import org.nebula.service.core.ActivityRealm;
import org.nebula.service.core.Realm;
import org.nebula.service.core.WorkflowRealm;
import org.nebula.service.message.ActivityScheduledMessageConverter;
import org.nebula.service.message.WorkflowScheduledMessageConverter;
import org.nebula.service.message.consumer.impl.RedisConsumer;
import org.nebula.service.message.provider.ProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import redis.clients.jedis.JedisPool;

@Component
public class ConsumerFactory {

  private final static Logger logger = Logger
      .getLogger(ConsumerFactory.class);

  private final static Map<Realm, Consumer> consumers = new ConcurrentHashMap<Realm, Consumer>();

  @Autowired
  private JedisPool jedisPool;

  private volatile Consumer workflowCompletedConsumer;

  public Consumer getConsumer(Realm realm) {

    Consumer consumer = consumers.get(realm);

    if (consumer == null) {
      synchronized (ConsumerFactory.class) {
        if (consumer == null) {
          consumer = new RedisConsumer(jedisPool,
                                       realm, 60);

          if (realm instanceof WorkflowRealm) {
            consumer.setMessageConverter(new WorkflowScheduledMessageConverter());
          } else if (realm instanceof ActivityRealm) {
            consumer.setMessageConverter(new ActivityScheduledMessageConverter());
          }

          consumers.put(realm, consumer);
          logger.info("queue " + realm.getQueueKey()
                      + " and consumer is " + consumer);
        }
      }
    }

    return consumer;
  }

  public Consumer getWorkflowCompletedConsumer() {

    if (workflowCompletedConsumer == null) {
      synchronized (this) {
        if (workflowCompletedConsumer == null) {
          workflowCompletedConsumer = new RedisConsumer(jedisPool,
                                                        ProviderFactory.WORKFLOW_COMPLETED_REALM,
                                                        60);
        }
      }
    }
    return workflowCompletedConsumer;
  }

}
