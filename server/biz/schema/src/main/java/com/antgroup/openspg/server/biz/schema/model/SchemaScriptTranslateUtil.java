/*
 * Copyright 2023 OpenSPG Authors
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
package com.antgroup.openspg.server.biz.schema.model;

import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.common.util.enums.AdvancedTypeEnum;
import com.antgroup.openspg.core.schema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.schema.model.constraint.Constraint;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.constraint.EnumConstraint;
import com.antgroup.openspg.core.schema.model.constraint.MultiValConstraint;
import com.antgroup.openspg.core.schema.model.constraint.NotNullConstraint;
import com.antgroup.openspg.core.schema.model.constraint.RegularConstraint;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.core.schema.service.type.model.BuiltInPropertyEnum;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;

/** schema script translate util */
public class SchemaScriptTranslateUtil {

  private static final String PROP = "PROP";
  private static final String EDGE = "EDGE";
  private static final String RULE = "RULE";
  private static final List<String> RULE_KEYWORDS =
      Lists.newArrayList("DEFINE", "STRUCTURE", "CONSTRAINT");

  /**
   * translate schema model to schema script
   *
   * @param schemaModel
   * @return
   */
  public static String translateSchema(SchemaModel schemaModel) {
    StringBuilder sb = new StringBuilder();
    sb.append("namespace ").append(schemaModel.getNamespace()).append(SpgAppConstant.EMPTY_LINE);
    if (CollectionUtils.isEmpty(schemaModel.getNodeTypeModels())) {
      return sb.toString();
    }
    for (NodeTypeModel nodeTypeModel : schemaModel.getNodeTypeModels()) {
      AdvancedTypeEnum advancedTypeEnum = AdvancedTypeEnum.valueOf(nodeTypeModel.getType());
      sb.append(nodeTypeModel.getName())
          .append("(")
          .append(nodeTypeModel.getNameZh())
          .append("): ")
          .append(advancedTypeEnum.getCode())
          .append(SpgAppConstant.LINE_SEPARATOR);
      if (StringUtils.isNotBlank(nodeTypeModel.getDesc())) {
        setDeep(1, sb);
        sb.append("desc:").append(nodeTypeModel.getDesc()).append(SpgAppConstant.LINE_SEPARATOR);
      }
      if (AdvancedTypeEnum.CONCEPT_TYPE.equals(advancedTypeEnum)
          && StringUtils.isNotBlank(nodeTypeModel.getHypernymPredicate())) {
        setDeep(1, sb);
        sb.append("hypernymPredicate: ")
            .append(nodeTypeModel.getHypernymPredicate())
            .append(SpgAppConstant.LINE_SEPARATOR)
            .append(SpgAppConstant.LINE_SEPARATOR);
        continue;
      }
      translateProperties(schemaModel.getNamespace(), nodeTypeModel, sb);
      translateRelations(schemaModel.getNamespace(), nodeTypeModel, sb);
      sb.append(SpgAppConstant.LINE_SEPARATOR);
    }
    return sb.toString();
  }

