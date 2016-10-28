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

package org.nebula.service.admin;

import org.nebula.service.core.ServiceContext;
import org.nebula.service.processor.Processor;
import org.nebula.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AdminProcessor<S, T> implements Processor<S, T> {

  private Validator<S> validator;

  @Autowired
  private ServiceContext serviceContext;

  public AdminProcessor(Validator<S> validator) {
    this.validator = validator;
  }

  public T process(S request) {

    checkReady();

    validator.validate(request);

    return processInternal(request);
  }

  protected abstract T processInternal(S request);

  private void checkReady() {
    if(!serviceContext.isReady()) {
      throw new IllegalStateException("The Nebula Service is Not Ready.");
    }
  }
}
