package com.antgroup.openspgapp.core.reasoner.model.task.result;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.HashMap;
import java.util.Map;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/task/result/Node.class */
public class Node extends BaseModel {
  private static final long serialVersionUID = 2507408653380201214L;
  private String id;
  private String bizId;
  private String name;
  private String label;
  private Map<String, Object> properties = new HashMap();

  public Node() {}

  public Node(IVertex<IVertexId, IProperty> vertex) {
    this.id = String.valueOf(((IVertexId) vertex.getId()).getInternalId());
    this.bizId = String.valueOf(((IProperty) vertex.getValue()).get("id"));
    this.label = ((IVertexId) vertex.getId()).getType();
    if (null != vertex.getValue()) {
      Object name = ((IProperty) vertex.getValue()).get("name");
      this.name = String.valueOf(null == name ? ((IProperty) vertex.getValue()).get("id") : name);
      for (String key : ((IProperty) vertex.getValue()).getKeySet()) {
        this.properties.put(key, ((IProperty) vertex.getValue()).get(key));
      }
    }
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Map<String, Object> getProperties() {
    return this.properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
