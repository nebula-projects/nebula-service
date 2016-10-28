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
import org.nebula.framework.client.request.RegisterRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterRequestValidator extends
                                      AbstractValidator<RegisterRequest> {

  private final static int MAX_LENGTH_OF_REALM = 40;
  private final static int MAX_REALMS = 10;

  private final static int MAX_LENGTH_OF_USERNAME = 10;

  @Override
  protected void verify(RegisterRequest request) {

    Validate.notNull(request, "The register request should not be null.");

    Validate.notEmpty(request.getName(), "The name should not be empty.");

    Validate.notEmpty(request.getUser(), "The user should not be empty.");

    Validate.isTrue(request.getUser().length() <= MAX_LENGTH_OF_USERNAME,
                    "The length of username should not exceed 20.");

    Validate.notEmpty(request.getVersion(),
                      "The version  should not be empty.");

    Validate.notNull(request.getRegistrationInfo(),
                     "The registration info should not be null.");

    Validate.notEmpty(request.getRegistrationInfo().getMethodProfiles(),
                      "At least one method should exist.");

    List<String> realms = request.getRegistrationInfo().getRealms();

    Validate.notEmpty(realms,
                      "At least one realm should exist.");

    Validate.isTrue(realms.size() <= MAX_REALMS,
                    "The size of realms should not exceed " + MAX_REALMS + ".");

    for (String realm : realms) {
      Validate.notEmpty(realm, "The realm should not be empty.");
      Validate.isTrue(realm.length() <= MAX_LENGTH_OF_REALM,
                      "The length of realm should not exceed " + MAX_LENGTH_OF_REALM + ".");
    }

  }

}
