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

package org.nebula.service;

import org.apache.log4j.Logger;
import org.nebula.admin.client.request.mgmt.EnableRegistrationRequest;
import org.nebula.admin.client.request.mgmt.GetHeartbeatsRequest;
import org.nebula.admin.client.request.mgmt.GetHistoryEventsRequest;
import org.nebula.admin.client.request.mgmt.GetInstancesRequest;
import org.nebula.admin.client.request.mgmt.GetLengthOfQueuesRequest;
import org.nebula.admin.client.request.mgmt.GetRegistrationRequest;
import org.nebula.admin.client.request.mgmt.GetRegistrationsRequest;
import org.nebula.admin.client.response.mgmt.EnableRegistrationResponse;
import org.nebula.admin.client.response.mgmt.GetHeartbeatsResponse;
import org.nebula.admin.client.response.mgmt.GetHistoryEventsResponse;
import org.nebula.admin.client.response.mgmt.GetInstancesResponse;
import org.nebula.admin.client.response.mgmt.GetLengthOfQueuesResponse;
import org.nebula.admin.client.response.mgmt.GetRegistrationResponse;
import org.nebula.admin.client.response.mgmt.GetRegistrationsResponse;
import org.nebula.framework.client.request.GetEventsRequest;
import org.nebula.framework.client.response.GetEventsResponse;
import org.nebula.service.processor.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.nebula.service.util.JsonUtils.toJson;

@Controller
public class AdminController {

  private final static Logger logger = Logger
      .getLogger(AdminController.class);

  @Autowired
  Processor<GetRegistrationsRequest, GetRegistrationsResponse> getRegistrationsProcessor;

  @Autowired
  Processor<GetRegistrationRequest, GetRegistrationResponse> getRegistrationProcessor;

  @Autowired
  Processor<EnableRegistrationRequest, EnableRegistrationResponse> enableRegistrationProcessor;

  @Autowired
  Processor<GetHeartbeatsRequest, GetHeartbeatsResponse> getHeartbeatsProcessor;

  @Autowired
  Processor<GetEventsRequest, GetEventsResponse> getEventsProcessor;

  @Autowired
  Processor<GetHistoryEventsRequest, GetHistoryEventsResponse> getHistoryEventsProcessor;

  @Autowired
  Processor<GetInstancesRequest, GetInstancesResponse> getInstancesProcessor;

  @Autowired
  Processor<GetLengthOfQueuesRequest, GetLengthOfQueuesResponse> getLengthOfQueuesProcessor;


  @RequestMapping(value = "/admin/getRegistrations", method = RequestMethod.GET)
  @ResponseBody
  public GetRegistrationsResponse getRegistrations(
      GetRegistrationsRequest request) {

    logger.info("GetRegistrations: " + toJson(request));

    GetRegistrationsResponse response = getRegistrationsProcessor
        .process(request);

    logger.info("GetRegistrations response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/admin/getRegistration", method = RequestMethod.GET)
  @ResponseBody
  public GetRegistrationResponse getRegistration(
      GetRegistrationRequest request) {

    logger.info("GetRegistration: " + toJson(request));

    GetRegistrationResponse response = getRegistrationProcessor
        .process(request);

    logger.info("GetRegistration response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/admin/enableRegistration", method = RequestMethod.POST)
  @ResponseBody
  public EnableRegistrationResponse enableRegistration(@RequestBody
                                                       EnableRegistrationRequest request) {

    logger.info("EnableRegistration: " + toJson(request));

    EnableRegistrationResponse response = enableRegistrationProcessor
        .process(request);

    logger.info("EnableRegistration response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/admin/getHeartbeats", method = RequestMethod.GET)
  @ResponseBody
  public GetHeartbeatsResponse getHeartbeats(
      GetHeartbeatsRequest request) {

    logger.info("GetHeartbeats: " + toJson(request));

    GetHeartbeatsResponse response = getHeartbeatsProcessor
        .process(request);

    logger.info("GetHeartbeats response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/admin/getEvents", method = RequestMethod.GET)
  @ResponseBody
  public GetEventsResponse getEvents(
      GetEventsRequest request) {

    logger.info("getEvents: " + toJson(request));

    GetEventsResponse response = getEventsProcessor
        .process(request);

    logger.info("getEvents response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/admin/getHistoryEvents", method = RequestMethod.GET)
  @ResponseBody
  public GetHistoryEventsResponse getHistoryEvents(
      GetHistoryEventsRequest request) {

    logger.info("getHistoryEvents: " + toJson(request));

    GetHistoryEventsResponse response = getHistoryEventsProcessor
        .process(request);

    logger.info("getHistoryEvents response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/admin/getInstances", method = RequestMethod.GET)
  @ResponseBody
  public GetInstancesResponse getInstances(
      GetInstancesRequest request) {

    logger.info("getInstances: " + toJson(request));

    GetInstancesResponse response = getInstancesProcessor
        .process(request);

    logger.info("getInstances response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/admin/getLengthOfQueues", method = RequestMethod.GET)
  @ResponseBody
  public GetLengthOfQueuesResponse getLengthOfQueues(GetLengthOfQueuesRequest request) {
    logger.info("getLengthOfQueues: " + toJson(request));
    GetLengthOfQueuesResponse response = getLengthOfQueuesProcessor.process(request);
    logger.info("getLengthOfQueues response: " + toJson(response));
    return response;
  }

}
