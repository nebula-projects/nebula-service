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

package org.nebula.service.admin.validator;

import org.apache.commons.lang.Validate;
import org.nebula.admin.client.request.mgmt.GetInstancesRequest;
import org.nebula.service.validator.AbstractValidator;
import org.nebula.service.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class GetInstancesRequestValidator extends
                                          AbstractValidator<GetInstancesRequest> {

  @Override
  protected void verify(GetInstancesRequest request) {

    Validate.notNull(request, "The GetInstancesRequest should not be null.");
    Validate.notNull(request.getSearchMode(), "The searchMode should not be null.");
    Validate.isTrue(request.getPageNo() >= 0, "PageNo should be positive integer or zero.");
    Validate.isTrue(request.getPageSize() > 0, "PageSize should be positive integer.");
  }

}
