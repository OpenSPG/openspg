package com.antgroup.openspgapp.biz.schema.model;

import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.SchemaExtInfo;
import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.schema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.schema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.ConceptLayerConfig;
import com.antgroup.openspg.core.schema.model.type.ConceptTaxonomicConfig;
import com.antgroup.openspg.core.schema.model.type.ConceptType;
import com.antgroup.openspg.core.schema.model.type.EntityType;
import com.antgroup.openspg.core.schema.model.type.EventType;
import com.antgroup.openspg.core.schema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.schema.model.type.SPGTypeAdvancedConfig;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.server.core.schema.service.type.model.BuiltInPropertyEnum;
import com.antgroup.openspgapp.common.util.enums.AdvancedTypeEnum;
import com.antgroup.openspgapp.common.util.enums.BasicTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/SchemaCompareUtil.class */
public class SchemaCompareUtil {
  private static final Logger log = LoggerFactory.getLogger(SchemaCompareUtil.class);

  public SchemaChangeDTO compare(
      String namespace, List<NodeTypeModel> oldTypes, List<NodeTypeModel> newTypes) {
    List<BaseAdvancedType> addAdvancedType = new ArrayList<>();
    List<BaseAdvancedType> updateAdvancedType = new ArrayList<>();
    List<BaseAdvancedType> deleteAdvancedType = new ArrayList<>();
    SchemaChangeDTO result =
        new SchemaChangeDTO(addAdvancedType, updateAdvancedType, deleteAdvancedType);
    List<String> oldTypeNames = Lists.newArrayList();
    List<String> newTypeNames = Lists.newArrayList();
    Map<String, NodeTypeModel> oldMap = Maps.newHashMap();
    Map<String, NodeTypeModel> newMap = Maps.newHashMap();
    Map<String, String> name2Type = Maps.newHashMap();
    if (CollectionUtils.isEmpty(newTypes)) {
      convertToAddOrDelete(
          namespace, oldTypes, deleteAdvancedType, name2Type, AlterOperationEnum.DELETE);
      return result;
    }
    newTypes.forEach(
        n -> {
          newTypeNames.add(n.getName());
          newMap.put(n.getName(), n);
          name2Type.put(n.getName(), n.getType());
        });
    List<String> intersection = new ArrayList<>(newTypeNames);
    List<String> newCreate = new ArrayList<>(newTypeNames);
    List<NodeTypeModel> delete = new ArrayList<>();
    List<NodeTypeModel> create = new ArrayList<>();
    if (CollectionUtils.isEmpty(oldTypes)) {
      convertToAddOrDelete(
          namespace, newTypes, addAdvancedType, name2Type, AlterOperationEnum.CREATE);
      return result;
    }
    oldTypes.forEach(
        o -> {
          oldTypeNames.add(o.getName());
          oldMap.put(o.getName(), o);
          name2Type.put(o.getName(), o.getType());
          newCreate.remove(o.getName());
          if (!newTypeNames.contains(o.getName())) {
            delete.add(o);
          }
        });
    intersection.retainAll(oldTypeNames);
    for (String s : newCreate) {
      if (!oldTypeNames.contains(s)) {
        create.add(newMap.get(s));
      }
    }
    for (String s2 : intersection) {
      NodeTypeModel o2 = oldMap.get(s2);
      NodeTypeModel n2 = newMap.get(s2);
      convertToUpdate(namespace, o2, n2, name2Type, updateAdvancedType);
    }
    convertToAddOrDelete(
        namespace, delete, deleteAdvancedType, name2Type, AlterOperationEnum.DELETE);
    convertToAddOrDelete(namespace, create, addAdvancedType, name2Type, AlterOperationEnum.CREATE);
    return result;
  }

