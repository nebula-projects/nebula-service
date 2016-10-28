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

import org.apache.log4j.Logger;
import org.nebula.framework.client.Request;
import org.nebula.service.core.ServiceContext;
import org.nebula.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractProcessor<S extends Request, T, E> implements Processor<S , T> {

  private Validator<S> validator;

  public AbstractProcessor(Validator<S> validator) {
    this.validator = validator;
  }

  public T process(S request) {
    validator.validate(request);

    PersistenceResult result = convertAndPersist(request);

    E model = result.getResult();

    if (result.isSuccess()) {
      createMessageAndSend(request, model);
    }

    return buildResponse(request, model);
  }

  protected PersistenceResult convertAndPersist(S request) {
    return new PersistenceResult(false, null);
  }

  protected void createMessageAndSend(S request, E model) {

  }

  protected abstract T buildResponse(S request, E model);

  protected Logger getLogger() {
    return Logger.getLogger(this.getClass());
  }

  class PersistenceResult {

    private boolean success;
    private E eventRecord;

    public PersistenceResult(boolean success, E eventRecord) {
      this.success = success;
      this.eventRecord = eventRecord;
    }

    public boolean isSuccess() {
      return success;
    }

    public E getResult() {
      return eventRecord;
    }
  }

}
