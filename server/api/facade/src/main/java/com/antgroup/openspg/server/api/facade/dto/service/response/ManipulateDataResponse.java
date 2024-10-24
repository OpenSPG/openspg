/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.response;

import lombok.Data;

@Data
public class ManipulateDataResponse {
  private Boolean success;
  private String errMessage;

  private ManipulateDataResponse(Boolean success, String errMessage) {
    this.success = success;
    this.errMessage = errMessage;
  }

  public static ManipulateDataResponse ofSuccess() {
    return new ManipulateDataResponse(true, null);
  }

  public static ManipulateDataResponse ofFailure(String errMessage) {
    return new ManipulateDataResponse(false, errMessage);
  }
}