  private static void convertToUpdate(
      String namespace,
      NodeTypeModel o,
      NodeTypeModel n,
      Map<String, String> name2Type,
      List<BaseAdvancedType> updateAdvancedType) {
    BaseAdvancedType conceptType;
    if (!n.getType().equalsIgnoreCase(o.getType())) {
      log.warn("update node type, nodeName={}, oldType={}", n.getName(), o.getType());
      throw new SpgException(SpgMessageEnum.SCHEMA_CHANGE_NODE_TYPE);
    }
    SPGTypeIdentifier spgTypeIdentifier = new SPGTypeIdentifier(namespace, n.getName());
    BasicInfo basicInfo = new BasicInfo(spgTypeIdentifier, n.getNameZh(), n.getDesc());
    ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING;
    if (StringUtils.isNotBlank(n.getParentName())) {
      parentTypeInfo =
          new ParentTypeInfo(
              (Long) null, (Long) null, SPGTypeIdentifier.parse(n.getParentName()), (List) null);
    }
    SPGTypeAdvancedConfig spgTypeAdvancedConfig = new SPGTypeAdvancedConfig();
    List<PropertyModel> props = n.getProperties();
    AdvancedTypeEnum advancedTypeEnum = AdvancedTypeEnum.valueOf(n.getType());
    switch (AnonymousClass1.$SwitchMap$com$antgroup$openspgapp$common$util$enums$AdvancedTypeEnum[
        advancedTypeEnum.ordinal()]) {
      case 1:
        conceptType =
            new EntityType(
                basicInfo, parentTypeInfo, (List) null, (List) null, spgTypeAdvancedConfig);
        break;
      case 2:
        conceptType =
            new EventType(
                basicInfo, parentTypeInfo, (List) null, (List) null, spgTypeAdvancedConfig);
        break;
      case 3:
        if (!o.getHypernymPredicate().equalsIgnoreCase(n.getHypernymPredicate())) {
          log.warn(
              "update node type, nodeName={}, oldHypernymPredicate={}, newHypernymPredicate={}",
              new Object[] {n.getName(), o.getHypernymPredicate(), n.getHypernymPredicate()});
          throw new SpgException(SpgMessageEnum.SCHEMA_CHANGE_HYPERNYM_PREDICATE);
        }
        conceptType =
            new ConceptType(
                basicInfo,
                parentTypeInfo,
                (List) null,
                (List) null,
                spgTypeAdvancedConfig,
                new ConceptLayerConfig(n.getHypernymPredicate(), (List) null),
                (ConceptTaxonomicConfig) null,
                (MultiVersionConfig) null);
        break;
      default:
        throw new RuntimeException("unsupported advanced type: " + n.getType());
    }
    conceptType.setExtInfo(new SchemaExtInfo());
    updateProperty(namespace, o, props, name2Type, conceptType);
    updateRelation(namespace, o, n.getRelations(), name2Type, conceptType);
    conceptType.setOntologyId(o.getOntologyId());
    conceptType.setAlterOperation(AlterOperationEnum.UPDATE);
    updateAdvancedType.add(conceptType);
  }

  /* renamed from: com.antgroup.openspgapp.biz.schema.model.SchemaCompareUtil$1, reason: invalid class name */
  /* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/SchemaCompareUtil$1.class */
  static /* synthetic */ class AnonymousClass1 {
    static final /* synthetic */ int[]
        $SwitchMap$com$antgroup$openspgapp$common$util$enums$AdvancedTypeEnum =
            new int[AdvancedTypeEnum.values().length];

    static {
      try {
        $SwitchMap$com$antgroup$openspgapp$common$util$enums$AdvancedTypeEnum[
                AdvancedTypeEnum.ENTITY_TYPE.ordinal()] =
            1;
      } catch (NoSuchFieldError e) {
      }
      try {
        $SwitchMap$com$antgroup$openspgapp$common$util$enums$AdvancedTypeEnum[
                AdvancedTypeEnum.EVENT_TYPE.ordinal()] =
            2;
      } catch (NoSuchFieldError e2) {
      }
      try {
        $SwitchMap$com$antgroup$openspgapp$common$util$enums$AdvancedTypeEnum[
                AdvancedTypeEnum.CONCEPT_TYPE.ordinal()] =
            3;
      } catch (NoSuchFieldError e3) {
      }
    }
  }

