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
import org.nebula.framework.client.request.StartActivityRequest;
import org.nebula.framework.client.request.StartWorkflowRequest;
import org.nebula.service.util.IdUtils;
import org.springframework.stereotype.Component;

@Component
public class StartWorkflowRequestValidator extends
                                           AbstractValidator<StartWorkflowRequest> {

  @Override
  protected void verify(StartWorkflowRequest request) {
    Validate.notNull(request,
                     "The startWorkflow request should not be null.");

    Validate.notNull(request.getRealms(), "The realms should not be null.");

    Validate.notNull(request.getWorkflowProfile(),
                     "The workflow version should not be null.");

    Validate.notEmpty(request.getWorkflowProfile().getName(),
                      "The workflow version name should not be empty.");

    Validate.notEmpty(request.getWorkflowProfile().getVersion(),
                      "The workflow version should not be empty.");

    Validate.notNull(request.getStartMode(),
                     "The start mode should not be null.");
  }

}
