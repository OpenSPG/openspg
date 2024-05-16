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

package com.antgroup.openspg.reasoner.udf.rule;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.udf.UdfMng;
import com.antgroup.openspg.reasoner.udf.UdfMngFactory;
import com.antgroup.openspg.reasoner.udf.model.RuntimeUdfMeta;
import com.antgroup.openspg.reasoner.udf.model.UdfOperatorTypeEnum;
import com.antgroup.openspg.reasoner.udf.rule.op.*;
import com.antgroup.openspg.reasoner.udf.rule.udf.UdfWrapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.instruction.detail.Instruction;
import com.ql.util.express.instruction.detail.InstructionConstData;
import com.ql.util.express.instruction.detail.InstructionLoadAttr;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.parse.KeyWordDefine4Java;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

public class RuleRunner {
  private static final Logger log = LoggerFactory.getLogger(RuleRunner.class);

  private static final Cache<String, Map<String, Object>> contextCache =
      CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(24, TimeUnit.HOURS).build();

  private final ExpressRunner EXPRESS_RUNNER = new ExpressRunner();

  private static final Set<String> keywordSet = new HashSet<>();

  static {
    KeyWordDefine4Java keyWordDefine4Java = new KeyWordDefine4Java();
    keywordSet.addAll(Arrays.asList(keyWordDefine4Java.keyWords));
  }

  private static final String CONFLICT_KEY_PREFIX = "__ConflictKey_";
  /** convert vertex or edge property name to prevent keyword conflicts */
  public static String convertPropertyName(String propertyName) {
    if (keywordSet.contains(propertyName)) {
      return CONFLICT_KEY_PREFIX + propertyName;
    }
    return propertyName;
  }

  /** recover property name */
  public static String recoverPropertyName(String propertyName) {
    if (!propertyName.startsWith(CONFLICT_KEY_PREFIX)) {
      return propertyName;
    }
    return propertyName.substring(CONFLICT_KEY_PREFIX.length());
  }

  public static boolean isConflictPropertyName(String propertyName) {
    return propertyName.startsWith(CONFLICT_KEY_PREFIX);
  }

  /**
   * set running context
   *
   * @param taskId
   * @param context
   */
  public void putRuleRunningContext(String taskId, Map<String, Object> context) {
    contextCache.put(taskId, context);
  }

  /**
   * get running context by id
   *
   * @param taskId
   * @return
   */
  public Map<String, Object> getRuleRunningContext(String taskId) {
    Map<String, Object> result = contextCache.getIfPresent(taskId);
    if (result == null) {
      result = new HashMap<>();
    }
    return result;
  }
  /**
   * last rule as filter
   *
   * @param context
   * @param ruleList
   * @param taskId
   * @return
   */
  public boolean check(Map<String, Object> context, List<String> ruleList, String taskId) {
    DefaultContext<String, Object> ctx = new DefaultContext<>();
    ctx.putAll(context);
    ctx.putAll(getRuleRunningContext(taskId));
    for (int i = 0; i < ruleList.size(); ++i) {
      String rule = ruleList.get(i);
      try {
        Object tmpRet = EXPRESS_RUNNER.execute(rule, ctx, null, true, false);
        if ((1 + i) == ruleList.size()) {
          return (Boolean) tmpRet;
        }

      } catch (QLCompileException e) {
        log.warn("RuleRunner error, rule=" + rule + ",ctx=" + JSON.toJSONString(context), e);
      } catch (Exception e) {
        if (Utils.randomLog()) {
          log.warn("RuleRunner error, rule=" + rule + ",ctx=" + JSON.toJSONString(context), e);
        }
        return false;
      }
    }
    return true;
  }

  public Object executeExpression(
      Map<String, Object> context, List<String> expressionList, String taskId) {
    DefaultContext<String, Object> ctx = new DefaultContext<>();
    ctx.putAll(context);
    ctx.putAll(getRuleRunningContext(taskId));
    for (int i = 0; i < expressionList.size(); ++i) {
      String rule = expressionList.get(i);
      try {
        Object rst = EXPRESS_RUNNER.execute(rule, ctx, null, true, false);
        if ((1 + i) == expressionList.size()) {
          return rst;
        }
      } catch (Exception e) {
        log.warn("RuleRunner error, rule=" + rule + ",ctx=" + JSON.toJSONString(context), e);
        return null;
      }
    }
    return null;
  }

