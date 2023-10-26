/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.schema.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.schema.SPGSchema2LPGService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.SPGTypeUtils;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.BaseLPGOntology;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGSchema;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AddPropertyOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseCreateTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseLPGSchemaOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseSchemaAtomicOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropPropertyOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.SchemaAtomicOperationEnum;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.VertexEdgeTypeOperationEnum;
import com.antgroup.openspg.common.model.exception.GraphStoreException;
import com.antgroup.openspg.core.spgschema.model.SPGSchema;
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.BasicTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.model.type.StandardType;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SPGSchema2LPGServiceImpl implements SPGSchema2LPGService {

    private final BaseLPGGraphStoreClient lpgGraphStoreClient;

    public SPGSchema2LPGServiceImpl(BaseLPGGraphStoreClient lpgGraphStoreClient) {
        this.lpgGraphStoreClient = lpgGraphStoreClient;
    }

    @Override
    public List<BaseLPGSchemaOperation> translate(SPGSchema spgSchema) {

        if (CollectionUtils.isEmpty(spgSchema.getSpgTypes())) {
            return Collections.emptyList();
        }

        List<BaseLPGSchemaOperation> expectedRecords = Lists.newArrayList();
        for (BaseSPGType spgType : spgSchema.getSpgTypes()) {
            if (spgType.isBasicType()) {
                continue;
            }
            expectedRecords.addAll(translate2VertexTypeOperation(spgType, spgSchema.getSpreadStdTypeNames()));
            expectedRecords.addAll(translate2EdgeTypeOperation(spgType));
        }
        this.addUsedStandardTypeV2(spgSchema.getSpgTypes(), expectedRecords);

        LPGSchema lpgSchema = lpgGraphStoreClient.querySchema();
        List<BaseLPGSchemaOperation> actualOperations = checkSchemaOperations(expectedRecords, lpgSchema);
        actualOperations.forEach(BaseLPGSchemaOperation::checkSchemaAtomicOperations);
        return actualOperations;
    }

    private List<BaseLPGSchemaOperation> checkSchemaOperations(
        List<BaseLPGSchemaOperation> expectedRecords, LPGSchema lpgSchema) {

        Map<String, BaseLPGOntology> lpgSchemaMap =
            Stream.concat(
                    lpgSchema.getVertexTypes().stream(),
                    lpgSchema.getEdgeTypes().stream())
                .collect(Collectors.toMap(BaseLPGOntology::getTypeName, Function.identity()));

        return expectedRecords.stream()
            .map(record -> calculateSingleSchemaChange(
                record,
                lpgSchemaMap.get(record.getTargetTypeName())))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private List<BaseLPGSchemaOperation> translate2VertexTypeOperation(
        BaseSPGType spgType, Set<SPGTypeIdentifier> spreadStdTypeNames) {

        BaseLPGSchemaOperation schemaOperationRecord = this.convert2VertexTypeOperation(spgType);
        List<BaseLPGSchemaOperation> operationRecords = Lists.newArrayList(schemaOperationRecord);

        List<BaseLPGSchemaOperation> semanticEdgeOperationRecords =
            this.generateSemanticEdgeTypeOperation(spgType, spreadStdTypeNames);
        operationRecords.addAll(semanticEdgeOperationRecords);
        return operationRecords;
    }

    private List<BaseLPGSchemaOperation> generateSemanticEdgeTypeOperation(
        BaseSPGType spgType, Set<SPGTypeIdentifier> spreadStdTypeNames) {
        if (CollectionUtils.isEmpty(spgType.getProperties())) {
            return Collections.emptyList();
        }

        List<BaseLPGSchemaOperation> operationRecords = Lists.newArrayList();
        spgType.getProperties().forEach(property -> {
            if (property.getAlterOperation() == null
                || !property.getObjectTypeRef().isAdvancedType()) {
                return;
            }

            if (property.getObjectTypeRef().isStandardType()
                && !spreadStdTypeNames.contains(property.getObjectTypeRef().getBaseSpgIdentifier())) {
                return;
            }

            if (property.getAlterOperation() != null) {
                operationRecords.add(convert2LpgEdgeTypeOperation(property));
            }
        });
        return operationRecords;
    }

    private void addUsedStandardTypeV2(List<BaseSPGType> spgTypes,
        List<BaseLPGSchemaOperation> operationRecords) {
        Set<String> addedVertexTypes = operationRecords.stream()
            .filter(record ->
                VertexEdgeTypeOperationEnum.CREATE_VERTEX_TYPE.equals(record.getOperationTypeEnum()))
            .map(record -> ((CreateVertexTypeOperation) record).getVertexTypeName())
            .collect(Collectors.toSet());
        spgTypes.forEach(spgType -> {
            if (CollectionUtils.isEmpty(spgType.getProperties())) {
                return;
            }

            for (Property property : spgType.getProperties()) {
                SPGTypeRef objectTypeRef = property.getObjectTypeRef();
                if (objectTypeRef.isStandardType()
                    && !addedVertexTypes.contains(objectTypeRef.getName())) {
                    StandardType standardType = new StandardType(objectTypeRef.getBasicInfo(),
                        ParentTypeInfo.THING, null, null,
                        null, null, null);
                    standardType.setAlterOperation(AlterOperationEnum.CREATE);
                    operationRecords.add(convert2VertexTypeOperation(standardType));
                    addedVertexTypes.add(standardType.getName());
                }
            }
        });
    }

    private List<BaseLPGSchemaOperation> translate2EdgeTypeOperation(BaseSPGType spgType) {
        List<BaseLPGSchemaOperation> edgeTypeOperationRecords = new ArrayList<>();
        if (CollectionUtils.isEmpty(spgType.getRelations())) {
            return edgeTypeOperationRecords;
        }

        spgType.getRelations().forEach(r -> {
            if (r.getAlterOperation() == null) {
                return;
            }

            if (r.isSemanticRelation()) {
                return;
            }

            edgeTypeOperationRecords.add(convert2LpgEdgeTypeOperation(r));
        });
        return edgeTypeOperationRecords;
    }

    private BaseLPGSchemaOperation convert2VertexTypeOperation(BaseSPGType spgType) {

        switch (spgType.getAlterOperation()) {
            case UPDATE:
                return convert2AlterVertexTypeOperation(spgType);
            case DELETE:
                return new DropVertexTypeOperation(spgType.getName());
            case CREATE:
                return convert2CreateVertexTypeOperation(spgType);
            default:
                throw GraphStoreException.unexpectedAlterOperationEnum(spgType.getAlterOperation());
        }
    }

    private CreateVertexTypeOperation convert2CreateVertexTypeOperation(BaseSPGType spgType) {
        List<BaseSchemaAtomicOperation> schemaAtomicOperations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(spgType.getProperties())) {
            spgType.getProperties().forEach(property -> {
                schemaAtomicOperations.addAll(
                    this.convert2SchemaAtomicOperations(property, AlterOperationEnum.CREATE)
                );
            });
        } else if (spgType.isStandardType()) {
            schemaAtomicOperations.add(new AddPropertyOperation(new LPGProperty(VertexType.ID, BasicTypeEnum.TEXT)));
            schemaAtomicOperations.add(new AddPropertyOperation(new LPGProperty("name", BasicTypeEnum.TEXT)));
            schemaAtomicOperations.add(new AddPropertyOperation(new LPGProperty("description", BasicTypeEnum.TEXT)));
        }
        return new CreateVertexTypeOperation(spgType.getName(), schemaAtomicOperations);
    }

    private AlterVertexTypeOperation convert2AlterVertexTypeOperation(BaseSPGType spgType) {
        if (CollectionUtils.isEmpty(spgType.getProperties())) {
            return new AlterVertexTypeOperation(spgType.getName());
        }

        List<BaseSchemaAtomicOperation> schemaAtomicOperations = Lists.newArrayList();

        spgType.getProperties().forEach(property -> {
            schemaAtomicOperations.addAll(
                convert2SchemaAtomicOperations(property, property.getAlterOperation()));
        });
        return new AlterVertexTypeOperation(spgType.getName(), schemaAtomicOperations);
    }

    private BaseLPGSchemaOperation convert2LpgEdgeTypeOperation(Property spgRelation) {

        String startVertexName = spgRelation.getSubjectTypeRef().getName();
        String endVertexName = spgRelation.getObjectTypeRef().getName();
        String edgeName = spgRelation.getName();
        EdgeTypeName edgeTypeName = new EdgeTypeName(startVertexName, edgeName, endVertexName);

        List<LPGProperty> properties = new ArrayList<>();

        BaseLPGSchemaOperation operationRecord;
        switch (spgRelation.getAlterOperation()) {
            case CREATE:
                properties.add(new LPGProperty(EdgeType.SRC_ID, BasicTypeEnum.TEXT));
                properties.add(new LPGProperty(EdgeType.DST_ID, BasicTypeEnum.TEXT));
                properties.add(new LPGProperty(EdgeType.VERSION, BasicTypeEnum.LONG));
                if (spgRelation.hasSubProperty()) {
                    spgRelation.getSubProperties().forEach(subPropertyType
                        -> properties.add(convert2LpgProperty(null, subPropertyType)));
                }

                CreateEdgeTypeOperation createEdgeTypeOperation = new CreateEdgeTypeOperation(edgeTypeName);
                properties.forEach(createEdgeTypeOperation::addProperty);
                operationRecord = createEdgeTypeOperation;
                break;
            case UPDATE:
                operationRecord = convert2AlterEdgeTypeOperation(spgRelation);
                break;
            case DELETE:
                operationRecord = new DropEdgeTypeOperation(edgeTypeName);
                break;
            default:
                throw GraphStoreException.unexpectedAlterOperationEnum(spgRelation.getAlterOperation());
        }
        return operationRecord;
    }

    private AlterEdgeTypeOperation convert2AlterEdgeTypeOperation(Property spgRelation) {
        String startVertexName = spgRelation.getSubjectTypeRef().getName();
        String endVertexName = spgRelation.getObjectTypeRef().getName();
        String edgeName = spgRelation.getName();
        EdgeTypeName edgeTypeName = new EdgeTypeName(startVertexName, edgeName, endVertexName);

        AlterEdgeTypeOperation alterEdgeTypeOperation = new AlterEdgeTypeOperation(edgeTypeName);
        if (spgRelation.hasSubProperty()) {
            spgRelation.getSubProperties().forEach(
                subPropertyType -> {
                    LPGProperty lpgProperty = convert2LpgProperty(null, subPropertyType);
                    if (subPropertyType.getAlterOperation() == null) {
                        return;
                    }
                    switch (subPropertyType.getAlterOperation()) {
                        case DELETE:
                            alterEdgeTypeOperation.dropProperty(lpgProperty.getName());
                            break;
                        case CREATE:
                            alterEdgeTypeOperation.addProperty(lpgProperty);
                            break;
                        case UPDATE:
                            break;
                        default:
                            throw GraphStoreException.unexpectedAlterOperationEnum(subPropertyType.getAlterOperation());
                    }
                });
        }
        return alterEdgeTypeOperation;
    }

    private List<BaseSchemaAtomicOperation> convert2SchemaAtomicOperations(
        Property spgProperty, AlterOperationEnum operationEnum) {
        if (operationEnum == null || spgProperty == null) {
            return Collections.emptyList();
        }

        List<BaseSchemaAtomicOperation> schemaOperationRecords = new ArrayList<>();

        String name = spgProperty.getName();
        BasicTypeEnum property = SPGTypeUtils.toBasicType(spgProperty.getObjectTypeRef());
        schemaOperationRecords.add(getPropertyOperation(new LPGProperty(name, property), operationEnum));

        if (CollectionUtils.isEmpty(spgProperty.getSubProperties())) {
            return schemaOperationRecords;
        }

        List<SubProperty> subPropertyTypes = spgProperty.getSubProperties();
        if (AlterOperationEnum.UPDATE.equals(operationEnum)) {
            subPropertyTypes.forEach(subPropertyType ->
                schemaOperationRecords.add(
                    getPropertyOperation(convert2LpgProperty(name, subPropertyType),
                        subPropertyType.getAlterOperation())));
        } else {
            subPropertyTypes.forEach(subPropertyType ->
                schemaOperationRecords.add(
                    getPropertyOperation(convert2LpgProperty(name, subPropertyType), operationEnum)));
        }

        return schemaOperationRecords;
    }

    private BaseSchemaAtomicOperation getPropertyOperation(
        LPGProperty property, AlterOperationEnum operationEnum) {
        if (operationEnum == null || property == null) {
            return null;
        }
        switch (operationEnum) {
            case CREATE:
                return new AddPropertyOperation(property);
            case DELETE:
                return new DropPropertyOperation(property.getName());
            default:
                return null;
        }
    }

    private LPGProperty convert2LpgProperty(String propertyName, SubProperty spgSubProperty) {
        String name = propertyName == null ? spgSubProperty.getName()
            : String.format("%s_%s", propertyName, spgSubProperty.getName());
        BasicTypeEnum property = SPGTypeUtils.toBasicType(spgSubProperty.getObjectTypeRef());
        return new LPGProperty(name, property);
    }

    private BaseLPGSchemaOperation calculateSingleSchemaChange(
        BaseLPGSchemaOperation operationRecord, BaseLPGOntology ontologyInLpgGraphStore) {
        switch (operationRecord.getOperationTypeEnum()) {
            case DROP_EDGE_TYPE:
            case DROP_VERTEX_TYPE:
                return ontologyInLpgGraphStore == null
                    ? null
                    : operationRecord;
            case CREATE_EDGE_TYPE:
            case CREATE_VERTEX_TYPE:
                return ontologyInLpgGraphStore == null
                    ? operationRecord
                    : convert2AlterSchemaOperation(
                        operationRecord,
                        getAddPropertyList(operationRecord, ontologyInLpgGraphStore, Collections.EMPTY_LIST),
                        Collections.EMPTY_LIST);
            case ALTER_EDGE_TYPE:
            case ALTER_VERTEX_TYPE:
                if (ontologyInLpgGraphStore == null) {
                    return convert2CreateSchemaOperation(operationRecord);
                } else {
                    List<String> dropPropertyNameList = getDropPropertyNameList(operationRecord,
                        ontologyInLpgGraphStore);
                    return convert2AlterSchemaOperation(
                        operationRecord,
                        getAddPropertyList(operationRecord, ontologyInLpgGraphStore, dropPropertyNameList),
                        dropPropertyNameList
                    );
                }
            default:
                throw GraphStoreException.unexpectedVertexEdgeTypeOperationEnum(operationRecord.getOperationTypeEnum());
        }
    }

    private List<String> getDropPropertyNameList(
        BaseLPGSchemaOperation operationRecord, BaseLPGOntology ontologyInLpgGraphStore) {

        List<BaseSchemaAtomicOperation> operations;
        switch (operationRecord.getOperationTypeEnum()) {
            case ALTER_EDGE_TYPE:
            case ALTER_VERTEX_TYPE:
                operations = operationRecord.getAtomicOperations();
                break;
            case CREATE_EDGE_TYPE:
            case CREATE_VERTEX_TYPE:
            case DROP_EDGE_TYPE:
            case DROP_VERTEX_TYPE:
                return Collections.EMPTY_LIST;
            default:
                throw GraphStoreException.unexpectedVertexEdgeTypeOperationEnum(operationRecord.getOperationTypeEnum());
        }

        return operations.stream()
            .filter(Objects::nonNull)
            .filter(operation ->
                SchemaAtomicOperationEnum.DROP_PROPERTY.equals(operation.getOperationTypeEnum()))
            .map(operation -> (DropPropertyOperation) operation)
            .filter(dropPropertyOperation ->
                ontologyInLpgGraphStore.isWithProperty(dropPropertyOperation.getPropertyName()))
            .map(DropPropertyOperation::getPropertyName)
            .collect(Collectors.toList());
    }

    private List<LPGProperty> getAddPropertyList(
        BaseLPGSchemaOperation schemaOperation, BaseLPGOntology ontologyInLpgGraphStore,
        List<String> dropPropertyNameList) {

        List<BaseSchemaAtomicOperation> atomicOperations;

        switch (schemaOperation.getOperationTypeEnum()) {
            case ALTER_EDGE_TYPE:
            case ALTER_VERTEX_TYPE:
            case CREATE_EDGE_TYPE:
            case CREATE_VERTEX_TYPE:
                atomicOperations = schemaOperation.getAtomicOperations();
                break;
            case DROP_EDGE_TYPE:
            case DROP_VERTEX_TYPE:
                return Collections.EMPTY_LIST;
            default:
                throw GraphStoreException.unexpectedVertexEdgeTypeOperationEnum(schemaOperation.getOperationTypeEnum());
        }
        return atomicOperations.stream()
            .filter(Objects::nonNull)
            .filter(operation ->
                SchemaAtomicOperationEnum.ADD_PROPERTY.equals(operation.getOperationTypeEnum()))
            .map(operation -> (AddPropertyOperation) operation)
            .filter(addPropertyOperation ->
                (!ontologyInLpgGraphStore.isWithProperty(
                    addPropertyOperation.getProperty().getName(),
                    addPropertyOperation.getProperty().getType()))
                    || (dropPropertyNameList.contains(addPropertyOperation.getProperty().getName())))
            .map(AddPropertyOperation::getProperty)
            .collect(Collectors.toList());
    }

    private BaseCreateTypeOperation convert2CreateSchemaOperation(BaseLPGSchemaOperation operationRecord) {
        switch (operationRecord.getOperationTypeEnum()) {
            case ALTER_EDGE_TYPE:
                AlterEdgeTypeOperation alterEdgeTypeOperation = (AlterEdgeTypeOperation) operationRecord;
                return new CreateEdgeTypeOperation(
                    alterEdgeTypeOperation.getEdgeTypeName(),
                    alterEdgeTypeOperation.getAtomicOperations());
            case ALTER_VERTEX_TYPE:
                AlterVertexTypeOperation alterVertexTypeOperation = (AlterVertexTypeOperation) operationRecord;
                return new CreateVertexTypeOperation(
                    alterVertexTypeOperation.getVertexTypeName(),
                    alterVertexTypeOperation.getAtomicOperations());
            default:
                throw GraphStoreException.unexpectedVertexEdgeTypeOperationEnum(operationRecord.getOperationTypeEnum());
        }
    }

    private BaseLPGSchemaOperation convert2AlterSchemaOperation(
        BaseLPGSchemaOperation operationRecord, List<LPGProperty> lpgPropertyToAdd,
        List<String> lpgPropertyNameToDrop) {
        if (CollectionUtils.isEmpty(lpgPropertyToAdd) && CollectionUtils.isEmpty(lpgPropertyNameToDrop)) {
            return null;
        }
        switch (operationRecord.getOperationTypeEnum()) {
            case CREATE_EDGE_TYPE:
            case ALTER_EDGE_TYPE:
                AlterEdgeTypeOperation alterEdgeTypeOperation = new AlterEdgeTypeOperation(
                    EdgeTypeName.parse(operationRecord.getTargetTypeName()));
                lpgPropertyToAdd.forEach(alterEdgeTypeOperation::addProperty);
                lpgPropertyNameToDrop.forEach(alterEdgeTypeOperation::dropProperty);
                return alterEdgeTypeOperation;
            case CREATE_VERTEX_TYPE:
            case ALTER_VERTEX_TYPE:
                AlterVertexTypeOperation alterVertexTypeOperation = new AlterVertexTypeOperation(
                    operationRecord.getTargetTypeName());
                lpgPropertyToAdd.forEach(alterVertexTypeOperation::addProperty);
                lpgPropertyNameToDrop.forEach(alterVertexTypeOperation::dropProperty);
                return alterVertexTypeOperation;
            default:
                throw GraphStoreException.unexpectedVertexEdgeTypeOperationEnum(operationRecord.getOperationTypeEnum());
        }
    }
}

