package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import java.io.Serializable;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/ObjectType.class */
public class ObjectType extends BaseDTO implements Serializable {
  private Long id;
  private String name;
  private String nameZh;

  public void setId(final Long id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setNameZh(final String nameZh) {
    this.nameZh = nameZh;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ObjectType)) {
      return false;
    }
    ObjectType other = (ObjectType) o;
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
    Object this$name = getName();
    Object other$name = other.getName();
    if (this$name == null) {
      if (other$name != null) {
        return false;
      }
    } else if (!this$name.equals(other$name)) {
      return false;
    }
    Object this$nameZh = getNameZh();
    Object other$nameZh = other.getNameZh();
    return this$nameZh == null ? other$nameZh == null : this$nameZh.equals(other$nameZh);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ObjectType;
  }

  public int hashCode() {
    Object $id = getId();
    int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $name = getName();
    int result2 = (result * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    return (result2 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
  }

  public String toString() {
    return "ObjectType(id=" + getId() + ", name=" + getName() + ", nameZh=" + getNameZh() + ")";
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getNameZh() {
    return this.nameZh;
  }
}
