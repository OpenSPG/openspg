package com.antgroup.openspgapp.biz.schema.model;

import com.antgroup.openspg.core.schema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.schema.model.constraint.Constraint;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.constraint.EnumConstraint;
import com.antgroup.openspg.core.schema.model.constraint.MultiValConstraint;
import com.antgroup.openspg.core.schema.model.constraint.NotNullConstraint;
import com.antgroup.openspg.core.schema.model.constraint.RegularConstraint;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.core.schema.service.type.model.BuiltInPropertyEnum;
import com.antgroup.openspgapp.common.util.enums.AdvancedTypeEnum;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/SchemaScriptTranslateUtil.class */
public class SchemaScriptTranslateUtil {
  private static final String PROP = "PROP";
  private static final String EDGE = "EDGE";
  private static final String RULE = "RULE";
  private static final List<String> RULE_KEYWORDS =
      Lists.newArrayList(new String[] {"DEFINE", "STRUCTURE", "CONSTRAINT"});

  public static String translateSchema(SchemaModel schemaModel) {
    StringBuilder sb = new StringBuilder();
    sb.append("namespace ").append(schemaModel.getNamespace()).append("\n\n");
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
          .append("\n");
      setDeep(1, sb);
      if (StringUtils.isNotBlank(nodeTypeModel.getDesc())) {
        sb.append("desc:").append(nodeTypeModel.getDesc()).append("\n");
        setDeep(1, sb);
      }
      if (AdvancedTypeEnum.CONCEPT_TYPE.equals(advancedTypeEnum)
          && StringUtils.isNotBlank(nodeTypeModel.getHypernymPredicate())) {
        sb.append("hypernymPredicate: ")
            .append(nodeTypeModel.getHypernymPredicate())
            .append("\n")
            .append("\n");
      } else {
        translateProperties(schemaModel.getNamespace(), nodeTypeModel, sb);
        translateRelations(schemaModel.getNamespace(), nodeTypeModel, sb);
        sb.append("\n");
      }
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
          edgeTypeModel.getSemanticRelation() != null
              && edgeTypeModel.getSemanticRelation().booleanValue();
      if (!semanticRelation) {
        setDeep(2, relationSb);
        relationSb
            .append(edgeTypeModel.getName())
            .append("(")
            .append(edgeTypeModel.getNameZh())
            .append("): ")
            .append(edgeTypeModel.getTargetType())
            .append("\n");
        if (CollectionUtils.isNotEmpty(edgeTypeModel.getProperties())) {
          setDeep(3, relationSb);
          relationSb.append("properties:").append("\n");
          for (PropertyModel property : edgeTypeModel.getProperties()) {
            if (property.getInherited() == null || !property.getInherited().booleanValue()) {
              setDeep(4, relationSb);
              relationSb
                  .append(property.getName())
                  .append("(")
                  .append(property.getNameZh())
                  .append("): ")
                  .append(property.getType())
                  .append("\n");
              if (StringUtils.isNotEmpty(property.getIndex())) {
                setDeep(5, relationSb);
                relationSb.append("index:").append(property.getIndex()).append("\n");
              }
            }
          }
        }
        if (edgeTypeModel.getRule() != null
            && StringUtils.isNotBlank(edgeTypeModel.getRule().getContent())) {
          translateRule(namespace, edgeTypeModel.getRule().getContent(), relationSb);
        }
      }
    }
    if (relationSb.length() > 0) {
      setDeep(1, sb);
      sb.append("relations:").append("\n").append((CharSequence) relationSb);
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
      if (property.getInherited() == null || !property.getInherited().booleanValue()) {
        if (!builtInPropertyNames.contains(property.getName())) {
          setDeep(2, propertySb);
          propertySb
              .append(property.getName())
              .append("(")
              .append(property.getNameZh())
              .append("): ")
              .append(property.getType())
              .append("\n");
          if (StringUtils.isNotEmpty(property.getIndex())) {
            setDeep(3, propertySb);
            propertySb
                .append("index:")
                .append(IndexTypeEnum.toEnum(property.getIndex()).getScriptName())
                .append("\n");
          }
          if (property.getConstraint() != null
              && CollectionUtils.isNotEmpty(property.getConstraint().getConstraintItems())) {
            setDeep(3, propertySb);
            propertySb.append("constraint:");
            List<BaseConstraintItem> constraintItems =
                property.getConstraint().getConstraintItems();
            StringBuilder constraintBuilder = new StringBuilder();
            for (int i = 0; i < constraintItems.size(); i++) {
              ConstraintTypeEnum constraintTypeEnum =
                  constraintItems.get(i).getConstraintTypeEnum();
              switch (AnonymousClass1
                  .$SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                  constraintTypeEnum.ordinal()]) {
                case 1:
                  appendIfNotEmpty(constraintBuilder, ",").append("MultiValue");
                  break;
                case 2:
                  EnumConstraint enumConstraint = (EnumConstraint) constraintItems.get(i);
                  appendIfNotEmpty(constraintBuilder, ",")
                      .append("Enum=")
                      .append("\"")
                      .append(Joiner.on(",").join(enumConstraint.getEnumValues()))
                      .append("\"");
                  break;
                case 3:
                  appendIfNotEmpty(constraintBuilder, ",").append("Unique");
                  break;
                case 4:
                  appendIfNotEmpty(constraintBuilder, ",").append("NotNull");
                  break;
              }
            }
            propertySb.append((CharSequence) constraintBuilder);
            propertySb.append("\n");
          }
          if (property.getRule() != null
              && StringUtils.isNotBlank(property.getRule().getContent())) {
            translateRule(namespace, property.getRule().getContent(), propertySb);
          }
        }
      }
    }
    if (propertySb.length() > 0) {
      sb.append("properties:").append("\n").append((CharSequence) propertySb);
    }
  }

  /* renamed from: com.antgroup.openspgapp.biz.schema.model.SchemaScriptTranslateUtil$1, reason: invalid class name */
  /* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/SchemaScriptTranslateUtil$1.class */
  static /* synthetic */ class AnonymousClass1 {
    static final /* synthetic */ int[]
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum =
            new int[ConstraintTypeEnum.values().length];

    static {
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.MULTI_VALUE.ordinal()] =
            1;
      } catch (NoSuchFieldError e) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.ENUM.ordinal()] =
            2;
      } catch (NoSuchFieldError e2) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.UNIQUE.ordinal()] =
            3;
      } catch (NoSuchFieldError e3) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.NOT_NULL.ordinal()] =
            4;
      } catch (NoSuchFieldError e4) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.RANGE.ordinal()] =
            5;
      } catch (NoSuchFieldError e5) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.REGULAR.ordinal()] =
            6;
      } catch (NoSuchFieldError e6) {
      }
    }
  }

  private static void translateRule(String namespace, String rule, StringBuilder sb) {
    setDeep(3, sb);
    String ruleScript = rule.replace(String.format("%s.", namespace), "");
    sb.append("rule: [[").append("\n");
    if (ruleScript.contains("\n")) {
      List<String> ruleLines = Splitter.on("\n").omitEmptyStrings().splitToList(ruleScript);
      for (int i = 0; i < ruleLines.size(); i++) {
        String ruleLine = ruleLines.get(i);
        if (indentCount(ruleLine) < 16) {
          setDeep(startWithKeyWords(ruleLine) ? 4 : 5, sb);
        }
        sb.append(ruleLine).append("\n");
      }
    } else {
      setDeep(4, sb);
      sb.append(ruleScript).append("\n");
    }
    setDeep(4, sb);
    sb.append("]]").append("\n");
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

  private static String completeRule(String namespace, String rule) {
    Pattern pattern = Pattern.compile("\\(([\\w\\s]*?:)(`?[\\w\\s.]+`?)/?[^)]*?\\)", 2);
    Matcher matcher = pattern.matcher(rule);
    while (matcher.find()) {
      String group0 = matcher.group(0);
      String group1 = matcher.group(1);
      String group = matcher.group(2);
      if (!group.contains(".") && !group.toLowerCase().matches("(integer|text|float)")) {
        String nodeName = "(" + group1 + namespace + "." + group + ")";
        rule = rule.replace(group0, nodeName);
      }
    }
    return rule.trim();
  }

  private static void setDeep(int deep, StringBuilder sb) {
    for (int i = 0; i < deep; i++) {
      sb.append("\t");
    }
  }

  public static SchemaModel translateScript(String script) {
    SchemaModel schemaModel = new SchemaModel();
    List<String> lines = Splitter.on("\n").omitEmptyStrings().splitToList(script.trim());
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
    int i = 1;
    while (i < lines.size()) {
      String line = lines.get(i);
      if (!StringUtils.isBlank(line) && !line.startsWith("#")) {
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
          NodeTypeModel nodeTypeModel2 = toNodeTypeModel(namespace, typeList);
          nodeTypeList.add(nodeTypeModel2);
        }
      }
      i++;
    }
    schemaModel.setNodeTypeModels(nodeTypeList);
    return schemaModel;
  }

  private static NodeTypeModel toNodeTypeModel(String namespace, List<String> typeLineList) {
    NodeTypeModel nodeTypeModel = new NodeTypeModel();
    String first = typeLineList.get(0);
    MutableTriple<String, String, String> type = getType(first);
    nodeTypeModel.setName((String) type.getLeft());
    nodeTypeModel.setNameZh((String) type.getMiddle());
    nodeTypeModel.setType(AdvancedTypeEnum.toEnumByCode((String) type.getRight()).name());
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
      } else {
        if (PROP.equals(currentStep) && spaceCount >= currentLintCount) {
          currentStep = EDGE;
        }
        if (line.trim().startsWith("rule:")) {
          spaceCount = currentLintCount;
          currentStep = RULE;
          relationRuleStr = Lists.newArrayList();
          relationRuleMap.put(last, relationRuleStr);
          relationRuleStr.add(line.trim().replace("rule:", ""));
        } else {
          if (RULE.equals(currentStep) && spaceCount >= currentLintCount) {
            currentStep = EDGE;
          }
          if (PROP.equals(currentStep)) {
            propStrLine.add(line.replaceAll("\t", ""));
          } else if (RULE.equals(currentStep)) {
            relationRuleStr.add(line.trim());
          } else if (line.contains("):")) {
            MutableTriple<String, String, String> propType = getType(line);
            EdgeTypeModel edgeTypeModel = new EdgeTypeModel();
            edgeTypeModel.setName((String) propType.getLeft());
            edgeTypeModel.setNameZh((String) propType.getMiddle());
            edgeTypeModel.setTargetType((String) propType.getRight());
            relations.add(edgeTypeModel);
            last = edgeTypeModel;
          }
        }
      }
    }
    for (Map.Entry<EdgeTypeModel, List<String>> entry : relationPropMap.entrySet()) {
      if (!CollectionUtils.isEmpty(entry.getValue())) {
        EdgeTypeModel edgeTypeModel2 = entry.getKey();
        List<String> value = entry.getValue();
        List<PropertyModel> propertyModelList = getPropertyModelList(namespace, value);
        edgeTypeModel2.setProperties(propertyModelList);
      }
    }
    for (Map.Entry<EdgeTypeModel, List<String>> entry2 : relationRuleMap.entrySet()) {
      if (!CollectionUtils.isEmpty(entry2.getValue())) {
        EdgeTypeModel edgeTypeModel3 = entry2.getKey();
        List<String> value2 = entry2.getValue();
        String rule = completeRule(namespace, Joiner.on("\n").join(value2).trim());
        if (rule.startsWith("[[")) {
          rule = rule.replace("[[", "");
        }
        if (rule.endsWith("]]")) {
          rule = rule.replace("]]", "");
        }
        edgeTypeModel3.setRule(new LogicalRule((RuleCode) null, (String) null, rule));
      }
    }
    return relations;
  }

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
      } else {
        if (PROP.equals(currentStep) && spaceCount >= currentLintCount) {
          currentStep = null;
        }
        if (line.trim().startsWith("rule:")) {
          spaceCount = currentLintCount;
          currentStep = RULE;
          ruleStrList = Lists.newArrayList();
          porpRuleMap.put(last, ruleStrList);
          ruleStrList.add(line.trim().replace("rule:", ""));
        } else {
          if (RULE.equals(currentStep) && spaceCount >= currentLintCount) {
            currentStep = EDGE;
          }
          if (PROP.equals(currentStep)) {
            propStrLine.add(line.replaceAll("\t", ""));
          } else if (RULE.equals(currentStep)) {
            ruleStrList.add(line.trim());
          } else if (line.contains("):")) {
            MutableTriple<String, String, String> propType = getType(line);
            PropertyModel propertyModel = new PropertyModel();
            propertyModel.setName((String) propType.getLeft());
            propertyModel.setNameZh((String) propType.getMiddle());
            propertyModel.setType((String) propType.getRight());
            last = propertyModel;
            result.add(propertyModel);
          }
          if (line.contains("index:") && last != null) {
            String scriptName = (String) Splitter.on(":").trimResults().splitToList(line).get(1);
            last.setIndex(IndexTypeEnum.getByScriptName(scriptName).name());
          }
          if (line.contains("constraint:") && last != null) {
            String constraintStr = (String) Splitter.on(":").trimResults().splitToList(line).get(1);
            Constraint constraint = new Constraint();
            List<BaseConstraintItem> list = new ArrayList<>();
            parseConstraintForProperty(constraintStr, list);
            constraint.setConstraintItems(list);
            last.setConstraint(constraint);
          }
        }
      }
    }
    for (Map.Entry<PropertyModel, List<String>> entry : subPropMap.entrySet()) {
      if (!CollectionUtils.isEmpty(entry.getValue())) {
        PropertyModel edgeTypeModel = entry.getKey();
        List<String> value = entry.getValue();
        List<PropertyModel> propertyModelList = getPropertyModelList(namespace, value);
        edgeTypeModel.setSubProperties(propertyModelList);
      }
    }
    for (Map.Entry<PropertyModel, List<String>> entry2 : porpRuleMap.entrySet()) {
      if (!CollectionUtils.isEmpty(entry2.getValue())) {
        PropertyModel propertyModel2 = entry2.getKey();
        List<String> value2 = entry2.getValue();
        String rule = completeRule(namespace, Joiner.on("\n").join(value2).trim());
        if (rule.startsWith("[[")) {
          rule = rule.replace("[[", "");
        }
        if (rule.endsWith("]]")) {
          rule = rule.replace("]]", "");
        }
        propertyModel2.setRule(new LogicalRule((RuleCode) null, (String) null, rule));
      }
    }
    return result;
  }

  private static void parseConstraintForProperty(
      String expression, List<BaseConstraintItem> constraintItems) {
    if (StringUtils.isBlank(expression)) {
      return;
    }
    Pattern pattern = Pattern.compile("(Enum|Regular)\\s*?=\\s*?\"([^\"]+)\"", 2);
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
    for (String str : array) {
      String cons = str.trim();
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
      } else if ("relations:".equals(line.trim())) {
        spaceCount = currentLintCount;
        currentStep = EDGE;
      } else if (line.trim().startsWith("hypernymPredicate:")) {
        spaceCount = currentLintCount;
        currentStep = null;
        String constraintStr = (String) Splitter.on(":").trimResults().splitToList(line).get(1);
        nodeTypeModel.setHypernymPredicate(constraintStr);
      } else if (line.trim().startsWith("desc:")) {
        spaceCount = currentLintCount;
        currentStep = null;
        String constraintStr2 = (String) Splitter.on(":").trimResults().splitToList(line).get(1);
        nodeTypeModel.setDesc(constraintStr2);
      } else {
        if (PROP.equals(currentStep) && spaceCount < currentLintCount) {
          propStrList.add(line);
        }
        if (EDGE.equals(currentStep) && spaceCount < indentCount(line)) {
          relationsStrList.add(line);
        }
      }
    }
  }

  private static int indentCount(String input) {
    int count = 0;
    for (char c : input.toCharArray()) {
      if (c == '\t') {
        count += 4;
      } else {
        if (c != ' ') {
          break;
        }
        count++;
      }
    }
    return count;
  }

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
