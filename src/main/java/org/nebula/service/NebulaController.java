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
import org.nebula.framework.client.request.CancelTimerRequest;
import org.nebula.framework.client.request.CancelWorkflowRequest;
import org.nebula.framework.client.request.CompleteActivityRequest;
import org.nebula.framework.client.request.CompleteDecisionRequest;
import org.nebula.framework.client.request.CompleteWorkflowRequest;
import org.nebula.framework.client.request.GetEventsRequest;
import org.nebula.framework.client.request.HeartbeatRequest;
import org.nebula.framework.client.request.PollActivityRequest;
import org.nebula.framework.client.request.PollWorkflowRequest;
import org.nebula.framework.client.request.RegisterRequest;
import org.nebula.framework.client.request.ScheduleTimerRequest;
import org.nebula.framework.client.request.SignalWorkflowRequest;
import org.nebula.framework.client.request.StartActivityRequest;
import org.nebula.framework.client.request.StartWorkflowRequest;
import org.nebula.framework.client.response.CancelTimerResponse;
import org.nebula.framework.client.response.CancelWorkflowResponse;
import org.nebula.framework.client.response.CompleteActivityResponse;
import org.nebula.framework.client.response.CompleteDecisionResponse;
import org.nebula.framework.client.response.CompleteWorkflowResponse;
import org.nebula.framework.client.response.GetEventsResponse;
import org.nebula.framework.client.response.HeartbeatResponse;
import org.nebula.framework.client.response.PollActivityResponse;
import org.nebula.framework.client.response.PollWorkflowResponse;
import org.nebula.framework.client.response.RegisterResponse;
import org.nebula.framework.client.response.ScheduleTimerResponse;
import org.nebula.framework.client.response.SignalWorkflowResponse;
import org.nebula.framework.client.response.StartActivityResponse;
import org.nebula.framework.client.response.StartWorkflowResponse;
import org.nebula.service.logging.Loggable;
import org.nebula.service.processor.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.nebula.service.util.JsonUtils.toJson;

@Controller
public class NebulaController {

  private final static Logger logger = Logger.getLogger(NebulaController.class);

  @Autowired
  private Processor<StartWorkflowRequest, StartWorkflowResponse> startWorkflowProcessor;

  @Autowired
  private Processor<SignalWorkflowRequest, SignalWorkflowResponse> signalWorkflowProcessor;

  @Autowired
  private Processor<GetEventsRequest, GetEventsResponse> getEventsProcessor;

  @Autowired
  private Processor<StartActivityRequest, StartActivityResponse> startActivityProcessor;

  @Autowired
  private Processor<CompleteActivityRequest, CompleteActivityResponse> completeActivityProcessor;

  @Autowired
  private Processor<CompleteWorkflowRequest, CompleteWorkflowResponse> completeWorkflowProcessor;

  @Autowired
  private Processor<PollWorkflowRequest, PollWorkflowResponse> pollWorkflowProcessor;

  @Autowired
  private Processor<PollActivityRequest, PollActivityResponse> pollActivityProcessor;

  @Autowired
  private Processor<ScheduleTimerRequest, ScheduleTimerResponse> scheduleTimerProcessor;

  @Autowired
  private Processor<CancelTimerRequest, CancelTimerResponse> cancelTimerProcessor;

  @Autowired
  private Processor<RegisterRequest, RegisterResponse> registerProcessor;

  @Autowired
  private Processor<HeartbeatRequest, HeartbeatResponse> heartbeatProcessor;

  @Autowired
  private Processor<CompleteDecisionRequest, CompleteDecisionResponse> completeDecisionProcessor;

  @Autowired
  private Processor<CancelWorkflowRequest, CancelWorkflowResponse> cancelWorkflowProcessor;

