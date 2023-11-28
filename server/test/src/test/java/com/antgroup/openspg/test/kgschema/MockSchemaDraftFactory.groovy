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


package com.antgroup.openspg.test.kgschema

import com.antgroup.openspg.common.util.StringUtils
import com.antgroup.openspg.schema.model.BasicInfo
import com.antgroup.openspg.schema.model.alter.AlterOperationEnum
import com.antgroup.openspg.schema.model.alter.SchemaDraft
import com.antgroup.openspg.schema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.schema.model.identifier.SPGTripleIdentifier
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.schema.model.semantic.LogicalRule
import com.antgroup.openspg.schema.model.semantic.PredicateSemantic
import com.antgroup.openspg.schema.model.semantic.SystemPredicateEnum
import com.google.common.collect.Lists

import java.util.stream.Collectors

class MockSchemaDraftFactory {

    /**
     namespace STD
     AlipayId(支付宝账号): StandardType
     spreadable(是否可传播): true
     constraintItems(约束条件): [RegularConstraint]

     namespace DEFAULT
     Company(公司): EntityType
     Device(设备): EntityType
     App(应用小程序): EntityType
     Goods(商品): EntityType
     Administration(行政区划): ConceptType
     conceptLayerConfig:
     hypernymPredicate: locateAt
     layerNames: ["国","省","市", "区"]

     MemberDegree(会员等级): ConceptType
     conceptLayerConfig:
     hypernymPredicate: isA
     conceptMultiVersionConfig:
     pattern: yyyyMMdd
     maxVersion: 3
     ttl: 30

     TaxonomyOfPerson(人群): ConceptType
     conceptLayerConfig:
     hypernymPredicate: isA
     conceptTaxonomicConfig:
     taxonomicTypeUniqueName: Person

     Person(自然人): EntityType
     properties:
     age(年龄): Integer
     constraint: RangeConstraint(0, +)
     mobile(联系方式): STD.ChinaMobile
     home(家庭住址): Administration
     workCompany(就职公司): Company
     subProperties:
     confidence(置信度): Text
     relations:
     parent(父母): Person
     children(子女): Person
     semantics: inverseOf parent
     closeRel(近亲): Person
     logicalRule: "Define (s:DEFAULT.Person)-[p:closeRel]->(o:DEFAULT.Person) {\n" +
     "    GraphStructure {\n" +
     "        (s)-[p1:parent]->(o)\n" +
     "        (s)-[p2:children]->(o)\n" +
     "    }\n" +
     "    Rule {\n" +
     "        \n" +
     "    }\n" +
     "}"

     AlipayUser(支付宝用户): EntityType
     parent: Person
     properties:
     regTime(注册时间): STD.Timestamp
     regMobile(注册手机号): STD.ChinaMobile
     regAddress(注册地址): Administration
     regDevice(注册设备): Device
     relations:
     collectApp(收藏的小程序): App

     AlipayMember(支付宝会员): EntityType
     parent: AlipayUser
     properties:
     degree(会员等级): MemberDegree
     mark(信誉分): Integer
     logicalRule: "Define (s:DEFAULT.AlipyMember)-[p:mark]->(o:Integer)
     { GraphStructure {} Rule { o=100}
     }"

     ExchangeGoods(兑换商品事件): EventType
     eventTime(发生时间): STD.Timestamp
     subject(主体): AlipayUser, AlipayMember
     object(客体): Goods
     */
    static SchemaDraft buildCreateDraft() {
        SchemaDraft schemaDraft = new SchemaDraft(alterSpgTypes: new ArrayList<BaseAdvancedType>())
        addAlipayId(schemaDraft)
        addCompany(schemaDraft)
        addDevice(schemaDraft)
        addApp(schemaDraft)
        addGoods(schemaDraft)
        addAdministration(schemaDraft)
        addMemberDegree(schemaDraft)
        addTaxonomyOfPerson(schemaDraft)
        addPerson(schemaDraft)
        addAlipayUser(schemaDraft)
        addAlipayMember(schemaDraft)
        addExchangeGoods(schemaDraft)
        addSubExchangeGoods(schemaDraft)
        return schemaDraft
    }

