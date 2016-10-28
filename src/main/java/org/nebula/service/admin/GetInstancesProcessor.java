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

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.nebula.admin.client.model.nebula.WorkflowInstance;
import org.nebula.admin.client.request.mgmt.GetInstancesRequest;
import org.nebula.admin.client.response.mgmt.GetInstancesResponse;
import org.nebula.framework.client.request.RegisterRequest;
import org.nebula.service.admin.validator.GetInstancesRequestValidator;
import org.nebula.service.dao.entity.Event;
import org.nebula.service.dao.entity.Registration;
import org.nebula.service.dao.mapper.CommonEventMapper;
import org.nebula.service.dao.mapper.EventMapper;
import org.nebula.service.dao.mapper.HistoryEventMapper;
import org.nebula.service.dao.mapper.RegistrationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetInstancesProcessor
    extends AdminProcessor<GetInstancesRequest, GetInstancesResponse> {

  private static Logger logger = LoggerFactory.getLogger(GetInstancesProcessor.class);

  @Autowired
  private RegistrationMapper registrationMapper;

  @Autowired
  private EventMapper eventMapper;

  @Autowired
  private HistoryEventMapper historyEventMapper;

  @Value("${max.workflows.per.user}")
  private int maxWorkflowsPerUser;

  @Autowired
  public GetInstancesProcessor(GetInstancesRequestValidator validator) {
    super(validator);
  }

  @Override
  protected GetInstancesResponse processInternal(GetInstancesRequest request) {
    int totalInstances = 0;
    List<WorkflowInstance> instances = new ArrayList<WorkflowInstance>();

    CommonEventMapper
        cem =
        request.getSearchMode() == GetInstancesRequest.SearchMode.HISTORY ? historyEventMapper
                                                                          : eventMapper;

    if (StringUtils.isNotBlank(request.getInstanceId())) {
      instances = getInstancesByInstanceId(cem, request);
      totalInstances = instances.size();
    } else if (StringUtils.isNotBlank(request.getUsername()) && StringUtils
        .isBlank(request.getRegistrationId())) {
      totalInstances = countInstancesByUsername(cem, request.getUsername());
    } else {
      //registrationId may be null, and all the instances return.
      instances = getInstancesByRegistrationId(cem, request);
      totalInstances = countInstancesByRegistrationId(cem, request.getRegistrationId());
    }

    // build response
    GetInstancesResponse response = new GetInstancesResponse();
    response.setInstances(instances);
    response.setTotal(totalInstances);
    response.setPageNo(request.getPageNo());
    response.setPageSize(request.getPageSize());

    return response;
  }

  private int countInstancesByRegistrationId(CommonEventMapper cem, String registrationId) {
    return cem.countInstancesByRegistrationId(registrationId);
  }

  private List<WorkflowInstance> getInstancesByInstanceId(CommonEventMapper cem,
                                                          GetInstancesRequest request) {
    List<WorkflowInstance> instances = new ArrayList<WorkflowInstance>();
    Event scheduledEvent = cem.findWorkflowScheduledEvent(request.getInstanceId());

    if (scheduledEvent == null) {
      return instances;
    }

    Registration registration = registrationMapper.findById(scheduledEvent.getRegistrationId());

    Event completedEvent = cem.findWorkflowCompletedEvent(request.getInstanceId());
    instances.add(createWorkflowInstance(registration, scheduledEvent, completedEvent));

    return instances;
  }

  private List<WorkflowInstance> getInstancesByRegistrationId(CommonEventMapper cem,
                                                              GetInstancesRequest request) {
    List<WorkflowInstance> instances = new ArrayList<WorkflowInstance>();

    RowBounds
        rowBounds =
        new RowBounds((request.getPageNo() - 1) * request.getPageSize(), request.getPageSize());
    List<Event>
        workflowScheduledEvents =
        cem.findInstancesByRegistrationIdByPage(rowBounds, request.getRegistrationId());

    for (Event scheduledEvent : workflowScheduledEvents) {

      String registrationId = request.getRegistrationId();
      if (registrationId == null) {
        registrationId = scheduledEvent.getRegistrationId();
      }

      Registration registration = registrationMapper.findById(registrationId);

      if (registration == null) {
        String error = String.format("No workflow registration found for registrationId - %s",
                                     request.getRegistrationId());
        logger.warn(error);
        return instances;
      }

      Event completedEvent = cem.findWorkflowCompletedEvent(scheduledEvent.getInstanceId());
      instances.add(createWorkflowInstance(registration, scheduledEvent, completedEvent));
    }

    return instances;
  }

  private int countInstancesByUsername(CommonEventMapper cem, String username) {

    RowBounds rowBounds = new RowBounds(0, maxWorkflowsPerUser);

    List<Registration> registrations = registrationMapper.findRegistrations(rowBounds,
                                                                            username, null,
                                                                            RegisterRequest.NodeType.WORKFLOW
                                                                                .name(), null,
                                                                            null);

    logger.info("registrations.size=" + registrations.size());

    int totalInstances = 0;
    for (Registration registration : registrations) {
      logger.info("registrations.getId=" + registration.getId());
      totalInstances += countInstancesByRegistrationId(cem, registration.getId());
    }

    return totalInstances;
  }

  private WorkflowInstance createWorkflowInstance(Registration registration, Event scheduledEvent,
                                                  Event completedEvent) {
    WorkflowInstance instance = new WorkflowInstance();
    instance.setWorkflowName(registration.getName());
    instance.setVersion(registration.getVersion());
    instance.setInstanceId(scheduledEvent.getInstanceId());
    instance.setStartedTime(scheduledEvent.getCreatedDate());
    if (completedEvent != null) {
      instance.setCompletedTime(completedEvent.getCreatedDate());
    }

    return instance;
  }
}