  private static void translateRelations(
      String namespace, NodeTypeModel nodeTypeModel, StringBuilder sb) {
    if (CollectionUtils.isEmpty(nodeTypeModel.getRelations())) {
      return;
    }
    StringBuilder relationSb = new StringBuilder();
    for (EdgeTypeModel edgeTypeModel : nodeTypeModel.getRelations()) {
      boolean semanticRelation =
          edgeTypeModel.getSemanticRelation() != null && edgeTypeModel.getSemanticRelation();
      if (semanticRelation) {
        continue;
      }
      setDeep(2, relationSb);
      relationSb
          .append(edgeTypeModel.getName())
          .append("(")
          .append(edgeTypeModel.getNameZh())
          .append("): ")
          .append(edgeTypeModel.getTargetType())
          .append(SpgAppConstant.LINE_SEPARATOR);
      if (CollectionUtils.isNotEmpty(edgeTypeModel.getProperties())) {
        setDeep(3, relationSb);
        relationSb.append("properties:").append(SpgAppConstant.LINE_SEPARATOR);
        for (PropertyModel property : edgeTypeModel.getProperties()) {
          if (property.getInherited() != null && property.getInherited()) {
            continue;
          }
          setDeep(4, relationSb);
          relationSb
              .append(property.getName())
              .append("(")
              .append(property.getNameZh())
              .append("): ")
              .append(property.getType())
              .append(SpgAppConstant.LINE_SEPARATOR);
          if (StringUtils.isNotEmpty(property.getIndex())) {
            setDeep(5, relationSb);
            relationSb
                .append("index:")
                .append(property.getIndex())
                .append(SpgAppConstant.LINE_SEPARATOR);
          }
        }
      }
      if (edgeTypeModel.getRule() != null
          && StringUtils.isNotBlank(edgeTypeModel.getRule().getContent())) {
        translateRule(namespace, edgeTypeModel.getRule().getContent(), relationSb);
      }
    }
    if (relationSb.length() > 0) {
      setDeep(1, sb);
      sb.append("relations:").append(SpgAppConstant.LINE_SEPARATOR).append(relationSb);
    }
  }

  private static void translateProperties(
      String namespace, NodeTypeModel nodeTypeModel, StringBuilder sb) {
    if (CollectionUtils.isEmpty(nodeTypeModel.getProperties())) {
      return;
    }
    Set<String> builtInPropertyNames = new HashSet<>();
    AdvancedTypeEnum advancedTypeEnum = AdvancedTypeEnum.valueOf(nodeTypeModel.getType());
    if (AdvancedTypeEnum.EVENT_TYPE.equals(advancedTypeEnum)) {
      builtInPropertyNames = BuiltInPropertyEnum.getBuiltInPropertyName(SPGTypeEnum.EVENT_TYPE);
    }
    StringBuilder propertySb = new StringBuilder();
    for (PropertyModel property : nodeTypeModel.getProperties()) {
      if ((property.getInherited() != null && property.getInherited())
          || builtInPropertyNames.contains(property.getName())) {
        continue;
      }
      setDeep(2, propertySb);
      propertySb
          .append(property.getName())
          .append("(")
          .append(property.getNameZh())
          .append("): ")
          .append(property.getType())
          .append(SpgAppConstant.LINE_SEPARATOR);
      if (StringUtils.isNotEmpty(property.getIndex())) {
        setDeep(3, propertySb);
        propertySb
            .append("index:")
            .append(IndexTypeEnum.toEnum(property.getIndex()).getScriptName())
            .append(SpgAppConstant.LINE_SEPARATOR);
      }
      if (property.getConstraint() != null
          && CollectionUtils.isNotEmpty(property.getConstraint().getConstraintItems())) {
        setDeep(3, propertySb);
        propertySb.append("constraint:");
        List<BaseConstraintItem> constraintItems = property.getConstraint().getConstraintItems();
        StringBuilder constraintBuilder = new StringBuilder();
        for (int i = 0; i < constraintItems.size(); i++) {
          ConstraintTypeEnum constraintTypeEnum = constraintItems.get(i).getConstraintTypeEnum();
          switch (constraintTypeEnum) {
            case MULTI_VALUE:
              appendIfNotEmpty(constraintBuilder, ",").append("MultiValue");
              break;
            case ENUM:
              EnumConstraint enumConstraint = (EnumConstraint) constraintItems.get(i);
              appendIfNotEmpty(constraintBuilder, ",")
                  .append("Enum=")
                  .append("\"")
                  .append(Joiner.on(",").join(enumConstraint.getEnumValues()))
                  .append("\"");
              break;
            case UNIQUE:
              appendIfNotEmpty(constraintBuilder, ",").append("Unique");
              break;
            case NOT_NULL:
              appendIfNotEmpty(constraintBuilder, ",").append("NotNull");
              break;
            case RANGE:
            case REGULAR:
            default:
              break;
          }
        }
        propertySb.append(constraintBuilder);
        propertySb.append(SpgAppConstant.LINE_SEPARATOR);
      }
      if (property.getRule() != null && StringUtils.isNotBlank(property.getRule().getContent())) {
        translateRule(namespace, property.getRule().getContent(), propertySb);
      }
    }
    if (propertySb.length() > 0) {
      setDeep(1, sb);
      sb.append("properties:").append(SpgAppConstant.LINE_SEPARATOR).append(propertySb);
    }
  }