  private static void updateRelation(
      String namespace,
      NodeTypeModel o,
      List<EdgeTypeModel> relations,
      Map<String, String> name2Type,
      BaseAdvancedType advancedType) {
    if (CollectionUtils.isEmpty(relations) && CollectionUtils.isEmpty(o.getRelations())) {
      return;
    }
    List<Relation> relationList = Lists.newArrayList();
    advancedType.setRelations(relationList);
    List<EdgeTypeModel> oldRelations = o.getRelations();
    Map<String, EdgeTypeModel> oldRelationMap = Maps.newHashMap();
    List<String> oldRelationSPO = Lists.newArrayList();
    if (CollectionUtils.isNotEmpty(oldRelations)) {
      for (EdgeTypeModel oldRelation : oldRelations) {
        String spo =
            String.format(
                "%s_%s_%s", o.getName(), oldRelation.getName(), oldRelation.getTargetType());
        oldRelationMap.put(spo, oldRelation);
        oldRelationSPO.add(spo);
      }
    }
    List<String> delete = new ArrayList<>(oldRelationSPO);
    if (CollectionUtils.isNotEmpty(relations) && !(advancedType instanceof ConceptType)) {
      relationCreateOrUpdate(
          namespace, o, relations, advancedType, oldRelationMap, delete, relationList, name2Type);
    }
    if (CollectionUtils.isEmpty(delete)) {
      return;
    }
    delete.forEach(
        spo2 -> {
          EdgeTypeModel typeModel = (EdgeTypeModel) oldRelationMap.get(spo2);
          SPGTypeRef targetType = getTargetType(typeModel.getTargetType(), namespace, name2Type);
          Relation relation =
              new Relation(
                  new BasicInfo(
                      new PredicateIdentifier(typeModel.getName()),
                      typeModel.getNameZh(),
                      typeModel.getDesc()),
                  advancedType.toRef(),
                  targetType,
                  false,
                  new PropertyAdvancedConfig(),
                  false);
          relation.setOntologyId(typeModel.getOntologyId());
          boolean semanticRelation =
              typeModel.getSemanticRelation() == null
                  ? false
                  : typeModel.getSemanticRelation().booleanValue();
          boolean isConceptRelation =
              (advancedType instanceof ConceptType)
                  && Constants.OPEN_CONCEPT_HYPERNYM_PREDICATE.contains(typeModel.getName());
          if (isConceptRelation || semanticRelation) {
            relation.setAlterOperation((AlterOperationEnum) null);
          } else {
            relation.setAlterOperation(AlterOperationEnum.DELETE);
          }
          relationList.add(relation);
        });
  }

  private static void relationCreateOrUpdate(
      String namespace,
      NodeTypeModel o,
      List<EdgeTypeModel> relations,
      BaseAdvancedType advancedType,
      Map<String, EdgeTypeModel> oldRelationMap,
      List<String> delete,
      List<Relation> relationList,
      Map<String, String> name2Type) {
    for (EdgeTypeModel edgeTypeModel : relations) {
      String spo =
          String.format(
              "%s_%s_%s", o.getName(), edgeTypeModel.getName(), edgeTypeModel.getTargetType());
      SPGTypeRef spgTypeRef = getTargetType(edgeTypeModel.getTargetType(), namespace, name2Type);
      Relation relation =
          new Relation(
              new BasicInfo(
                  new PredicateIdentifier(edgeTypeModel.getName()),
                  edgeTypeModel.getNameZh(),
                  edgeTypeModel.getDesc()),
              advancedType.toRef(),
              spgTypeRef,
              false,
              new PropertyAdvancedConfig(),
              false);
      relation.setExtInfo(new SchemaExtInfo());
      EdgeTypeModel typeModel = oldRelationMap.get(spo);
      if ((typeModel != null && CollectionUtils.isNotEmpty(typeModel.getProperties()))
          || CollectionUtils.isNotEmpty(edgeTypeModel.getProperties())) {
        updateRelationProperty(typeModel, edgeTypeModel.getProperties(), relation);
      }
      if (typeModel == null) {
        relation.getAdvancedConfig().setLogicalRule(setLogicalRule(null, edgeTypeModel.getRule()));
        relation.setAlterOperation(AlterOperationEnum.CREATE);
      } else {
        delete.remove(spo);
        relation
            .getAdvancedConfig()
            .setLogicalRule(setLogicalRule(typeModel.getRule(), edgeTypeModel.getRule()));
        relation.setOntologyId(typeModel.getOntologyId());
        relation.setAlterOperation(AlterOperationEnum.UPDATE);
      }
      relationList.add(relation);
    }
  }

