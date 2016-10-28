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

import org.nebula.service.core.Realm;
import org.nebula.service.core.Realms;
import org.nebula.service.message.Message;
import org.nebula.service.util.RealmUtils;

import java.util.List;

public class Consumers {

  private final static int DISABLE_BLOCK_CONSUME = -1;

  private ConsumerFactory consumerFactory;

  private Realms realms;

  private Consumer luckyConsumer;

  public Consumers(ConsumerFactory consumerFactory, Realms realms) {
    this.consumerFactory = consumerFactory;
    this.realms = realms;
  }

  public Message get(int timeout) throws Exception {

    List<Realm> realmList = realms.getRealms();
    Realm selectedRealm = RealmUtils.randomSelectRealm(realmList);

    realmList.remove(selectedRealm);

    for (Realm realm : realmList) {
      luckyConsumer = consumerFactory.getConsumer(realm);
      Message message = luckyConsumer.get(DISABLE_BLOCK_CONSUME);
      if (message != null) {
        return message;
      }
    }
    luckyConsumer = consumerFactory.getConsumer(selectedRealm);

    return luckyConsumer.get(timeout);

  }

  public void ack(String messageId) {
    if (luckyConsumer != null) {
      luckyConsumer.ack(messageId);
    }
  }
}