  private static void translateRule(String namespace, String rule, StringBuilder sb) {
    setDeep(3, sb);
    String ruleScript = rule.replace(String.format("%s.", namespace), "");
    sb.append("rule: [[").append(SpgAppConstant.LINE_SEPARATOR);
    if (ruleScript.contains(SpgAppConstant.LINE_SEPARATOR)) {
      List<String> ruleLines =
          Splitter.on(SpgAppConstant.LINE_SEPARATOR).omitEmptyStrings().splitToList(ruleScript);
      for (int i = 0; i < ruleLines.size(); i++) {
        String ruleLine = ruleLines.get(i);
        if (indentCount(ruleLine) < 16) {
          setDeep(startWithKeyWords(ruleLine) ? 4 : 5, sb);
        }
        sb.append(ruleLine).append(SpgAppConstant.LINE_SEPARATOR);
      }
    } else {
      setDeep(4, sb);
      sb.append(ruleScript).append(SpgAppConstant.LINE_SEPARATOR);
    }
    setDeep(4, sb);
    sb.append("]]").append(SpgAppConstant.LINE_SEPARATOR);
  }

  private static boolean startWithKeyWords(String ruleLine) {
    boolean result = false;
    for (String keyword : RULE_KEYWORDS) {
      result = ruleLine.trim().toUpperCase().startsWith(keyword);
      if (result) {
        return result;
      }
    }
    return result;
  }

  /**
   * complete rule nodeName
   *
   * @param namespace
   * @param rule
   * @return
   */
  private static String completeRule(String namespace, String rule) {
    Pattern pattern =
        Pattern.compile("\\(([\\w\\s]*?:)(`?[\\w\\s.]+`?)/?[^)]*?\\)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(rule);
    while (matcher.find()) {
      String group0 = matcher.group(0);
      String group1 = matcher.group(1);
      String group = matcher.group(2);
      if (group.contains(".") || group.toLowerCase().matches("(integer|text|float)")) {
        continue;
      }
      String nodeName = "(" + group1 + namespace + "." + group + ")";
      rule = rule.replace(group0, nodeName);
    }
    return rule.trim();
  }

  private static void setDeep(int deep, StringBuilder sb) {
    for (int i = 0; i < deep; i++) {
      sb.append(SpgAppConstant.TAB_SEPARATOR);
    }
  }

  /**
   * translate schema script to schema model
   *
   * @param script
   * @return
   */
  public static SchemaModel translateScript(String script) {
    SchemaModel schemaModel = new SchemaModel();
    List<String> lines =
        Splitter.on(SpgAppConstant.LINE_SEPARATOR).omitEmptyStrings().splitToList(script.trim());
    if (CollectionUtils.isEmpty(lines)) {
      throw new IllegalArgumentException("schema structure error");
    }
    String firstLine = lines.get(0);
    String namespace = null;
    if (firstLine.contains("namespace")) {
      namespace = firstLine.replace("namespace", "").trim();
    }
    if (StringUtils.isBlank(namespace)) {
      throw new IllegalArgumentException("schema miss namespace");
    }
    schemaModel.setNamespace(namespace);
    List<NodeTypeModel> nodeTypeList = Lists.newArrayList();
    List<String> typeList = null;
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (StringUtils.isBlank(line) || line.startsWith("#")) {
        continue;
      }
      int indentCount = indentCount(line);
      boolean lastLine = i == lines.size() - 1;
      if (indentCount == 0) {
        if (CollectionUtils.isNotEmpty(typeList)) {
          NodeTypeModel nodeTypeModel = toNodeTypeModel(namespace, typeList);
          nodeTypeList.add(nodeTypeModel);
        }
        typeList = Lists.newArrayList();
        typeList.add(line);
      } else {
        typeList.add(line);
      }
      if (lastLine && CollectionUtils.isNotEmpty(typeList)) {
        NodeTypeModel nodeTypeModel = toNodeTypeModel(namespace, typeList);
        nodeTypeList.add(nodeTypeModel);
      }
    }
    schemaModel.setNodeTypeModels(nodeTypeList);
    return schemaModel;
  }