  private static void updateProperty(
      String namespace,
      NodeTypeModel o,
      List<PropertyModel> props,
      Map<String, String> name2Type,
      BaseAdvancedType advancedType) {
    List<Property> propertyList = Lists.newArrayList();
    Map<String, PropertyModel> oldPropMap = Maps.newHashMap();
    List<String> oldPro = Lists.newArrayList();
    List<PropertyModel> oldProps = o.getProperties();
    for (PropertyModel oldProp : oldProps) {
      oldPropMap.put(oldProp.getName(), oldProp);
      oldPro.add(oldProp.getName());
    }
    List<PropertyModel> props2 = CollectionUtils.isEmpty(props) ? Lists.newArrayList() : props;
    List<String> deleteProps = new ArrayList<>(oldPro);
    for (PropertyModel newProp : props2) {
      PropertyModel oldProp2 = oldPropMap.get(newProp.getName());
      if (oldProp2 != null && !oldProp2.getType().equals(newProp.getType())) {
        log.warn(
            "update property type, propName={}, oldType={}", newProp.getName(), oldProp2.getType());
        throw new SpgException(SpgMessageEnum.SCHEMA_CHANGE_PROPERTY_TYPE);
      }
      SPGTypeRef propRef = getTargetType(newProp.getType(), namespace, name2Type);
      Property property =
          new Property(
              new BasicInfo(new PredicateIdentifier(newProp.getName()), newProp.getNameZh(), ""),
              advancedType.toRef(),
              propRef,
              Boolean.valueOf(oldProp2 == null ? false : isInherited(oldProp2)),
              new PropertyAdvancedConfig().setIndexType(getIndexType(newProp.getIndex())));
      buildEventProperty(advancedType, property);
      property.getAdvancedConfig().setConstraint(newProp.getConstraint());
      if (oldProp2 == null) {
        property.getAdvancedConfig().setLogicalRule(setLogicalRule(null, newProp.getRule()));
        property.setAlterOperation(
            advancedType instanceof ConceptType ? null : AlterOperationEnum.CREATE);
      } else {
        property
            .getAdvancedConfig()
            .setLogicalRule(setLogicalRule(oldProp2.getRule(), newProp.getRule()));
        deleteProps.remove(newProp.getName());
        property.setOntologyId(oldProp2.getOntologyId());
        property.setAlterOperation(AlterOperationEnum.UPDATE);
      }
      propertyList.add(property);
    }
    Set<String> builtInPropertyNames = new HashSet<>();
    if (advancedType instanceof ConceptType) {
      builtInPropertyNames = BuiltInPropertyEnum.getBuiltInPropertyName(SPGTypeEnum.CONCEPT_TYPE);
    } else if (advancedType instanceof EventType) {
      builtInPropertyNames = BuiltInPropertyEnum.getBuiltInPropertyName(SPGTypeEnum.EVENT_TYPE);
    }
    for (String name : deleteProps) {
      PropertyModel oldProp3 = oldPropMap.get(name);
      SPGTypeRef propRef2 = getTargetType(oldProp3.getType(), namespace, name2Type);
      boolean isInherited = isInherited(oldProp3);
      Property property2 =
          new Property(
              new BasicInfo(new PredicateIdentifier(name), name, ""),
              advancedType.toRef(),
              propRef2,
              Boolean.valueOf(isInherited),
              new PropertyAdvancedConfig());
      if (!isInherited && !builtInPropertyNames.contains(name)) {
        property2.setAlterOperation(AlterOperationEnum.DELETE);
      }
      property2.getAdvancedConfig().setLogicalRule(oldProp3.getRule());
      property2.setOntologyId(oldProp3.getOntologyId());
      propertyList.add(property2);
    }
    advancedType.setProperties(propertyList);
  }

