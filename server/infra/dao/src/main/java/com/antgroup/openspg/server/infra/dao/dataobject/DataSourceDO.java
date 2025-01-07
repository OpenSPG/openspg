package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceDO {

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String createUser;
  private String updateUser;
  private String status;
  private String remark;
  private String type;
  private String dbName;
  private String dbUrl;
  private String dbUser;
  private String dbPassword;
  private String encrypt;
  private String dbDriverName;
  private String category;
  private String connectionInfo;
}