  private static NodeTypeModel toNodeTypeModel(String namespace, List<String> typeLineList) {
    NodeTypeModel nodeTypeModel = new NodeTypeModel();
    String first = typeLineList.get(0);
    MutableTriple<String, String, String> type = getType(first);
    nodeTypeModel.setName(type.getLeft());
    nodeTypeModel.setNameZh(type.getMiddle());
    nodeTypeModel.setType(AdvancedTypeEnum.toEnumByCode(type.getRight()).name());
    if (typeLineList.size() > 1) {
      List<String> propStrList = Lists.newArrayList();
      List<String> relationsStrList = Lists.newArrayList();
      splitPopAndEdge(nodeTypeModel, typeLineList, propStrList, relationsStrList);
      List<PropertyModel> properties = getPropertyModelList(namespace, propStrList);
      List<EdgeTypeModel> relations = getRelationList(namespace, relationsStrList);
      nodeTypeModel.setProperties(properties);
      nodeTypeModel.setRelations(relations);
    }
    return nodeTypeModel;
  }

  private static List<EdgeTypeModel> getRelationList(
      String namespace, List<String> relationsStrList) {
    List<EdgeTypeModel> relations = new ArrayList<>();
    int spaceCount = 0;
    String currentStep = null;
    List<String> propStrLine = null;
    List<String> relationRuleStr = null;
    EdgeTypeModel last = null;
    Map<EdgeTypeModel, List<String>> relationPropMap = Maps.newHashMap();
    Map<EdgeTypeModel, List<String>> relationRuleMap = Maps.newHashMap();
    for (String line : relationsStrList) {
      int currentLintCount = indentCount(line);
      if ("properties:".equals(line.trim())) {
        spaceCount = currentLintCount;
        currentStep = PROP;
        propStrLine = Lists.newArrayList();
        relationPropMap.put(last, propStrLine);
        continue;
      }
      if (PROP.equals(currentStep) && spaceCount >= currentLintCount) {
        currentStep = EDGE;
      }
      if (line.trim().startsWith("rule:")) {
        spaceCount = currentLintCount;
        currentStep = RULE;
        relationRuleStr = Lists.newArrayList();
        relationRuleMap.put(last, relationRuleStr);
        relationRuleStr.add(line.trim().replace("rule:", ""));
        continue;
      }
      if (RULE.equals(currentStep) && spaceCount >= currentLintCount) {
        currentStep = EDGE;
      }

      if (PROP.equals(currentStep)) {
        propStrLine.add(line.replaceAll(SpgAppConstant.TAB_SEPARATOR, ""));
      } else if (RULE.equals(currentStep)) {
        relationRuleStr.add(line.trim());
      } else if (line.contains("):")) {
        MutableTriple<String, String, String> propType = getType(line);
        EdgeTypeModel edgeTypeModel = new EdgeTypeModel();
        edgeTypeModel.setName(propType.getLeft());
        edgeTypeModel.setNameZh(propType.getMiddle());
        edgeTypeModel.setTargetType(propType.getRight());
        relations.add(edgeTypeModel);
        last = edgeTypeModel;
      }
    }
    for (Entry<EdgeTypeModel, List<String>> entry : relationPropMap.entrySet()) {
      if (CollectionUtils.isEmpty(entry.getValue())) {
        continue;
      }
      EdgeTypeModel edgeTypeModel = entry.getKey();
      List<String> value = entry.getValue();
      List<PropertyModel> propertyModelList = getPropertyModelList(namespace, value);
      edgeTypeModel.setProperties(propertyModelList);
    }
    for (Entry<EdgeTypeModel, List<String>> entry : relationRuleMap.entrySet()) {
      if (CollectionUtils.isEmpty(entry.getValue())) {
        continue;
      }
      EdgeTypeModel edgeTypeModel = entry.getKey();
      List<String> value = entry.getValue();
      String rule = Joiner.on(SpgAppConstant.LINE_SEPARATOR).join(value).trim();
      rule = completeRule(namespace, rule);
      if (rule.startsWith("[[")) {
        rule = rule.replace("[[", "");
      }
      if (rule.endsWith("]]")) {
        rule = rule.replace("]]", "");
      }
      edgeTypeModel.setRule(new LogicalRule(null, null, rule));
    }
    return relations;
  }