  private static void updateRelationProperty(
      EdgeTypeModel oldEdge, List<PropertyModel> properties, Relation relation) {
    if (CollectionUtils.isEmpty(properties)
        && (oldEdge == null || CollectionUtils.isEmpty(oldEdge.getProperties()))) {
      return;
    }
    List<SubProperty> subProperties = Lists.newArrayList();
    relation.getAdvancedConfig().setSubProperties(subProperties);
    Map<String, PropertyModel> oldsubPropMap = Maps.newHashMap();
    List<String> oldSubProp = Lists.newArrayList();
    if (oldEdge != null && CollectionUtils.isNotEmpty(oldEdge.getProperties())) {
      List<PropertyModel> oldProps = oldEdge.getProperties();
      for (PropertyModel oldProp : oldProps) {
        oldsubPropMap.put(oldProp.getName(), oldProp);
        oldSubProp.add(oldProp.getName());
      }
    }
    List<String> deleteProps = new ArrayList<>(oldSubProp);
    if (CollectionUtils.isNotEmpty(properties)) {
      relationPropertyCreateOrUpdate(
          properties, relation, oldsubPropMap, deleteProps, subProperties);
    }
    if (CollectionUtils.isEmpty(deleteProps)) {
      return;
    }
    deleteProps.forEach(
        name -> {
          PropertyModel oldProp2 = (PropertyModel) oldsubPropMap.get(name);
          SubProperty subProperty =
              new SubProperty(
                  new BasicInfo(
                      new PredicateIdentifier(oldProp2.getName()), oldProp2.getNameZh(), ""),
                  relation.toRef(),
                  new SPGTypeRef(
                      new BasicInfo(
                          new SPGTypeIdentifier((String) null, BasicTypeEnum.TEXT.getCode()),
                          BasicTypeEnum.TEXT.getDesc(),
                          BasicTypeEnum.TEXT.getDesc()),
                      SPGTypeEnum.BASIC_TYPE),
                  new PropertyAdvancedConfig());
          subProperty.setOntologyId(oldProp2.getOntologyId());
          if (oldProp2.getInherited() == null || !oldProp2.getInherited().booleanValue()) {
            subProperty.setAlterOperation(AlterOperationEnum.DELETE);
          }
          subProperties.add(subProperty);
        });
  }

  private static void relationPropertyCreateOrUpdate(
      List<PropertyModel> properties,
      Relation relation,
      Map<String, PropertyModel> oldsubPropMap,
      List<String> deleteProps,
      List<SubProperty> subProperties) {
    for (PropertyModel newSubProp : properties) {
      BasicTypeEnum propertyType = getPropertyType(newSubProp.getType());
      SubProperty subProperty =
          new SubProperty(
              new BasicInfo(
                  new PredicateIdentifier(newSubProp.getName()), newSubProp.getNameZh(), ""),
              relation.toRef(),
              new SPGTypeRef(
                  new BasicInfo(
                      new SPGTypeIdentifier((String) null, propertyType.getCode()),
                      propertyType.getDesc(),
                      propertyType.getDesc()),
                  SPGTypeEnum.BASIC_TYPE),
              new PropertyAdvancedConfig());
      PropertyModel propertyModel = oldsubPropMap.get(newSubProp.getName());
      if (propertyModel == null) {
        subProperty.setAlterOperation(AlterOperationEnum.CREATE);
      } else {
        deleteProps.remove(newSubProp.getName());
        subProperty.setOntologyId(propertyModel.getOntologyId());
        subProperty.setAlterOperation(AlterOperationEnum.UPDATE);
      }
      subProperties.add(subProperty);
    }
  }

