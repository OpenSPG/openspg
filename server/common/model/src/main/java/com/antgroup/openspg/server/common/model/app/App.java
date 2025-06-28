package com.antgroup.openspg.server.common.model.app;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Data;

@Data
public class App extends BaseModel {
  private Long id;
  private String name;
  private String logo;
  private String description;
  private JSONObject config;
  private String userNo;
  private String accessToken;
  // account token status
  private Integer status;
  private Date appDeployTime;
  private String alias;

  public App() {}

  public App(
      Long id,
      String name,
      String logo,
      String description,
      JSONObject config,
      String userNo,
      String alias) {
    this.id = id;
    this.name = name;
    this.logo = logo;
    this.description = description;
    this.config = config;
    this.userNo = userNo;
    this.alias = alias;
  }
}