  /**
   * get Node property
   *
   * @param propStrList
   * @return
   */
  private static List<PropertyModel> getPropertyModelList(
      String namespace, List<String> propStrList) {
    List<PropertyModel> result = new ArrayList<>();
    PropertyModel last = null;
    int spaceCount = 0;
    String currentStep = null;
    List<String> propStrLine = null;
    List<String> ruleStrList = null;
    Map<PropertyModel, List<String>> subPropMap = Maps.newHashMap();
    Map<PropertyModel, List<String>> porpRuleMap = Maps.newHashMap();
    for (String line : propStrList) {
      int currentLintCount = indentCount(line);
      if ("properties:".equals(line.trim())) {
        spaceCount = currentLintCount;
        currentStep = PROP;
        propStrLine = Lists.newArrayList();
        subPropMap.put(last, propStrLine);
        continue;
      }
      if (PROP.equals(currentStep) && spaceCount >= currentLintCount) {
        currentStep = null;
      }
      if (line.trim().startsWith("rule:")) {
        spaceCount = currentLintCount;
        currentStep = RULE;
        ruleStrList = Lists.newArrayList();
        porpRuleMap.put(last, ruleStrList);
        ruleStrList.add(line.trim().replace("rule:", ""));
        continue;
      }
      if (RULE.equals(currentStep) && spaceCount >= currentLintCount) {
        currentStep = EDGE;
      }

      if (PROP.equals(currentStep)) {
        propStrLine.add(line.replaceAll(SpgAppConstant.TAB_SEPARATOR, ""));
      } else if (RULE.equals(currentStep)) {
        ruleStrList.add(line.trim());
      } else if (line.contains("):")) {
        MutableTriple<String, String, String> propType = getType(line);
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setName(propType.getLeft());
        propertyModel.setNameZh(propType.getMiddle());
        propertyModel.setType(propType.getRight());
        last = propertyModel;
        result.add(propertyModel);
      }
      if (line.contains("index:") && last != null) {
        String scriptName = Splitter.on(":").trimResults().splitToList(line).get(1);
        last.setIndex(IndexTypeEnum.getByScriptName(scriptName).name());
      }
      if (line.contains("constraint:") && last != null) {
        String constraintStr = Splitter.on(":").trimResults().splitToList(line).get(1);
        Constraint constraint = new Constraint();
        List<BaseConstraintItem> list = new ArrayList<>();
        parseConstraintForProperty(constraintStr, list);
        constraint.setConstraintItems(list);
        last.setConstraint(constraint);
      }
    }
    for (Entry<PropertyModel, List<String>> entry : subPropMap.entrySet()) {
      if (CollectionUtils.isEmpty(entry.getValue())) {
        continue;
      }
      PropertyModel edgeTypeModel = entry.getKey();
      List<String> value = entry.getValue();
      List<PropertyModel> propertyModelList = getPropertyModelList(namespace, value);
      edgeTypeModel.setSubProperties(propertyModelList);
    }
    for (Entry<PropertyModel, List<String>> entry : porpRuleMap.entrySet()) {
      if (CollectionUtils.isEmpty(entry.getValue())) {
        continue;
      }
      PropertyModel propertyModel = entry.getKey();
      List<String> value = entry.getValue();
      String rule = Joiner.on(SpgAppConstant.LINE_SEPARATOR).join(value).trim();
      rule = completeRule(namespace, rule);
      if (rule.startsWith("[[")) {
        rule = rule.replace("[[", "");
      }
      if (rule.endsWith("]]")) {
        rule = rule.replace("]]", "");
      }
      propertyModel.setRule(new LogicalRule(null, null, rule));
    }
    return result;
  }

