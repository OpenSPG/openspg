package com.antgroup.openspg.server.biz.service.convertor;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.server.api.facade.dto.service.response.RelationInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;

public class InstanceConvertor {

  public static SPGTypeInstance toInstance(VertexRecord vertexRecord) {
    SPGTypeInstance instance = new SPGTypeInstance();
    instance.setId(vertexRecord.getId());
    instance.setSpgType(vertexRecord.getVertexType());
    instance.setProperties(vertexRecord.toPropertyMapWithId());
    return instance;
  }

  public static RelationInstance toInstance(EdgeRecord edgeRecord) {
    RelationInstance instance = new RelationInstance();
    instance.setSrcId(edgeRecord.getSrcId());
    instance.setDstId(edgeRecord.getDstId());
    instance.setRelationType(edgeRecord.getEdgeType().getEdgeLabel());
    instance.setProperties(edgeRecord.toPropertyMapWithId());
    return instance;
  }
}
