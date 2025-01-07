package com.antgroup.openspg.server.common.model.account;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class Account extends BaseModel {

  private Long id;
  private String workNo;
  private String token;
  private String salt;
  private String realName;
  private String nickName;
  private String account;
  private String password;
  private String confirmPassword;
  private String email;
  private Date gmtCreate;
  private Date gmtModified;
  private String config;
  private String useCurrentLanguage;
  private List<String> roleNames;

  public Account() {}

  public Account(
      Long id,
      String workNo,
      String realName,
      String nickName,
      String account,
      String email,
      Date gmtCreate,
      Date gmtModified,
      String config,
      String useCurrentLanguage) {
    this.id = id;
    this.workNo = workNo;
    this.realName = realName;
    this.nickName = nickName;
    this.account = account;
    this.email = email;
    this.gmtCreate = gmtCreate;
    this.gmtModified = gmtModified;
    this.config = config;
    this.useCurrentLanguage = useCurrentLanguage;
  }

  public Account(
      Long id,
      String workNo,
      String realName,
      String nickName,
      String account,
      String email,
      String salt,
      String config,
      String useCurrentLanguage) {
    this.id = id;
    this.workNo = workNo;
    this.realName = realName;
    this.nickName = nickName;
    this.account = account;
    this.email = email;
    this.salt = salt;
    this.config = config;
    this.useCurrentLanguage = useCurrentLanguage;
  }
}
