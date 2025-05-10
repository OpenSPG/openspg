package com.antgroup.openspgapp.biz.builder.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import java.io.Serializable;
import java.util.Date;

/* loaded from: biz-builder-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/builder/dto/BuilderJobDTO.class */
public class BuilderJobDTO extends BaseDTO implements Serializable {
  private static final long serialVersionUID = 5461073901819219434L;
  private Long id;
  private Long projectId;
  private Date gmtCreate;
  private Date gmtModified;
  private String modifyUser;
  private String createUser;
  private Long taskId;
  private String name;
  private Long chunkNum;
  private String fileUrl;
  private String status;
  private String type;
  private String extension;

  public void setId(final Long id) {
    this.id = id;
  }

  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }

  public void setGmtCreate(final Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public void setGmtModified(final Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public void setModifyUser(final String modifyUser) {
    this.modifyUser = modifyUser;
  }

  public void setCreateUser(final String createUser) {
    this.createUser = createUser;
  }

  public void setTaskId(final Long taskId) {
    this.taskId = taskId;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setChunkNum(final Long chunkNum) {
    this.chunkNum = chunkNum;
  }

  public void setFileUrl(final String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public void setExtension(final String extension) {
    this.extension = extension;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BuilderJobDTO)) {
      return false;
    }
    BuilderJobDTO other = (BuilderJobDTO) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$id = getId();
    Object other$id = other.getId();
    if (this$id == null) {
      if (other$id != null) {
        return false;
      }
    } else if (!this$id.equals(other$id)) {
      return false;
    }
    Object this$projectId = getProjectId();
    Object other$projectId = other.getProjectId();
    if (this$projectId == null) {
      if (other$projectId != null) {
        return false;
      }
    } else if (!this$projectId.equals(other$projectId)) {
      return false;
    }
    Object this$taskId = getTaskId();
    Object other$taskId = other.getTaskId();
    if (this$taskId == null) {
      if (other$taskId != null) {
        return false;
      }
    } else if (!this$taskId.equals(other$taskId)) {
      return false;
    }
    Object this$chunkNum = getChunkNum();
    Object other$chunkNum = other.getChunkNum();
    if (this$chunkNum == null) {
      if (other$chunkNum != null) {
        return false;
      }
    } else if (!this$chunkNum.equals(other$chunkNum)) {
      return false;
    }
    Object this$gmtCreate = getGmtCreate();
    Object other$gmtCreate = other.getGmtCreate();
    if (this$gmtCreate == null) {
      if (other$gmtCreate != null) {
        return false;
      }
    } else if (!this$gmtCreate.equals(other$gmtCreate)) {
      return false;
    }
    Object this$gmtModified = getGmtModified();
    Object other$gmtModified = other.getGmtModified();
    if (this$gmtModified == null) {
      if (other$gmtModified != null) {
        return false;
      }
    } else if (!this$gmtModified.equals(other$gmtModified)) {
      return false;
    }
    Object this$modifyUser = getModifyUser();
    Object other$modifyUser = other.getModifyUser();
    if (this$modifyUser == null) {
      if (other$modifyUser != null) {
        return false;
      }
    } else if (!this$modifyUser.equals(other$modifyUser)) {
      return false;
    }
    Object this$createUser = getCreateUser();
    Object other$createUser = other.getCreateUser();
    if (this$createUser == null) {
      if (other$createUser != null) {
        return false;
      }
    } else if (!this$createUser.equals(other$createUser)) {
      return false;
    }
    Object this$name = getName();
    Object other$name = other.getName();
    if (this$name == null) {
      if (other$name != null) {
        return false;
      }
    } else if (!this$name.equals(other$name)) {
      return false;
    }
    Object this$fileUrl = getFileUrl();
    Object other$fileUrl = other.getFileUrl();
    if (this$fileUrl == null) {
      if (other$fileUrl != null) {
        return false;
      }
    } else if (!this$fileUrl.equals(other$fileUrl)) {
      return false;
    }
    Object this$status = getStatus();
    Object other$status = other.getStatus();
    if (this$status == null) {
      if (other$status != null) {
        return false;
      }
    } else if (!this$status.equals(other$status)) {
      return false;
    }
    Object this$type = getType();
    Object other$type = other.getType();
    if (this$type == null) {
      if (other$type != null) {
        return false;
      }
    } else if (!this$type.equals(other$type)) {
      return false;
    }
    Object this$extension = getExtension();
    Object other$extension = other.getExtension();
    return this$extension == null
        ? other$extension == null
        : this$extension.equals(other$extension);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof BuilderJobDTO;
  }

  public int hashCode() {
    Object $id = getId();
    int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $projectId = getProjectId();
    int result2 = (result * 59) + ($projectId == null ? 43 : $projectId.hashCode());
    Object $taskId = getTaskId();
    int result3 = (result2 * 59) + ($taskId == null ? 43 : $taskId.hashCode());
    Object $chunkNum = getChunkNum();
    int result4 = (result3 * 59) + ($chunkNum == null ? 43 : $chunkNum.hashCode());
    Object $gmtCreate = getGmtCreate();
    int result5 = (result4 * 59) + ($gmtCreate == null ? 43 : $gmtCreate.hashCode());
    Object $gmtModified = getGmtModified();
    int result6 = (result5 * 59) + ($gmtModified == null ? 43 : $gmtModified.hashCode());
    Object $modifyUser = getModifyUser();
    int result7 = (result6 * 59) + ($modifyUser == null ? 43 : $modifyUser.hashCode());
    Object $createUser = getCreateUser();
    int result8 = (result7 * 59) + ($createUser == null ? 43 : $createUser.hashCode());
    Object $name = getName();
    int result9 = (result8 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $fileUrl = getFileUrl();
    int result10 = (result9 * 59) + ($fileUrl == null ? 43 : $fileUrl.hashCode());
    Object $status = getStatus();
    int result11 = (result10 * 59) + ($status == null ? 43 : $status.hashCode());
    Object $type = getType();
    int result12 = (result11 * 59) + ($type == null ? 43 : $type.hashCode());
    Object $extension = getExtension();
    return (result12 * 59) + ($extension == null ? 43 : $extension.hashCode());
  }

  public String toString() {
    return "BuilderJobDTO(id="
        + getId()
        + ", projectId="
        + getProjectId()
        + ", gmtCreate="
        + getGmtCreate()
        + ", gmtModified="
        + getGmtModified()
        + ", modifyUser="
        + getModifyUser()
        + ", createUser="
        + getCreateUser()
        + ", taskId="
        + getTaskId()
        + ", name="
        + getName()
        + ", chunkNum="
        + getChunkNum()
        + ", fileUrl="
        + getFileUrl()
        + ", status="
        + getStatus()
        + ", type="
        + getType()
        + ", extension="
        + getExtension()
        + ")";
  }

  public Long getId() {
    return this.id;
  }

  public Long getProjectId() {
    return this.projectId;
  }

  public Date getGmtCreate() {
    return this.gmtCreate;
  }

  public Date getGmtModified() {
    return this.gmtModified;
  }

  public String getModifyUser() {
    return this.modifyUser;
  }

  public String getCreateUser() {
    return this.createUser;
  }

  public Long getTaskId() {
    return this.taskId;
  }

  public String getName() {
    return this.name;
  }

  public Long getChunkNum() {
    return this.chunkNum;
  }

  public String getFileUrl() {
    return this.fileUrl;
  }

  public String getStatus() {
    return this.status;
  }

  public String getType() {
    return this.type;
  }

  public String getExtension() {
    return this.extension;
  }
}