  private static void convertToAddOrDelete(
      String namespace,
      List<NodeTypeModel> nodes,
      List<BaseAdvancedType> advancedType,
      Map<String, String> name2Type,
      AlterOperationEnum alterOperation) {
    List<PropertyModel> properties;
    String hypernymPredicate;
    BaseAdvancedType conceptType;
    if (CollectionUtils.isEmpty(nodes)) {
      return;
    }
    if (name2Type.isEmpty()) {
      name2Type =
          (Map)
              nodes.stream()
                  .collect(
                      Collectors.toMap(
                          (v0) -> {
                            return v0.getName();
                          },
                          (v0) -> {
                            return v0.getType();
                          }));
    }
    for (NodeTypeModel oldType : nodes) {
      SPGTypeIdentifier spgTypeIdentifier = new SPGTypeIdentifier(namespace, oldType.getName());
      BasicInfo basicInfo =
          new BasicInfo(spgTypeIdentifier, oldType.getNameZh(), oldType.getDesc());
      ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING;
      if (StringUtils.isNotBlank(oldType.getParentName())) {
        parentTypeInfo =
            new ParentTypeInfo(
                (Long) null,
                (Long) null,
                SPGTypeIdentifier.parse(oldType.getParentName()),
                (List) null);
      }
      SPGTypeAdvancedConfig spgTypeAdvancedConfig = new SPGTypeAdvancedConfig();
      if (CollectionUtils.isEmpty(oldType.getProperties())) {
        properties = Lists.newArrayList();
      } else {
        properties = oldType.getProperties();
      }
      List<PropertyModel> props = properties;
      List<Property> propertyList = Lists.newArrayList();
      AdvancedTypeEnum advancedTypeEnum = AdvancedTypeEnum.valueOf(oldType.getType());
      switch (AnonymousClass1.$SwitchMap$com$antgroup$openspgapp$common$util$enums$AdvancedTypeEnum[
          advancedTypeEnum.ordinal()]) {
        case 1:
          conceptType =
              new EntityType(
                  basicInfo, parentTypeInfo, propertyList, (List) null, spgTypeAdvancedConfig);
          break;
        case 2:
          conceptType =
              new EventType(
                  basicInfo, parentTypeInfo, propertyList, (List) null, spgTypeAdvancedConfig);
          break;
        case 3:
          if (StringUtils.isBlank(oldType.getHypernymPredicate())) {
            hypernymPredicate = SystemPredicateEnum.IS_A.getName();
          } else {
            hypernymPredicate = oldType.getHypernymPredicate();
          }
          String hypernymPredicate2 = hypernymPredicate;
          conceptType =
              new ConceptType(
                  basicInfo,
                  parentTypeInfo,
                  propertyList,
                  (List) null,
                  spgTypeAdvancedConfig,
                  new ConceptLayerConfig(hypernymPredicate2, (List) null),
                  (ConceptTaxonomicConfig) null,
                  (MultiVersionConfig) null);
          break;
        default:
          throw new RuntimeException("unsupported advanced type: " + oldType.getType());
      }
      conceptType.setOntologyId(oldType.getOntologyId());
      conceptType.setExtInfo(new SchemaExtInfo());
      if (conceptType instanceof ConceptType) {
        conceptType.setAlterOperation(alterOperation);
        advancedType.add(conceptType);
      } else {
        for (PropertyModel prop : props) {
          SPGTypeRef propRef = getTargetType(prop.getType(), namespace, name2Type);
          boolean isInherited = isInherited(prop);
          Property property =
              new Property(
                  new BasicInfo(new PredicateIdentifier(prop.getName()), prop.getNameZh(), ""),
                  conceptType.toRef(),
                  propRef,
                  Boolean.valueOf(isInherited),
                  new PropertyAdvancedConfig().setIndexType(getIndexType(prop.getIndex())));
          property.setOntologyId(prop.getOntologyId());
          property.getAdvancedConfig().setLogicalRule(setLogicalRule(null, prop.getRule()));
          property.getAdvancedConfig().setConstraint(prop.getConstraint());
          property.setExtInfo(new SchemaExtInfo());
          buildEventProperty(conceptType, property);
          if (!isInherited) {
            property.setAlterOperation(alterOperation);
          }
          propertyList.add(property);
        }
        if (CollectionUtils.isNotEmpty(oldType.getRelations())) {
          List<Relation> relations = Lists.newArrayList();
          List<EdgeTypeModel> relationModels = oldType.getRelations();
          for (EdgeTypeModel relationModel : relationModels) {
            PropertyAdvancedConfig propertyAdvancedConfig = new PropertyAdvancedConfig();
            boolean isInherited2 =
                relationModel.getInherited() != null && relationModel.getInherited().booleanValue();
            SPGTypeRef propRef2 =
                getTargetType(relationModel.getTargetType(), namespace, name2Type);
            Relation relation =
                new Relation(
                    new BasicInfo(
                        new PredicateIdentifier(relationModel.getName()),
                        relationModel.getNameZh(),
                        relationModel.getDesc()),
                    conceptType.toRef(),
                    propRef2,
                    Boolean.valueOf(isInherited2),
                    propertyAdvancedConfig,
                    false);
            relation
                .getAdvancedConfig()
                .setLogicalRule(setLogicalRule(null, relationModel.getRule()));
            updateRelationProperty(relationModel, relationModel.getProperties(), relation);
            relation.setOntologyId(relationModel.getOntologyId());
            relation.setExtInfo(new SchemaExtInfo());
            if (!isInherited2) {
              relation.setAlterOperation(alterOperation);
            }
            relations.add(relation);
          }
          conceptType.setRelations(relations);
        }
        conceptType.setAlterOperation(alterOperation);
        advancedType.add(conceptType);
      }
    }
  }

