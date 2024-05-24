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

package com.antgroup.openspg.reasoner.thinker.qlexpress;

import com.antgroup.openspg.reasoner.thinker.qlexpress.op.RichOperatorEqualsLessMore;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.google.common.collect.Lists;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.Operator;
import com.ql.util.express.instruction.detail.Instruction;
import com.ql.util.express.instruction.detail.InstructionConstData;
import com.ql.util.express.instruction.detail.InstructionLoadAttr;
import com.ql.util.express.instruction.detail.InstructionOperator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import scala.Tuple2;

public class QlExpressRunner extends RuleRunner {
  private static volatile QlExpressRunner instance = null;

  private QlExpressRunner() {}

  public static QlExpressRunner getInstance() {
    if (null != instance) {
      return instance;
    }
    synchronized (QlExpressRunner.class) {
      QlExpressRunner runner = new QlExpressRunner();
      runner.init();
      instance = runner;
    }
    return instance;
  }

  @Override
  protected void init() {
    super.init();
    overrideOperator();
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

  @Override
  protected void overrideOperator() {
    Lists.newArrayList(
            new Tuple2<String, Operator>("<", new RichOperatorEqualsLessMore("<")),
            new Tuple2<String, Operator>(">", new RichOperatorEqualsLessMore(">")),
            new Tuple2<String, Operator>("<=", new RichOperatorEqualsLessMore("<=")),
            new Tuple2<String, Operator>(">=", new RichOperatorEqualsLessMore(">=")),
            new Tuple2<String, Operator>("==", new RichOperatorEqualsLessMore("==")),
            new Tuple2<String, Operator>("!=", new RichOperatorEqualsLessMore("!=")),
            new Tuple2<String, Operator>("<>", new RichOperatorEqualsLessMore("<>")))
        .forEach(udfTuple -> EXPRESS_RUNNER.replaceOperator(udfTuple._1(), udfTuple._2()));
  }
}
