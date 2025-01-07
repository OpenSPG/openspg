package com.antgroup.openspg.server.common.model.project;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import lombok.Data;

/** @version UserRoleInfo.java, v 0.1 2024年11月26日 上午11:41 */
@Data
public class AccountRoleInfo extends BaseModel {

  /** Specific Role Name */
  String roleName;

  /** Permission Detail Information */
  String permissionDetail;

  public AccountRoleInfo() {}

  public AccountRoleInfo(String roleName) {
    this.roleName = roleName;
  }
}
