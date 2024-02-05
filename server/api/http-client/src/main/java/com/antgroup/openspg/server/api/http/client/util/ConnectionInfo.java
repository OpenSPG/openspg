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

package com.antgroup.openspg.server.api.http.client.util;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class ConnectionInfo implements Serializable {

  private final String scheme;
  private final String host;
  private final String port;

  private Integer connectTimeout;
  private Integer readTimeout;

  public ConnectionInfo(String uriStr) {
    try {
      URI uri = new URI(uriStr);
      scheme = uri.getScheme();
      host = uri.getHost();
      port = String.valueOf(uri.getPort());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public String getScheme() {
    return scheme;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public Integer getConnectTimeout() {
    return connectTimeout;
  }

  public ConnectionInfo setConnectTimeout(Integer connectTimeout) {
    this.connectTimeout = connectTimeout;
    return this;
  }

  public Integer getReadTimeout() {
    return readTimeout;
  }

  public ConnectionInfo setReadTimeout(Integer readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }
}
