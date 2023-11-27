/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.Date;

public class JobInstDO {
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private Long jobId;

  private String type;

  private String status;

  private String host;

  private String traceId;

  private String idempotentId;

  private String result;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public Long getJobId() {
    return jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type == null ? null : type.trim();
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status == null ? null : status.trim();
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host == null ? null : host.trim();
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId == null ? null : traceId.trim();
  }

  public String getIdempotentId() {
    return idempotentId;
  }

  public void setIdempotentId(String idempotentId) {
    this.idempotentId = idempotentId == null ? null : idempotentId.trim();
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result == null ? null : result.trim();
  }
}
