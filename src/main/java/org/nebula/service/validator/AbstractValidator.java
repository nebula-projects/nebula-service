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

package org.nebula.service.validator;

import org.apache.commons.lang.Validate;
import org.nebula.framework.client.Request;
import org.nebula.service.auth.UserCredentialsPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractValidator<S extends Request> implements Validator<S> {

  @Autowired
  private UserCredentialsPool userCredentialsPool;

  public void validate(S request) {

    verify(request);

    String accessId = request.getAccessId();

    verifyAccessId(accessId);

    if(! userCredentialsPool.isAdmin(accessId)){
      verifyAuthorization(request);
    }

  }

  private void verifyAccessId(String accessId) {
    Validate.notEmpty(accessId, "The access id should not be empty.");
  }

  protected abstract void verify(S request);

  protected void verifyAuthorization(S request){
    //not verify the authorization by default
  }
}