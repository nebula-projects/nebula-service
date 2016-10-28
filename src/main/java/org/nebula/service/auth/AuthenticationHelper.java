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

import org.apache.commons.lang.StringUtils;
import org.nebula.framework.client.Request;
import org.nebula.framework.core.Authorization;

import java.security.GeneralSecurityException;

public class AuthenticationHelper {

  private UserCredentialsPool userCredentialsPool;
  private String signedSignature;

  private Authorization authorization;
  private UserCredentials userCredentials;

  public AuthenticationHelper(UserCredentialsPool userCredentialsPool, String signedSignature) {
    this.userCredentialsPool = userCredentialsPool;
    this.signedSignature = signedSignature;
  }

  public boolean authenticate() throws GeneralSecurityException{

    createAuthorization();

    findUserCredentials();

    return authorization.authenticate(userCredentials.getSecretKey());

  }

  public boolean isAdmin(){
    return userCredentials.isAdmin();
  }

  private void findUserCredentials() throws GeneralSecurityException{
    String accessId = authorization.getAccessId();

    userCredentials = userCredentialsPool.getUserCredentials(accessId);

    if(userCredentials == null) {
      throw new GeneralSecurityException("The accessId " + accessId + " not found");
    }
  }

  private void createAuthorization() throws GeneralSecurityException {

    if(StringUtils.isBlank(signedSignature)) {
      throw new GeneralSecurityException("The header " + Request.AUTHORIZATION_HEADER + " is illegal");
    }

    authorization = Authorization.build(signedSignature);

  }

}