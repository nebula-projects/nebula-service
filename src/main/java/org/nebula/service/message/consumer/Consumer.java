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

import org.nebula.service.message.Message;
import org.nebula.service.message.MessageConverter;

public interface Consumer {

  public void setMessageConverter(MessageConverter messageConverter);

  public Message get(int timeout) throws Exception;

  public void release(String messageId) throws Exception;

  public void ack(String messageId);

  public long lengthOfQueue();

  public long lengthOfAckQueue();

  public long lengthOfBackupQueue();

  public long numberOfMessage();
}
