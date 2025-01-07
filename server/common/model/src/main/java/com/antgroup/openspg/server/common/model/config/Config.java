package com.antgroup.openspg.server.common.model.config;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Data;

@Data
public class Config extends BaseModel {
  /** primary key */
  private Long id;

  /** create time */
  private Date gmtCreate;

  /** update time */
  private Date gmtModified;

  /** creator userNo */
  private String userNo;

  /** Project ID, which can be a unique value for a certain domain. */
  private String projectId;

  /** config name */
  private String configName;

  /** config id */
  private String configId;

  /** config version */
  private String version;

  /** Status, 1: Offline status (default) 2: Online */
  private Integer status;

  /** config json */
  private String config;

  /** version description */
  private String description;

  /** Resource ID, used for foreign key association with the schem view. */
  private String resourceId;

  /** resource type */
  private String resourceType;

  private boolean showProfilePicture = false;

  private boolean showUserConfig = false;

  public Config() {}

  public Config(Long id, String configName, String configId, String config, String resourceType) {
    this.id = id;
    this.configName = configName;
    this.configId = configId;
    this.config = config;
    this.resourceType = resourceType;
  }
}