  /** parse constraint for property */
  private static void parseConstraintForProperty(
      String expression, List<BaseConstraintItem> constraintItems) {
    if (StringUtils.isBlank(expression)) {
      return;
    }
    Pattern pattern =
        Pattern.compile("(Enum|Regular)\\s*?=\\s*?\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(expression);
    while (matcher.find()) {
      String constraintType = matcher.group(1).toLowerCase();
      String constraintValue = matcher.group(2).trim();
      if ("enum".equals(constraintType)) {
        String[] enumValues = constraintValue.split(",");
        List<String> stripEnumValues = new ArrayList<>();
        for (String ev : enumValues) {
          stripEnumValues.add(ev.trim());
        }
        constraintItems.add(new EnumConstraint(stripEnumValues));
      } else if ("regular".equals(constraintType)) {
        constraintItems.add(new RegularConstraint(constraintValue));
      }
      expression = expression.replaceFirst(matcher.group(), "");
    }
    String[] array = expression.split(",");
    for (String cons : array) {
      cons = cons.trim();
      if ("multivalue".equalsIgnoreCase(cons)) {
        constraintItems.add(new MultiValConstraint());
      } else if ("notnull".equalsIgnoreCase(cons)) {
        constraintItems.add(new NotNullConstraint());
      }
    }
  }

  private static void splitPopAndEdge(
      NodeTypeModel nodeTypeModel,
      List<String> typeLineList,
      List<String> propStrList,
      List<String> relationsStrList) {
    int spaceCount = 0;
    String currentStep = null;
    for (int i = 1; i < typeLineList.size(); i++) {
      String line = typeLineList.get(i);
      int currentLintCount = indentCount(line);
      if ("properties:".equals(line.trim()) && currentStep == null) {
        spaceCount = currentLintCount;
        currentStep = PROP;
        continue;
      }
      if ("relations:".equals(line.trim())) {
        spaceCount = currentLintCount;
        currentStep = EDGE;
        continue;
      }
      if (line.trim().startsWith("hypernymPredicate:")) {
        spaceCount = currentLintCount;
        currentStep = null;
        String constraintStr = Splitter.on(":").trimResults().splitToList(line).get(1);
        nodeTypeModel.setHypernymPredicate(constraintStr);
        continue;
      }
      if (line.trim().startsWith("desc:")) {
        spaceCount = currentLintCount;
        currentStep = null;
        String constraintStr = Splitter.on(":").trimResults().splitToList(line).get(1);
        nodeTypeModel.setDesc(constraintStr);
        continue;
      }
      if (PROP.equals(currentStep) && spaceCount < currentLintCount) {
        propStrList.add(line);
      }
      if (EDGE.equals(currentStep) && spaceCount < indentCount(line)) {
        relationsStrList.add(line);
      }
    }
  }

  /**
   * Count the number of spaces at the beginning of a string. Tabs are assumed to be equivalent to 4
   * spaces by default.
   *
   * @param input
   * @return
   */
  private static int indentCount(String input) {
    int count = 0;
    for (char c : input.toCharArray()) {
      if (c == '\t') {
        count = count + 4;
      } else if (c == ' ') {
        count++;
      } else {
        break;
      }
    }
    return count;
  }

  /**
   * Appends the specified string to the given StringBuilder if it is not empty.
   *
   * @param builder The StringBuilder to append to.
   * @param str The string to append.
   */
  public static StringBuilder appendIfNotEmpty(StringBuilder builder, String str) {
    if (builder.length() > 0) {
      builder.append(str);
    }
    return builder;
  }

  private static MutableTriple<String, String, String> getType(String s) {
    List<String> list =
        Splitter.on(CharMatcher.anyOf("():")).omitEmptyStrings().trimResults().splitToList(s);
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    return MutableTriple.of(list.get(0), list.get(1), list.get(2));
  }
}
