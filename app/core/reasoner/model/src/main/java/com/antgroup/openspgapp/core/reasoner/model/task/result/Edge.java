package com.antgroup.openspgapp.core.reasoner.model.task.result;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/task/result/Edge.class */
public class Edge extends BaseModel {
  private static final long serialVersionUID = 6567121968824686072L;
  private String docId;
  private String id;
  private String from;
  private String fromId;
  private String fromType;
  private String to;
  private String toId;
  private String toType;
  private String label;
  private Long version;
  private Map<String, Object> properties;

  public void setDocId(final String docId) {
    this.docId = docId;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setFrom(final String from) {
    this.from = from;
  }

  public void setFromId(final String fromId) {
    this.fromId = fromId;
  }

  public void setFromType(final String fromType) {
    this.fromType = fromType;
  }

  public void setTo(final String to) {
    this.to = to;
  }

  public void setToId(final String toId) {
    this.toId = toId;
  }

  public void setToType(final String toType) {
    this.toType = toType;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public void setProperties(final Map<String, Object> properties) {
    this.properties = properties;
  }

  public String getDocId() {
    return this.docId;
  }

  public String getId() {
    return this.id;
  }

  public String getFrom() {
    return this.from;
  }

  public String getFromId() {
    return this.fromId;
  }

  public String getFromType() {
    return this.fromType;
  }

  public String getTo() {
    return this.to;
  }

  public String getToId() {
    return this.toId;
  }

  public String getToType() {
    return this.toType;
  }

  public String getLabel() {
    return this.label;
  }

  public Long getVersion() {
    return this.version;
  }

  public Map<String, Object> getProperties() {
    return this.properties;
  }

  public Edge() {
    this.properties = new HashMap();
  }

  public Edge(IEdge<IVertexId, IProperty> edge) {
    this.properties = new HashMap();
    this.id = UUID.randomUUID().toString();
    this.from = String.valueOf(((IVertexId) edge.getSourceId()).getInternalId());
    this.fromType = ((IVertexId) edge.getSourceId()).getType();
    this.to = String.valueOf(((IVertexId) edge.getTargetId()).getInternalId());
    this.toType = ((IVertexId) edge.getTargetId()).getType();
    if (Direction.IN.equals(edge.getDirection())) {
      this.from = String.valueOf(((IVertexId) edge.getTargetId()).getInternalId());
      this.fromType = ((IVertexId) edge.getTargetId()).getType();
      this.to = String.valueOf(((IVertexId) edge.getSourceId()).getInternalId());
      this.toType = ((IVertexId) edge.getSourceId()).getType();
    }
    this.label = getEdgeType(edge.getType());
    this.version = edge.getVersion();
    if (null != edge.getValue()) {
      this.fromId = String.valueOf(((IProperty) edge.getValue()).get("__from_id__"));
      this.toId = String.valueOf(((IProperty) edge.getValue()).get("__to_id__"));
      if (Direction.IN.equals(edge.getDirection())) {
        this.fromId = String.valueOf(((IProperty) edge.getValue()).get("__to_id__"));
        this.toId = String.valueOf(((IProperty) edge.getValue()).get("__from_id__"));
      }
      for (String key : ((IProperty) edge.getValue()).getKeySet()) {
        this.properties.put(key, ((IProperty) edge.getValue()).get(key));
      }
    }
  }

  private String getEdgeType(String spoStr) {
    SPO spo = new SPO(spoStr);
    return spo.getP();
  }
}