  public Map<String, Set<String>> getParamNames(String rule) throws Exception {
    InstructionSet instructionSet = EXPRESS_RUNNER.getInstructionSetFromLocalCache(rule);
    Map<String, Set<String>> result = new TreeMap<>();
    for (int i = 0; i < instructionSet.getInstructionLength(); i++) {
      Instruction instruction = instructionSet.getInstruction(i);
      if (instruction instanceof InstructionLoadAttr) {
        if ("null".equals(((InstructionLoadAttr) instruction).getAttrName())) {
          continue;
        }
        String aliasName = ((InstructionLoadAttr) instruction).getAttrName();
        Set<String> varSet = result.computeIfAbsent(aliasName, (k) -> new HashSet<>());
        // LoadAttr之后就是具体的属性名
        Instruction next = instructionSet.getInstruction(i + 1);
        if (next instanceof InstructionOperator) {
          String opName = ((InstructionOperator) next).getOperator().getName();
          if ("FieldCall".equalsIgnoreCase(opName)) {
            String varName = ((InstructionOperator) next).getOperator().toString().split(":")[1];
            varSet.add(varName);
          }
        }
      }
    }

    for (int i = 0; i < instructionSet.getInstructionLength(); i++) {
      Instruction instruction = instructionSet.getInstruction(i);
      if (instruction instanceof InstructionOperator) {
        String opName = ((InstructionOperator) instruction).getOperator().getName();
        if (opName != null) {
          if (opName.equalsIgnoreCase("def") || opName.equalsIgnoreCase("exportDef")) {
            if (i >= 1) {
              String varLocalName =
                  (String)
                      ((InstructionConstData) instructionSet.getInstruction(i - 1))
                          .getOperateData()
                          .getObject(null);
              result.remove(varLocalName);
            }
          } else if (opName.equalsIgnoreCase("alias") || opName.equalsIgnoreCase("exportAlias")) {
            if (i >= 2) {
              String varLocalName =
                  (String)
                      ((InstructionConstData) instructionSet.getInstruction(i - 2))
                          .getOperateData()
                          .getObject(null);
              result.remove(varLocalName);
            }
          }
        }
      }
    }
    return result;
  }

  private RuleRunner() {}

  private static volatile RuleRunner instance = null;

  public static RuleRunner getInstance() {
    if (null != instance) {
      return instance;
    }
    synchronized (RuleRunner.class) {
      if (null == instance) {
        RuleRunner runner = new RuleRunner();
        runner.init();
        instance = runner;
      }
    }
    return instance;
  }

  private void init() {
    // disable print error
    // InstructionSet.printInstructionError = false;
    // use short circuit
    EXPRESS_RUNNER.setShortCircuit(true);
    registerUdf();
    overrideOperator();
    EXPRESS_RUNNER.addFunction("get_value", new OperatorGetValue());
    EXPRESS_RUNNER.addFunction("get_spo", new OperatorGetSPO());
  }

  /** register all udfs */
  private void registerUdf() {
    UdfMng udfMng = UdfMngFactory.getUdfMng();
    List<RuntimeUdfMeta> runtimeUdfMetaList = udfMng.getAllRuntimeUdfMeta();
    for (RuntimeUdfMeta runtimeUdfMeta : runtimeUdfMetaList) {
      try {
        if (UdfOperatorTypeEnum.OPERATOR.equals(runtimeUdfMeta.getUdfType())) {
          log.debug("EXPRESS_RUNNER.addOperator,name=" + runtimeUdfMeta.getName());
          EXPRESS_RUNNER.addOperator(runtimeUdfMeta.getName(), new UdfWrapper(runtimeUdfMeta));
        } else {
          log.debug("EXPRESS_RUNNER.addFunction,name=" + runtimeUdfMeta.getName());
          EXPRESS_RUNNER.addFunction(runtimeUdfMeta.getName(), new UdfWrapper(runtimeUdfMeta));
        }
      } catch (Exception e) {
        if (e.getMessage().contains("重复定义操作符")) {
          log.warn("rule runner replace operator, name=" + runtimeUdfMeta.getName());
          EXPRESS_RUNNER.replaceOperator(runtimeUdfMeta.getName(), new UdfWrapper(runtimeUdfMeta));
          continue;
        }
        throw new RuntimeException(e);
      }
    }
  }

  private void overrideOperator() {
    Lists.newArrayList(
            new Tuple2<String, Operator>("<", new OperatorEqualsLessMore("<")),
            new Tuple2<String, Operator>(">", new OperatorEqualsLessMore(">")),
            new Tuple2<String, Operator>("<=", new OperatorEqualsLessMore("<=")),
            new Tuple2<String, Operator>(">=", new OperatorEqualsLessMore(">=")),
            new Tuple2<String, Operator>("==", new OperatorEqualsLessMore("==")),
            new Tuple2<String, Operator>("!=", new OperatorEqualsLessMore("!=")),
            new Tuple2<String, Operator>("<>", new OperatorEqualsLessMore("<>")),
            new Tuple2<String, Operator>("*", new OperatorMultiDiv("*")),
            new Tuple2<String, Operator>("/", new OperatorMultiDiv("/")),
            new Tuple2<String, Operator>("%", new OperatorMultiDiv("%")),
            new Tuple2<String, Operator>("mod", new OperatorMultiDiv("mod")),
            new Tuple2<String, Operator>("like", new OperatorLike("like")),
            new Tuple2<String, Operator>("in", new OperatorIn("in")))
        .forEach(udfTuple -> EXPRESS_RUNNER.replaceOperator(udfTuple._1(), udfTuple._2()));
  }
}
