package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import java.io.Serializable;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/AttrConsDTO.class */
public class AttrConsDTO extends BaseDTO implements Serializable {
  private String id;
  private String name;
  private String nameZh;
  private Object value;

  public void setId(final String id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setNameZh(final String nameZh) {
    this.nameZh = nameZh;
  }

  public void setValue(final Object value) {
    this.value = value;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AttrConsDTO)) {
      return false;
    }
    AttrConsDTO other = (AttrConsDTO) o;
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
    if (this$nameZh == null) {
      if (other$nameZh != null) {
        return false;
      }
    } else if (!this$nameZh.equals(other$nameZh)) {
      return false;
    }
    Object this$value = getValue();
    Object other$value = other.getValue();
    return this$value == null ? other$value == null : this$value.equals(other$value);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof AttrConsDTO;
  }

  public int hashCode() {
    Object $id = getId();
    int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $name = getName();
    int result2 = (result * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result3 = (result2 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $value = getValue();
    return (result3 * 59) + ($value == null ? 43 : $value.hashCode());
  }

  public String toString() {
    return "AttrConsDTO(id="
        + getId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", value="
        + getValue()
        + ")";
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getNameZh() {
    return this.nameZh;
  }

  public Object getValue() {
    return this.value;
  }

  public AttrConsDTO() {}

  public AttrConsDTO(String name) {
    this.name = name;
  }
}
