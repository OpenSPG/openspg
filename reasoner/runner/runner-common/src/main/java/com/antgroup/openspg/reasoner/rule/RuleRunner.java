/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.rule.op.OperatorEqualsLessMore;
import com.antgroup.openspg.reasoner.rule.op.OperatorIn;
import com.antgroup.openspg.reasoner.rule.op.OperatorLike;
import com.antgroup.openspg.reasoner.rule.op.OperatorMultiDiv;
import com.antgroup.openspg.reasoner.rule.udf.UdfWrapper;
import com.antgroup.openspg.reasoner.udf.UdfMng;
import com.antgroup.openspg.reasoner.udf.UdfMngFactory;
import com.antgroup.openspg.reasoner.udf.model.RuntimeUdfMeta;
import com.antgroup.openspg.reasoner.udf.model.UdfOperatorTypeEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import lombok.extern.slf4j.Slf4j;
import scala.Tuple2;


@Slf4j(topic = "userlogger")
public class RuleRunner {

    private static Cache<String, Map<String, Object>> contextCache = CacheBuilder.newBuilder()
            .maximumSize(100).expireAfterWrite(24, TimeUnit.HOURS).build();

    private final ExpressRunner                      EXPRESS_RUNNER = new ExpressRunner();

    /**
     * set running context
     * @param taskId
     * @param context
     */
    public void putRuleRunningContext(String taskId, Map<String, Object> context) {
        contextCache.put(taskId, context);
    }

    /**
     * get running context by id
     * @param taskId
     * @return
     */
    public Map<String, Object> getRuleRunningContext(String taskId) {
        Map<String, Object>  result =  contextCache.getIfPresent(taskId);
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
            } catch (Exception e) {
                if (Utils.randomLog()) {
                    log.warn("RuleRunner error, rule=" + rule + ",ctx=" + JSON.toJSONString(context), e);
                }
                return false;
            }
        }
        return true;
    }

    public Object executeExpression(Map<String, Object> context, List<String> expressionList,
                                    String taskId) {
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

    private RuleRunner() {
    }

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
    }

    /**
     * register all udfs
     */
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
                new Tuple2<String, Operator>("<", new OperatorEqualsLessMore("<"))
                , new Tuple2<String, Operator>(">", new OperatorEqualsLessMore(">"))
                , new Tuple2<String, Operator>("<=", new OperatorEqualsLessMore("<="))
                , new Tuple2<String, Operator>(">=", new OperatorEqualsLessMore(">="))
                , new Tuple2<String, Operator>("==", new OperatorEqualsLessMore("=="))
                , new Tuple2<String, Operator>("!=", new OperatorEqualsLessMore("!="))
                , new Tuple2<String, Operator>("<>", new OperatorEqualsLessMore("<>"))

                , new Tuple2<String, Operator>("*", new OperatorMultiDiv("*"))
                , new Tuple2<String, Operator>("/", new OperatorMultiDiv("/"))
                , new Tuple2<String, Operator>("%", new OperatorMultiDiv("%"))
                , new Tuple2<String, Operator>("mod", new OperatorMultiDiv("mod"))

                , new Tuple2<String, Operator>("like", new OperatorLike("like"))
                , new Tuple2<String, Operator>("in", new OperatorIn("in"))
        ).forEach(udfTuple -> EXPRESS_RUNNER.replaceOperator(udfTuple._1(), udfTuple._2()));
    }

}