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

package org.nebula.service.auth;


import org.apache.log4j.Logger;
import org.nebula.framework.client.Request;
import org.nebula.service.core.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

  private final static Logger logger = Logger
      .getLogger(AuthenticationInterceptor.class);

  private List<String> adminUrls;

  @Autowired
  private UserCredentialsPool userCredentialsPool;

  @Autowired
  private ServiceContext serviceContext;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    logger.info("AuthenticationInterceptor request");

    checkServiceReady();

    AuthenticationHelper
        helper =
        new AuthenticationHelper(userCredentialsPool,
                                 request.getHeader(Request.AUTHORIZATION_HEADER));

    helper.authenticate();

    checkIfAccessAdminUrls(helper, request);

    return true;
  }

  private void checkIfAccessAdminUrls(AuthenticationHelper helper, HttpServletRequest request)
      throws GeneralSecurityException {

    if (!helper.isAdmin()) {

      String contextPath = request.getContextPath();
      String requestUri = request.getRequestURI();

      for (String url : adminUrls) {
        if (requestUri.startsWith(contextPath + url)) {
          throw new GeneralSecurityException(
              "The user is not allowed to access the url " + (contextPath + url));
        }
      }
    }
  }


  private void checkServiceReady() {
    if (!serviceContext.isReady()) {
      throw new IllegalStateException("The Nebula Service is Not Ready.");
    }
  }

  public void setAdminUrls(List<String> adminUrls) {
    this.adminUrls = adminUrls;
  }

}