    /**
     * mock schema update draft, delete a new concept type, update a entity type by adding a property,
     * update a property by adding a sub property, and delete a relation, finally add an event type.
     * @param projectSchema
     * @return
     */
    static SchemaDraft buildUpdateDraft(ProjectSchema projectSchema) {
        SchemaDraft schemaDraft = new SchemaDraft(alterSpgTypes: new ArrayList<BaseAdvancedType>())
        deleteTaxonomyOfPerson(schemaDraft, projectSchema)
        updateAlipayUser(schemaDraft, projectSchema)
        updatePerson(schemaDraft, projectSchema)
        addTaobaoMember(schemaDraft)
        return schemaDraft
    }

    /**
     * mock schema delete draft, delete all customized spg type.
     * @param projectSchema
     * @return
     */
    static SchemaDraft buildDeleteDraft(ProjectSchema projectSchema) {
        SchemaDraft schemaDraft = new SchemaDraft(alterSpgTypes: new ArrayList<BaseAdvancedType>())
        Set<String> spgTypeNames = MockSpgTypeNameEnum.getCustomizedType()
                .toList().stream()
                .map(e -> e.getName())
                .collect(Collectors.toSet())
        for (BaseSPGType spgType : projectSchema.getSpgTypes()) {
            if (spgTypeNames.contains(spgType.getName())) {
                spgType.setAlterOperation(AlterOperationEnum.DELETE)
                schemaDraft.getAlterSpgTypes().add(spgType as BaseAdvancedType)
            }
        }
        return schemaDraft
    }

