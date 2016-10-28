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

package org.nebula.service.core;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class HostAddress {

  public static String getLocalHost() throws Exception {

      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface current = interfaces.nextElement();

        if (!current.isUp() || current.isLoopback() || current.isVirtual()) {
          continue;
        }
        Enumeration<InetAddress> addresses = current.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress current_addr = addresses.nextElement();
          if (current_addr.isLoopbackAddress() || !InetAddressUtils
              .isIPv4Address(current_addr.getHostAddress())) {
            continue;
          }
          return current_addr.getHostName();
        }
      }

    throw new Exception("Failed to get local hostname");
  }

}