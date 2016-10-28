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

package org.nebula.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.nebula.service.dao.entity.Registration;

import java.util.Date;
import java.util.List;

public interface RegistrationMapper {

  public void insertRegistration(Registration registration);

  public void update(Registration registration);

  public Registration find(Registration registration);

  public Registration findEnabled(Registration registration);

  public Registration findById(String id);

  public List<Registration> findRegistrations(RowBounds rowBounds, @Param("user")
  String user, @Param("workflowName")
                                              String workflowName, @Param("nodeType")
                                              String nodeType,
                                              @Param("createdBefore") Date createdBefore,
                                              @Param("createdAfter") Date createdAfter);

  int countRegistrations(@Param("user")
                         String user, @Param("workflowName")
                         String workflowName, @Param("nodeType")
                         String nodeType, @Param("createdBefore") Date createdBefore,
                         @Param("createdAfter") Date createdAfter);

  public void enable(@Param("registrationId")
                     String registrationId, @Param("enabled")
                     boolean enabled);

  public Registration findLatestRegistration(Registration registration);

  public List<Registration> findRegistrationsWithRealm(RowBounds rowBounds,
                                                       @Param("realm") String realm,
                                                       @Param("nodeType") String nodeType);
}
