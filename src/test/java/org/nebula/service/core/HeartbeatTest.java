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

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;

import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class HeartbeatTest {



  @Test
  public void testStartSuccess() throws Exception{

     String s = "test123abceewew";

    String hexString = hexadecimal(s,"UTF-8");//Hex.encodeHexString(s.getBytes("UTF-8"));
    System.out.println(hexString);
  }

  public static String hexadecimal(String input, String charsetName) throws
                                                                     UnsupportedEncodingException {
    if (input == null) throw new NullPointerException();
    return asHex(input.getBytes(charsetName));
  }

  private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

  public static String asHex(byte[] buf)
  {
    char[] chars = new char[2 * buf.length];
    for (int i = 0; i < buf.length; ++i)
    {
      chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
      chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
    }
    return new String(chars);
  }

}