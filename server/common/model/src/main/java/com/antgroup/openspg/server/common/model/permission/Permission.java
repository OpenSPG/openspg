package com.antgroup.openspg.server.common.model.permission;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.project.AccountRoleInfo;
import lombok.Data;

@Data
public class Permission extends BaseModel {

  private Long id;
  private String userNo;
  private Long resourceId;
  private String resourceTag;
  private Long roleId;
  private String roleType;
  private String userName;
  private AccountRoleInfo accountRoleInfo;

  public Permission() {}

  public Permission(Long id) {
    this.id = id;
  }

  public Permission(Long id, String userNo, Long resourceId, String resourceTag, Long roleId) {
    this.id = id;
    this.userNo = userNo;
    this.resourceId = resourceId;
    this.resourceTag = resourceTag;
    this.roleId = roleId;
  }
}