    static void addAlipayId(SchemaDraft schemaDraft) {
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
                new SPGTypeIdentifier("STD", "AlipayId"),
                "支付宝账号", "支付宝账号")
        Boolean spreadable = true
        List<BaseConstraintItem> constraintItems = Lists.newArrayList(
                new RegularConstraint("2088[0-9]+"))
        StandardType standardType = new StandardType(basicInfo,
                null, null, null,
                null, spreadable, constraintItems)
        standardType.setAlterOperation(AlterOperationEnum.CREATE)
        schemaDraft.getAlterSpgTypes().add(standardType)
    }

    static void addCompany(SchemaDraft schemaDraft) {
        SPGTypeRef companyRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.Company"), "公司", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        EntityType company = newEntityType(companyRef.getBasicInfo(),
                null, null, null)
        schemaDraft.getAlterSpgTypes().add(company)
    }

    static void addDevice(SchemaDraft schemaDraft) {
        SPGTypeRef deviceRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.Device"), "设备", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        EntityType device = newEntityType(deviceRef.getBasicInfo(),
                null, null, null)
        schemaDraft.getAlterSpgTypes().add(device)
    }

    static void addApp(SchemaDraft schemaDraft) {
        SPGTypeRef deviceRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.App"), "应用小程序", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        EntityType device = newEntityType(deviceRef.getBasicInfo(),
                null, null, null)
        schemaDraft.getAlterSpgTypes().add(device)
    }

    static void addGoods(SchemaDraft schemaDraft) {
        SPGTypeRef goodsRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.Goods"), "商品", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        EntityType goods = newEntityType(goodsRef.getBasicInfo(),
                null, null, null)
        schemaDraft.getAlterSpgTypes().add(goods)
    }

    static void addAdministration(SchemaDraft schemaDraft) {
        SPGTypeRef areaRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.Administration"), "行政区划", "desc"),
                SPGTypeEnum.CONCEPT_TYPE)
        ConceptType area = newConceptType(areaRef.getBasicInfo(),
                null, new ConceptLayerConfig("locateAt", ["国", "省", "市", "区"]),
                null, null, null, null)
        schemaDraft.getAlterSpgTypes().add(area)
    }

    static void addMemberDegree(SchemaDraft schemaDraft) {
        SPGTypeRef memberDegreeRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.MemberDegree"), "会员等级", "desc"),
                SPGTypeEnum.CONCEPT_TYPE)
        ConceptType memberDegree = newConceptType(memberDegreeRef.getBasicInfo(),
                null, new ConceptLayerConfig("isA", null),
                null, new MultiVersionConfig("yyyyMMdd", 1, 3),
                null, null)
        schemaDraft.getAlterSpgTypes().add(memberDegree)
    }

    static void addTaxonomyOfPerson(SchemaDraft schemaDraft) {
        SPGTypeRef taxonomyOfPersonRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.TaxonomyOfPerson"), "人群", "desc"),
                SPGTypeEnum.CONCEPT_TYPE)
        ConceptType taxonomyOfPerson = newConceptType(taxonomyOfPersonRef.getBasicInfo(),
                null, new ConceptLayerConfig("isA", null),
                new ConceptTaxonomicConfig(SPGTypeIdentifier.parse("DEFAULT.Person")),
                null, null, null)
        schemaDraft.getAlterSpgTypes().add(taxonomyOfPerson)
    }

    static void addPerson(SchemaDraft schemaDraft) {
        SPGTypeRef personRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.Person"), "自然人", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        List<Property> personProperties = mockPersonProperty(personRef)
        List<Relation> personRelations = mockPersonRelation(personRef)
        EntityType personType = newEntityType(personRef.getBasicInfo(), "Thing",
                personProperties, personRelations)
        schemaDraft.getAlterSpgTypes().add(personType)
    }

    static void addAlipayUser(SchemaDraft schemaDraft) {
        SPGTypeRef alipayUserRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.AlipayUser"), "支付宝用户", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        List<Property> alipayUserProperties = mockAlipayUserProperties(alipayUserRef)
        List<Relation> alipayUserRelations = mockAlipayUserRelation(alipayUserRef)
        EntityType alipayUser = newEntityType(alipayUserRef.getBasicInfo(), "DEFAULT.Person",
                alipayUserProperties, alipayUserRelations)
        schemaDraft.getAlterSpgTypes().add(alipayUser)
    }

    static void addAlipayMember(SchemaDraft schemaDraft) {
        SPGTypeRef alipayMemberRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.AlipayMember"), "支付宝会员", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        List<Property> alipayMemberProperties = mockAlipayMemberProperty(alipayMemberRef)
        EntityType alipayMember = newEntityType(alipayMemberRef.getBasicInfo(), "DEFAULT.AlipayUser",
                alipayMemberProperties, null)
        schemaDraft.getAlterSpgTypes().add(alipayMember)
    }


    static void addTaobaoMember(SchemaDraft schemaDraft) {
        SPGTypeRef taobaoMemberRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.TaobaoMember"), "淘宝会员", "desc"),
                SPGTypeEnum.ENTITY_TYPE)
        EntityType taobaoMember = newEntityType(taobaoMemberRef.getBasicInfo(), null,
                null, null)
        schemaDraft.getAlterSpgTypes().add(taobaoMember)
    }

    static void addSubExchangeGoods(SchemaDraft schemaDraft) {
        SPGTypeRef exchangeRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.SubExchangeGoods"),
                "兑换事件", "desc"),
                SPGTypeEnum.EVENT_TYPE)
        //List<Property> exchangeProperties = mockExchangeProperty(exchangeRef)
        EventType exchange = newEventType(exchangeRef.getBasicInfo(),
                "DEFAULT.ExchangeGoods",
                null, null)
        schemaDraft.getAlterSpgTypes().add(exchange)
    }

    static void addExchangeGoods(SchemaDraft schemaDraft) {
        SPGTypeRef exchangeRef = new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier
                .parse("DEFAULT.ExchangeGoods"),
                "兑换事件", "desc"),
                SPGTypeEnum.EVENT_TYPE)
        List<Property> exchangeProperties = mockExchangeProperty(exchangeRef)
        EventType exchange = newEventType(exchangeRef.getBasicInfo(), null,
                exchangeProperties, null)
        schemaDraft.getAlterSpgTypes().add(exchange)
    }

    private static EntityType newEntityType(BasicInfo<SPGTypeIdentifier> basicInfo,
                                            String parent,
                                            List<Property> properties,
                                            List<Relation> relations) {
        ParentTypeInfo parentTypeInfo = StringUtils.isBlank(parent)
                ? null : new ParentTypeInfo(null,
                null, SPGTypeIdentifier.parse(parent), null)
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setLinkOperator(new OperatorKey("linkOp", 2))

        EntityType entityType = new EntityType(basicInfo, parentTypeInfo,
                properties, relations, advancedConfig)
        entityType.setAlterOperation(AlterOperationEnum.CREATE)
        return entityType
    }

    private static ConceptType newConceptType(BasicInfo<SPGTypeIdentifier> basicInfo,
                                              String parent,
                                              ConceptLayerConfig conceptLayerConfig,
                                              ConceptTaxonomicConfig conceptTaxonomicConfig,
                                              MultiVersionConfig multiVersionConfig,
                                              List<Property> properties,
                                              List<Relation> relations) {
        ParentTypeInfo parentTypeInfo = StringUtils.isBlank(parent)
                ? null : new ParentTypeInfo(null,
                null, SPGTypeIdentifier.parse(parent), null)
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setNormalizedOperator(new OperatorKey("normalizedOp", 1))

        ConceptType conceptTypeDTO = new ConceptType(basicInfo, parentTypeInfo,
                properties, relations,
                advancedConfig, conceptLayerConfig,
                conceptTaxonomicConfig, multiVersionConfig)
        conceptTypeDTO.setAlterOperation(AlterOperationEnum.CREATE)
        return conceptTypeDTO
    }

    private static EventType newEventType(BasicInfo<SPGTypeIdentifier> basicInfo,
                                          String parent,
                                          List<Property> properties,
                                          List<Relation> relations) {
        ParentTypeInfo parentTypeInfo = StringUtils.isBlank(parent)
                ? null : new ParentTypeInfo(null,
                null, SPGTypeIdentifier.parse(parent), null)
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig()
        advancedConfig.setLinkOperator(new OperatorKey("linkOp", 1))
        advancedConfig.setExtractOperator(new OperatorKey("extractOp", 1))

        EventType eventTypeDTO = new EventType(basicInfo, parentTypeInfo,
                properties, relations, advancedConfig)
        eventTypeDTO.setAlterOperation(AlterOperationEnum.CREATE)
        return eventTypeDTO
    }

    static List<Property> mockPersonProperty(SPGTypeRef personRef) {
        List<Property> personProperties = Lists.newArrayList()
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
                new PredicateIdentifier("age"), "年龄", "age desc")
        SPGTypeRef intRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("Integer"))),
                SPGTypeEnum.BASIC_TYPE)

        Constraint ageConstraint = new Constraint()
        RangeConstraint rangeConstraintDTO = new RangeConstraint()
        rangeConstraintDTO.setMinimumValue("0")
        rangeConstraintDTO.setLeftOpen(true)
        ageConstraint.setConstraintItems(Lists.newArrayList(rangeConstraintDTO))
        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig()
        advancedConfig.setConstraint(ageConstraint)

        Property ageProperty = new Property(basicInfo, personRef,
                intRef, false, advancedConfig)
        personProperties.add(ageProperty)

        BasicInfo<PredicateIdentifier> mobileInfo = new BasicInfo<>(
                new PredicateIdentifier("mobile"),
                "手机号", "mobile desc")
        SPGTypeRef mobileRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("STD.ChinaMobile"))),
                SPGTypeEnum.STANDARD_TYPE)

        Property mobileProperty = new Property(mobileInfo, personRef,
                mobileRef, false, new PropertyAdvancedConfig())
        personProperties.add(mobileProperty)

        BasicInfo<PredicateIdentifier> homeInfo = new BasicInfo<>(
                new PredicateIdentifier("home"),
                "家乡", "home desc")
        SPGTypeRef areaRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.Administration"))),
                SPGTypeEnum.CONCEPT_TYPE)
        Property homeProperty = new Property(homeInfo, personRef,
                areaRef, false, new PropertyAdvancedConfig())
        personProperties.add(homeProperty)

        BasicInfo<PredicateIdentifier> workCompanyInfo = new BasicInfo<>(
                new PredicateIdentifier("workCompany"),
                "就职", "workCompany desc")
        SPGTypeRef companyRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.Company"))),
                SPGTypeEnum.ENTITY_TYPE)
        Property workCompanyProperty = new Property(workCompanyInfo, personRef,
                companyRef, false, new PropertyAdvancedConfig())

        BasicInfo<PredicateIdentifier> conf = new BasicInfo<>(new PredicateIdentifier("confidence"),
                "置信度", "confidence desc")
        SPGTypeRef textRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("Text"))), SPGTypeEnum.BASIC_TYPE)
        SubProperty workComSub = new SubProperty(conf, workCompanyProperty.toRef(),
                textRef, new PropertyAdvancedConfig())
        Constraint workConfConstraint = new Constraint()
        workConfConstraint.getConstraintItems().add(new NotNullConstraint())
        workComSub.getAdvancedConfig().setConstraint(workConfConstraint)

        workCompanyProperty.getAdvancedConfig().setSubProperties([workComSub])
        personProperties.add(workCompanyProperty)

        return personProperties
    }

    static List<Relation> mockPersonRelation(SPGTypeRef personRef) {
        BasicInfo<PredicateIdentifier> parentInfo = new BasicInfo<>(
                new PredicateIdentifier("parent"),
                "父母", "parent desc")
        Relation parentRelation = new Relation(parentInfo, personRef,
                personRef, false, new PropertyAdvancedConfig())

        BasicInfo<PredicateIdentifier> childrenInfo = new BasicInfo<>(
                new PredicateIdentifier("children"),
                "子女", "children desc")
        Relation childrenRelation = new Relation(childrenInfo, personRef,
                personRef, false, new PropertyAdvancedConfig())
        PredicateSemantic semantic = new PredicateSemantic(
                childrenRelation.toRef(),
                new PredicateIdentifier(SystemPredicateEnum.INVERSE_OF.getName()),
                parentRelation.toRef())
        childrenRelation.getAdvancedConfig().setSemantics([semantic])

        BasicInfo<PredicateIdentifier> closeRelInfo = new BasicInfo<>(
                new PredicateIdentifier("closeRel"),
                "近亲", "closeRel desc")
        Relation closeRelRelation = new Relation(closeRelInfo, personRef,
                personRef, false, new PropertyAdvancedConfig())
        closeRelRelation.getAdvancedConfig().setLogicalRule(new LogicalRule(null, null,
                "Define (s:DEFAULT.Person)-[p:closeRel]->(o:DEFAULT.Person) {\n" +
                        "    GraphStructure {\n" +
                        "        (s)-[p1:parent]->(o)\n" +
                        "        (s)-[p2:children]->(o)\n" +
                        "    }\n" +
                        "    Rule {\n" +
                        "        \n" +
                        "    }\n" +
                        "}"))
        return [parentRelation, childrenRelation, closeRelRelation]
    }

    static List<Property> mockAlipayUserProperties(SPGTypeRef alipayUserRef) {
        BasicInfo<PredicateIdentifier> regTimeInfo = new BasicInfo<>(
                new PredicateIdentifier("regTime"),
                "注册时间", "regTime desc")
        SPGTypeRef timestampRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("STD.Timestamp"))),
                SPGTypeEnum.STANDARD_TYPE)
        Property regTimeProperty = new Property(regTimeInfo, alipayUserRef,
                timestampRef, false, new PropertyAdvancedConfig())

        BasicInfo<PredicateIdentifier> regMobileInfo = new BasicInfo<>(
                new PredicateIdentifier("regMobile"),
                "注册手机号", "regMobile desc")
        SPGTypeRef mobileRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("STD.ChinaMobile"))),
                SPGTypeEnum.STANDARD_TYPE)
        Property regMobileProperty = new Property(regMobileInfo, alipayUserRef,
                mobileRef, false, new PropertyAdvancedConfig())

        BasicInfo<PredicateIdentifier> regAddrInfo = new BasicInfo<>(
                new PredicateIdentifier("regAddress"),
                "注册地址", "regAddress desc")
        SPGTypeRef areaRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.Administration"))),
                SPGTypeEnum.CONCEPT_TYPE)
        Property regAddrProperty = new Property(regAddrInfo, alipayUserRef,
                areaRef, false, new PropertyAdvancedConfig())

        BasicInfo<PredicateIdentifier> regDeviceInfo = new BasicInfo<>(
                new PredicateIdentifier("regDevice"),
                "注册设备", "regDevice desc")
        SPGTypeRef regDeviceRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.Device"))),
                SPGTypeEnum.ENTITY_TYPE)
        Property regDeviceProperty = new Property(regDeviceInfo, alipayUserRef,
                regDeviceRef, false, new PropertyAdvancedConfig())

        return [regTimeProperty, regMobileProperty, regAddrProperty, regDeviceProperty]
    }

    static List<Relation> mockAlipayUserRelation(SPGTypeRef alipayUserRef) {
        BasicInfo<PredicateIdentifier> collectAppInfo = new BasicInfo<>(
                new PredicateIdentifier("collectApp"),
                "收藏小程序", "collectApp desc")
        SPGTypeRef appRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.App"))),
                SPGTypeEnum.ENTITY_TYPE)

        Relation collectionApp = new Relation(collectAppInfo, alipayUserRef,
                appRef, false, new PropertyAdvancedConfig(), false)
        return [collectionApp]
    }

    static List<Property> mockAlipayMemberProperty(SPGTypeRef alipayMemberRef) {
        BasicInfo<PredicateIdentifier> degreeInfo = new BasicInfo<>(
                new PredicateIdentifier("degree"),
                "等级", "degree desc")
        SPGTypeRef memberDegreeRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.MemberDegree"))),
                SPGTypeEnum.CONCEPT_TYPE)
        Property degreeProperty = new Property(degreeInfo, alipayMemberRef,
                memberDegreeRef, false, new PropertyAdvancedConfig())

        BasicInfo<PredicateIdentifier> markInfo = new BasicInfo<>(
                new PredicateIdentifier("mark"),
                "积分", "mark desc")
        SPGTypeRef intRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("Integer"))),
                SPGTypeEnum.BASIC_TYPE)
        Property markProperty = new Property(markInfo, alipayMemberRef,
                intRef, false, new PropertyAdvancedConfig())

        LogicalRule logicalRule = new LogicalRule(null, null,
                "Define (s:DEFAULT.AlipayMember)-[p:mark]->(o:Integer) {" +
                        " GraphStructure {} Rule { o=100}}")
        markProperty.getAdvancedConfig().setLogicalRule(logicalRule)
        return [degreeProperty, markProperty]
    }

    static List<Property> mockExchangeProperty(SPGTypeRef exchangeRef) {
        BasicInfo<PredicateIdentifier> subjectInfo = new BasicInfo<>(
                new PredicateIdentifier("subject"),
                "主体", "subject desc")
        SPGTypeRef textRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("Text"))),
                SPGTypeEnum.BASIC_TYPE)
        PropertyAdvancedConfig subAdvancedConfig = new PropertyAdvancedConfig()
        subAdvancedConfig.setPropertyGroup(PropertyGroupEnum.SUBJECT)
        Property subjectProperty = new Property(subjectInfo, exchangeRef,
                textRef, false, subAdvancedConfig)

        BasicInfo<PredicateIdentifier> subjectAlipayUserInfo = new BasicInfo<>(
                new PredicateIdentifier("subjectAlipayUser"),
                "主体", "subject desc")
        SPGTypeRef alipayUserRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.AlipayUser"))),
                SPGTypeEnum.ENTITY_TYPE)
        Property subjectAlipayUserProperty = new Property(
                subjectAlipayUserInfo, exchangeRef, alipayUserRef,
                false, subAdvancedConfig)

        BasicInfo<PredicateIdentifier> subjectPerson = new BasicInfo<>(
                new PredicateIdentifier("subjectPerson"),
                "主体", "subject desc")
        SPGTypeRef personRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.Person"))),
                SPGTypeEnum.ENTITY_TYPE)
        Property subject2Property = new Property(subjectPerson, exchangeRef,
                personRef, false, subAdvancedConfig)

        BasicInfo<PredicateIdentifier> objInfo = new BasicInfo<>(
                new PredicateIdentifier("object"),
                "客体", "object desc")
        SPGTypeRef goodsRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("DEFAULT.Goods"))),
                SPGTypeEnum.ENTITY_TYPE)
        PropertyAdvancedConfig objAdvancedConfig = new PropertyAdvancedConfig()
        objAdvancedConfig.setPropertyGroup(PropertyGroupEnum.OBJECT)
        Property objProperty = new Property(objInfo, exchangeRef,
                goodsRef, false, objAdvancedConfig)

        return [subjectProperty, subjectAlipayUserProperty, subject2Property, objProperty]
    }

    static deleteTaxonomyOfPerson(SchemaDraft schemaDraft, ProjectSchema projectSchema) {
        BaseSPGType taxonomyOfPerson = getSpgType(projectSchema,
                MockSpgTypeNameEnum.DEFAULT_TAXOMOMY_OF_PERSON.name)
        taxonomyOfPerson.setAlterOperation(AlterOperationEnum.DELETE)
        schemaDraft.getAlterSpgTypes().add((BaseAdvancedType) taxonomyOfPerson)
    }

    static updateAlipayUser(SchemaDraft schemaDraft, ProjectSchema projectSchema) {
        BaseSPGType alipayUser = getSpgType(projectSchema, MockSpgTypeNameEnum.DEFAULT_ALIPAY_USER.name)
        alipayUser.getBasicInfo().setNameZh("支付宝新用户")

        BasicInfo<PredicateIdentifier> lastVisitTimeInfo = new BasicInfo<>(
                new PredicateIdentifier("lastVisit"),
                "最近访问时间", "lastVisit desc")
        SPGTypeRef timestampRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("STD.Timestamp"))),
                SPGTypeEnum.STANDARD_TYPE)
        Property lastVisitProperty = new Property(lastVisitTimeInfo, alipayUser.toRef(),
                timestampRef, false, new PropertyAdvancedConfig())
        lastVisitProperty.setAlterOperation(AlterOperationEnum.CREATE)
        alipayUser.getProperties().add(lastVisitProperty)

        SPGTripleIdentifier mobileTripleName = new SPGTripleIdentifier(alipayUser.getBaseSpgIdentifier(),
                new PredicateIdentifier("regMobile"),
                SPGTypeIdentifier.parse(MockSpgTypeNameEnum.STD_MOBILE.name))
        Property mobileProp = alipayUser.getPropertyByName(mobileTripleName)

        BasicInfo<PredicateIdentifier> conf = new BasicInfo<>(new PredicateIdentifier("confidence"),
                "置信度", "confidence desc")
        SPGTypeRef textRef = new SPGTypeRef(new BasicInfo<>(
                (SPGTypeIdentifier.parse("Text"))), SPGTypeEnum.BASIC_TYPE)
        SubProperty mobileSub = new SubProperty(conf, mobileProp.toRef(),
                textRef, new PropertyAdvancedConfig())
        mobileSub.setAlterOperation(AlterOperationEnum.CREATE)
        mobileProp.getAdvancedConfig().getSubProperties().add(mobileSub)
        mobileProp.setAlterOperation(AlterOperationEnum.UPDATE)

        SPGTripleIdentifier collectAppTripleName = new SPGTripleIdentifier(alipayUser.getBaseSpgIdentifier(),
                new PredicateIdentifier("collectApp"),
                SPGTypeIdentifier.parse(MockSpgTypeNameEnum.DEFAULT_APP.name))
        Relation collectApp = alipayUser.getRelationByName(collectAppTripleName)
        collectApp.setAlterOperation(AlterOperationEnum.DELETE)

        alipayUser.setAlterOperation(AlterOperationEnum.UPDATE)
        schemaDraft.getAlterSpgTypes().add((BaseAdvancedType) alipayUser)
    }

    static void updatePerson(SchemaDraft schemaDraft, ProjectSchema projectSchema) {
        BaseSPGType person = getSpgType(projectSchema, MockSpgTypeNameEnum.DEFAULT_PERSON.name)
        SPGTripleIdentifier closeRelTripleName = new SPGTripleIdentifier(person.getBaseSpgIdentifier(),
                new PredicateIdentifier("closeRel"),
                SPGTypeIdentifier.parse(MockSpgTypeNameEnum.DEFAULT_PERSON.name))
        Relation closeRel = person.getRelationByName(closeRelTripleName)
        closeRel.getAdvancedConfig().getLogicalRule().setContent(
                "Define (s:DEFAULT.Person)-[p:closeRel]->(o:DEFAULT.Person) {\n" +
                        "    GraphStructure {\n" +
                        "        (s)-[p1:parent]->(o)\n" +
                        "    }\n" +
                        "    Rule {\n" +
                        "        \n" +
                        "    }\n" +
                        "}")
        closeRel.setAlterOperation(AlterOperationEnum.UPDATE)

        SPGTripleIdentifier ageTripleName = new SPGTripleIdentifier(person.getBaseSpgIdentifier(),
                new PredicateIdentifier("age"),
                SPGTypeIdentifier.parse("Integer"))
        Property age = person.getPropertyByName(ageTripleName)
        age.setAlterOperation(AlterOperationEnum.UPDATE)
        age.getAdvancedConfig().setConstraint(null)

        person.setAlterOperation(AlterOperationEnum.UPDATE)
        schemaDraft.getAlterSpgTypes().add((BaseAdvancedType) person)
    }

    static BaseSPGType getSpgType(ProjectSchema projectSchema, String spgTypeName) {
        List<BaseSPGType> spgTypes = projectSchema.getSpgTypes()
        for (BaseSPGType spgType : spgTypes) {
            if (spgType.getName() == spgTypeName) {
                return spgType
            }
        }
        return null
    }
}
