package com.antgroup.openspgapp.biz.schema.model;

import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptType;
import java.util.ArrayList;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/SchemaModelConvertor.class */
public class SchemaModelConvertor {
  public static NodeTypeModel convert2NodeTypeModel(
      String namespace, BaseSPGType baseAdvancedType) {
    String prefix = namespace + ".";
    NodeTypeModel nodeTypeModel = new NodeTypeModel();
    nodeTypeModel.setOntologyId(baseAdvancedType.getOntologyId());
    nodeTypeModel.setName(baseAdvancedType.getName());
    nodeTypeModel.setName(baseAdvancedType.getName().replace(prefix, ""));
    nodeTypeModel.setNameZh(baseAdvancedType.getBasicInfo().getNameZh());
    nodeTypeModel.setDesc(baseAdvancedType.getBasicInfo().getDesc());
    List<PropertyModel> propertyModels = getPropertyModels(prefix, baseAdvancedType);
    nodeTypeModel.setProperties(propertyModels);
    nodeTypeModel.setType(baseAdvancedType.getSpgTypeEnum().name());
    nodeTypeModel.setParentName(
        baseAdvancedType.getParentTypeName().getNameEn().replace(prefix, ""));
    if (baseAdvancedType instanceof ConceptType) {
      ConceptType conceptType = (ConceptType) baseAdvancedType;
      if (null != conceptType.getConceptLayerConfig()) {
        nodeTypeModel.setHypernymPredicate(
            conceptType.getConceptLayerConfig().getHypernymPredicate());
      }
    }
    List<EdgeTypeModel> relations = new ArrayList<>();
    baseAdvancedType
        .getRelations()
        .forEach(
            relation -> {
              EdgeTypeModel edgeTypeModel = new EdgeTypeModel();
              edgeTypeModel.setOntologyId(relation.getOntologyId());
              edgeTypeModel.setName(relation.getName());
              edgeTypeModel.setInherited(relation.getInherited());
              edgeTypeModel.setSemanticRelation(Boolean.valueOf(relation.isSemanticRelation()));
              edgeTypeModel.setNameZh(relation.getBasicInfo().getNameZh());
              edgeTypeModel.setDesc(relation.getBasicInfo().getDesc());
              edgeTypeModel.setSourceType(
                  relation.getSubjectTypeRef().getName().replace(prefix, ""));
              edgeTypeModel.setTargetType(
                  relation.getObjectTypeRef().getName().replace(prefix, ""));
              List<PropertyModel> edgeProperty = getPropertyModels(relation.getSubProperties());
              edgeTypeModel.setProperties(edgeProperty);
              edgeTypeModel.setRule(relation.getLogicalRule());
              relations.add(edgeTypeModel);
            });
    nodeTypeModel.setRelations(relations);
    return nodeTypeModel;
  }

  private static List<PropertyModel> getPropertyModels(List<SubProperty> subProperties) {
    List<PropertyModel> propertyModels = new ArrayList<>();
    for (SubProperty subProperty : subProperties) {
      PropertyModel propertyModel = new PropertyModel();
      propertyModel.setOntologyId(subProperty.getOntologyId());
      propertyModel.setName(subProperty.getName());
      propertyModel.setNameZh(subProperty.getBasicInfo().getNameZh());
      propertyModel.setType(subProperty.getObjectTypeRef().getName());
      if (null != subProperty.getAdvancedConfig()
          && subProperty.getAdvancedConfig().getIndexType() != null) {
        propertyModel.setIndex(subProperty.getAdvancedConfig().getIndexType().name());
      }
      propertyModel.setConstraint(subProperty.getConstraint());
      propertyModels.add(propertyModel);
    }
    return propertyModels;
  }

  private static List<PropertyModel> getPropertyModels(
      String prefix, BaseSPGType baseAdvancedType) {
    List<PropertyModel> propertyModels = new ArrayList<>();
    List<Property> properties = baseAdvancedType.getProperties();
    for (Property property : properties) {
      PropertyModel propertyModel = new PropertyModel();
      propertyModel.setOntologyId(property.getOntologyId());
      propertyModel.setName(property.getName());
      propertyModel.setNameZh(property.getBasicInfo().getNameZh());
      propertyModel.setType(property.getObjectTypeRef().getName().replace(prefix, ""));
      propertyModel.setInherited(property.getInherited());
      if (null != property.getAdvancedConfig()
          && property.getAdvancedConfig().getIndexType() != null) {
        propertyModel.setIndex(property.getAdvancedConfig().getIndexType().name());
      }
      propertyModel.setRule(property.getLogicalRule());
      propertyModel.setConstraint(property.getConstraint());
      propertyModel.setSubProperties(getPropertyModels(property.getSubProperties()));
      propertyModels.add(propertyModel);
    }
    return propertyModels;
  }
}
