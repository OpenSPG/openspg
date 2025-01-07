package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuilderJobDO {

  private Long id;
  private Long projectId;
  private Date gmtCreate;
  private Date gmtModified;
  private String modifyUser;
  private String createUser;
  private Long taskId;
  private String jobName;
  private Long chunkNum;
  private String fileUrl;
  private String status;
  private String type;
  private String extension;
  private String version;
  private String computingConf;
  private String lifeCycle;
  private String action;
}
