package com.antgroup.openspgapp.server.api.facade.dto.schema;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/schema/SchemaSaveRequest.class */
public class SchemaSaveRequest extends BaseRequest {
  private String data;

  public void setData(final String data) {
    this.data = data;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof SchemaSaveRequest)) {
      return false;
    }
    SchemaSaveRequest other = (SchemaSaveRequest) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$data = getData();
    Object other$data = other.getData();
    return this$data == null ? other$data == null : this$data.equals(other$data);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof SchemaSaveRequest;
  }

  public int hashCode() {
    Object $data = getData();
    int result = (1 * 59) + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  public String toString() {
    return "SchemaSaveRequest(data=" + getData() + ")";
  }

  public String getData() {
    return this.data;
  }
}