  @RequestMapping(value = "/workflow/start", method = RequestMethod.POST)
  @ResponseBody
  @Loggable
  public StartWorkflowResponse start(@RequestBody
                                     StartWorkflowRequest startWorkflowRequest) {

    logger.info("StartWorkflow: " + toJson(startWorkflowRequest));

    StartWorkflowResponse response = startWorkflowProcessor
        .process(startWorkflowRequest);

    logger.info("StartWorkflow response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/workflow/signal", method = RequestMethod.POST)
  @ResponseBody
  public SignalWorkflowResponse signal(@RequestBody
                                       SignalWorkflowRequest signalWorkflowRequest) {

    logger.info("SignalWorkflow: " + toJson(signalWorkflowRequest));

    SignalWorkflowResponse response = signalWorkflowProcessor
        .process(signalWorkflowRequest);

    logger.info("SignalWorkflow response: " + toJson(response));
    return response;
  }

  @RequestMapping(value = "/workflow/complete", method = RequestMethod.POST)
  @ResponseBody
  public CompleteWorkflowResponse completeWorkflow(@RequestBody
                                                   CompleteWorkflowRequest completeWorkflowRequest) {

    logger.info("CompleteWorkflow:  " + toJson(completeWorkflowRequest));

    CompleteWorkflowResponse response = completeWorkflowProcessor
        .process(completeWorkflowRequest);

    logger.info("CompleteWorkflow response: " + toJson(response));

    return response;
  }

  @RequestMapping(value = "/workflow/poll", method = RequestMethod.GET)
  @ResponseBody
  public PollWorkflowResponse pollWorkflow(
      PollWorkflowRequest pollWorkflowRequest) throws Exception {

    logger.info("PollWorkflow :" + toJson(pollWorkflowRequest));

    PollWorkflowResponse result = pollWorkflowProcessor
        .process(pollWorkflowRequest);

    logger.info("PollWorkflow response:" + toJson(result));

    return result;
  }

  @RequestMapping(value = "/workflow/getEvents", method = RequestMethod.GET)
  @ResponseBody
  public GetEventsResponse getEvents(
      GetEventsRequest getEventsRequest) throws Exception {

    logger.info("GetEvents:" + toJson(getEventsRequest));

    GetEventsResponse response = getEventsProcessor
        .process(getEventsRequest);

    logger.info("GetEvents response :" + toJson(response));

    return response;
  }

  @RequestMapping(value = "/activity/start", method = RequestMethod.POST)
  @ResponseBody
  public StartActivityResponse start(@RequestBody
                                     StartActivityRequest startActivityRequest) throws Exception {

    logger.info("StartActivityRequest  :" + toJson(startActivityRequest));

    StartActivityResponse response = startActivityProcessor
        .process(startActivityRequest);

    logger.info("StartActivityRequest response :" + toJson(response));

    return response;
  }

  @RequestMapping(value = "/activity/poll", method = RequestMethod.GET)
  @ResponseBody
  public PollActivityResponse poll(
      PollActivityRequest pollActivityRequest) throws Exception {

    logger.info("PollActivityRequest :" + toJson(pollActivityRequest));

    PollActivityResponse result = pollActivityProcessor
        .process(pollActivityRequest);

    logger.info("PollActivityRequest response :" + toJson(result));

    return result;
  }

  @RequestMapping(value = "/activity/complete", method = RequestMethod.POST)
  @ResponseBody
  public CompleteActivityResponse complete(@RequestBody
                                           CompleteActivityRequest request) throws Exception {

    logger.info("CompleteActivity :" + toJson(request));

    CompleteActivityResponse response = completeActivityProcessor
        .process(request);

    logger.info("CompleteActivity response:" + toJson(response));
    return response;
  }

  @RequestMapping(value = "/timer/schedule", method = RequestMethod.POST)
  @ResponseBody
  public ScheduleTimerResponse schedule(@RequestBody
                                        ScheduleTimerRequest request) throws Exception {

    logger.info("ScheduleTimer :" + toJson(request));

    ScheduleTimerResponse response = scheduleTimerProcessor
        .process(request);

    logger.info("ScheduleTimer response:" + toJson(response));
    return response;
  }

  @RequestMapping(value = "/timer/cancel", method = RequestMethod.POST)
  @ResponseBody
  public CancelTimerResponse cancel(@RequestBody
                                    CancelTimerRequest request) throws Exception {

    logger.info("CancelTimer :" + toJson(request));

    CancelTimerResponse response = cancelTimerProcessor.process(request);

    logger.info("CancelTimer response:" + toJson(response));
    return response;
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  @ResponseBody
  public RegisterResponse register(@RequestBody
                                   RegisterRequest request) throws Exception {

    logger.info("Register :" + toJson(request));

    RegisterResponse response = registerProcessor.process(request);

    logger.info("Register response:" + toJson(response));
    return response;
  }

  @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
  @ResponseBody
  public HeartbeatResponse heartbeat(@RequestBody
                                     HeartbeatRequest request) throws Exception {

    logger.info("Heartbeat :" + toJson(request));

    HeartbeatResponse response = heartbeatProcessor.process(request);

    logger.info("Heartbeat response:" + toJson(response));
    return response;
  }

  @RequestMapping(value = "/decision/complete", method = RequestMethod.POST)
  @ResponseBody
  public CompleteDecisionResponse completeDecision(@RequestBody
                                                   CompleteDecisionRequest request)
      throws Exception {

    logger.info("CompleteDecision :" + toJson(request));

    CompleteDecisionResponse response = completeDecisionProcessor.process(request);

    logger.info("CompleteDecision response:" + toJson(response));
    return response;
  }

  @RequestMapping(value = "/workflow/cancel", method = RequestMethod.POST)
  @ResponseBody
  public CancelWorkflowResponse cancelWorkflow(@RequestBody
                                               CancelWorkflowRequest request) throws Exception {

    logger.info("CancelWorkflow :" + toJson(request));

    CancelWorkflowResponse response = cancelWorkflowProcessor.process(request);

    logger.info("CancelWorkflow response:" + toJson(response));
    return response;
  }
}