package com.antgroup.openspg.server.common.model.data;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Map;
import lombok.Data;

@Data
public class DataRecord extends BaseModel {

  private String name;

  private String docId;

  private double score;

  private String label;

  private Map<String, Object> fields;
}
