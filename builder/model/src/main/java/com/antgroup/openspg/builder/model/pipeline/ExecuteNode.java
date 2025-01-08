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

package com.antgroup.openspg.builder.model.pipeline;

import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ExecuteNode extends BaseValObj {

  /** The id of the node. */
  private String id;

  /** The name of the node. */
  private String name;

  private String type;

  private StatusEnum status;

  private int index;

  private Object outputs;

  private StringBuffer traceLog;

  public ExecuteNode() {}

  public ExecuteNode(Node node) {
    this.id = node.getId();
    this.name = node.getName();
    this.type = node.getType().name();
    this.status = StatusEnum.WAITING;
    this.traceLog = new StringBuffer();
  }

  public synchronized void addTraceLog(String message, Object... args) {
    message = String.format(message, args);
    String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    if (traceLog == null) {
      log.info("traceLog: " + message);
    } else {
      traceLog.append(currentTime + ": " + message + System.getProperty("line.separator"));
    }
  }
}
