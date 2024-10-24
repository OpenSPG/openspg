package com.antgroup.openspg.server.api.http.server;

import java.io.Serializable;
import lombok.Data;

@Data
public class HttpResult<T> implements Serializable {

  private static final long serialVersionUID = 5374949831347324249L;

  private T result;
  private boolean success;
  private String errorCode;
  private String errorMsg;
  private String remote;
  private String traceId;

  public static <T> HttpResult<T> success(T data) {
    HttpResult<T> httpResult = new HttpResult<>();
    httpResult.setSuccess(true);
    httpResult.setResult(data);
    return httpResult;
  }

  public static <T> HttpResult<T> failed(String errorCode, String errorMsg) {
    HttpResult<T> httpResult = new HttpResult<>();
    httpResult.setSuccess(false);
    httpResult.setErrorCode(errorCode);
    httpResult.setErrorMsg(errorMsg);
    return httpResult;
  }
}
