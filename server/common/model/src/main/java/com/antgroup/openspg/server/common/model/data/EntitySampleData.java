package com.antgroup.openspg.server.common.model.data;

import java.util.Map;
import lombok.Data;

/** sample data of entity */
@Data
public class EntitySampleData {

  /** entity data id */
  private String id;

  /** entity type name */
  private String label;

  /** entity properties */
  private Map<String, Object> properties;
}
