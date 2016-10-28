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
import org.nebula.service.dao.entity.EventRecord;

import java.util.Date;
import java.util.List;

public interface EventRecordMapper extends CommonEventMapper {

  public int countWorkflowByRidAndType(@Param("registrationId")
                                       List<String> registrationIds, @Param("eventType")
                                       String eventType);

  public EventRecord findByInstanceIdAndTypeAndPrecedingId(@Param("instanceId")
                                                           String instanceId, @Param("eventType")
                                                           String eventType, @Param("precedingId")
                                                           int precedingId);

  public void deleteInstance(String instanceId);

  public int countInstancesByRegistrationIdAndBacklogDate(@Param("registrationIds")
                                                          List<String> registrationIds,
                                                          @Param("backlogPeriodDate")
                                                          Date backlogPeriodDate);
}
