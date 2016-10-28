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
import org.nebula.service.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class EventArchiveDaemon {

  private final static Logger logger = Logger
      .getLogger(EventArchiveDaemon.class);
  private final static int ONE_MINUTE = 60;
  @Autowired
  private EventArchiver eventArchiver;
  @Autowired
  private MessageManager messageManager;

  private boolean shouldRun = true;

  public void start() {
    this.shouldRun = true;
    this.notifyAll();
  }

  public void stop() {
    this.shouldRun = false;
  }

  @PostConstruct
  public void run() {

    new Thread(new Runnable() {
      public void run() {

        logger.info("EventCleanDaemon start");

        while (true) {

          checkAndWait();

          Message message = null;

          try {
            message = messageManager.pollMessage();

            String instanceId = null;

            if (message == null || (instanceId = message.getInstanceId()) == null) {
              sleep(ONE_MINUTE);
              continue;
            }
            logger.info("Clean instance:" + instanceId);

            eventArchiver.archive(instanceId);

            messageManager.ackMessage(message.getId());

          } catch (Exception e) {
            logger.error("Failed to clean event for message: "
                         + message, e);
          }

        }
      }
    }).start();

  }

  private void checkAndWait() {
    while (!shouldRun) {

      synchronized (EventArchiveDaemon.class) {
        try {
          //TODO is it correct to wait forever?
          this.wait();
        } catch (InterruptedException e) {
          // ignore
        }
      }

    }
  }

  private void sleep(int secs) {
    try {
      Thread.sleep(1000 * secs);
    } catch (InterruptedException e) {
      //Ignore
    }
  }
}
