package com.antgroup.openspg.server.api.facade.dto.common.request;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class DataQueryRequest extends BaseRequest {
  private Long sessionId;
  private Long projectId;
  private String document;
  private String instruction;
  private String type;
  private Map<String, String> params = new HashMap<>();
}
