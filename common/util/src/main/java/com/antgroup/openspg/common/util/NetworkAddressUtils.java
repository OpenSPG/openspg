/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;

/** Created by john.qf on 2017/6/1. */
@Slf4j
public class NetworkAddressUtils {

  public static final String LOCAL_IP = getLocalIp();

  private static String getLocalIp() {
    String localIp = null;
    try {
      Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress ip = null;
      boolean find = false;
      while (netInterfaces.hasMoreElements() && !find) {
        NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
        Enumeration address = ni.getInetAddresses();
        while (address.hasMoreElements()) {
          ip = (InetAddress) address.nextElement();
          if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
            localIp = ip.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
    return localIp;
  }
}