  private static void buildEventProperty(BaseAdvancedType advancedType, Property property) {
    if (advancedType instanceof EventType) {
      String name = property.getName();
      if (PropertyGroupEnum.SUBJECT.getNameEn().equalsIgnoreCase(name)) {
        property.getAdvancedConfig().setPropertyGroup(PropertyGroupEnum.SUBJECT);
      }
      if (PropertyGroupEnum.OBJECT.getNameEn().equalsIgnoreCase(name)) {
        property.getAdvancedConfig().setPropertyGroup(PropertyGroupEnum.OBJECT);
      }
    }
  }

  private static SPGTypeRef getTargetType(
      String targetType, String namespace, Map<String, String> typeMap) {
    SPGTypeRef spgTypeRef;
    if (BasicTypeEnum.isBasicType(targetType)) {
      BasicTypeEnum propertyType = getPropertyType(targetType);
      spgTypeRef =
          new SPGTypeRef(
              new BasicInfo(
                  new SPGTypeIdentifier((String) null, propertyType.getCode()),
                  propertyType.getDesc(),
                  propertyType.getDesc()),
              SPGTypeEnum.BASIC_TYPE);
    } else if (targetType.startsWith("STD.")) {
      spgTypeRef =
          new SPGTypeRef(
              new BasicInfo(SPGTypeIdentifier.parse(targetType)), SPGTypeEnum.STANDARD_TYPE);
    } else {
      SPGTypeIdentifier propSpgTypeIdentifier = new SPGTypeIdentifier(namespace, targetType);
      String s = typeMap.get(targetType);
      if (StringUtils.isBlank(s)) {
        throw new RuntimeException("can not find type: " + targetType);
      }
      spgTypeRef = new SPGTypeRef(new BasicInfo(propSpgTypeIdentifier), SPGTypeEnum.toEnum(s));
    }
    return spgTypeRef;
  }

  private static LogicalRule setLogicalRule(LogicalRule oldRule, LogicalRule newRule) {
    if (oldRule == null && newRule == null) {
      return null;
    }
    if (oldRule != null && newRule == null) {
      return null;
    }
    if (oldRule == null && newRule != null) {
      return newRule;
    }
    if (oldRule != null && newRule != null) {
      newRule.setCode(oldRule.getCode());
      return newRule;
    }
    return null;
  }

  private static IndexTypeEnum getIndexType(String index) {
    IndexTypeEnum indexTypeEnum = null;
    if (StringUtils.isNotBlank(index)) {
      indexTypeEnum = IndexTypeEnum.valueOf(index.trim());
    }
    return indexTypeEnum;
  }

  private static boolean isInherited(PropertyModel propertyModel) {
    return propertyModel.getInherited() != null && propertyModel.getInherited().booleanValue();
  }

  private static BasicTypeEnum getPropertyType(String type) {
    if (StringUtils.isBlank(type)) {
      return BasicTypeEnum.TEXT;
    }
    return BasicTypeEnum.getByCode(type.trim());
  }

  /* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/SchemaCompareUtil$SchemaChangeDTO.class */
  public class SchemaChangeDTO {
    private List<BaseAdvancedType> addTypes;
    private List<BaseAdvancedType> updateTypes;
    private List<BaseAdvancedType> deleteTypes;

    public List<BaseAdvancedType> getAddTypes() {
      return this.addTypes;
    }

    public List<BaseAdvancedType> getUpdateTypes() {
      return this.updateTypes;
    }

    public List<BaseAdvancedType> getDeleteTypes() {
      return this.deleteTypes;
    }

    public SchemaChangeDTO(
        List<BaseAdvancedType> addTypes,
        List<BaseAdvancedType> updateTypes,
        List<BaseAdvancedType> deleteTypes) {
      this.addTypes = addTypes == null ? new ArrayList<>() : addTypes;
      this.updateTypes = updateTypes == null ? new ArrayList<>() : updateTypes;
      this.deleteTypes = deleteTypes == null ? new ArrayList<>() : deleteTypes;
    }
  }
}
