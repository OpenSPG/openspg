package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import java.util.HashMap;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/SubmitTaskRequest.class */
public class SubmitTaskRequest extends BaseRequest {
  private static final long serialVersionUID = 6870684691952780677L;
  private String dsl;
  private String nl;
  private Long sessionId;
  private Long userId;
  private Map<String, String> params;

  public String getDsl() {
    return this.dsl;
  }

  public void setDsl(String dsl) {
    this.dsl = dsl;
  }

  public String getNl() {
    return this.nl;
  }

  public void setNl(String nl) {
    this.nl = nl;
  }

  public Long getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public Long getUserId() {
    return this.userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Map<String, String> getParams() {
    if (null == this.params) {
      return new HashMap();
    }
    return this.params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }
}
