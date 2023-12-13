/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.rule.RuleRunner;


public class PredicateKgGraph implements Predicate<KgGraph<IVertexId>> {

    private final Pattern             kgGraphSchema;
    private final Map<String, Object> initRuleContext;
    private final List<String>        ruleList;

    /**
     * implement a predicate for KgGraph
     */
    public PredicateKgGraph(Pattern kgGraphSchema, List<String> ruleList) {
        this.kgGraphSchema = kgGraphSchema;
        this.initRuleContext = RunnerUtil.getKgGraphInitContext(this.kgGraphSchema);
        this.ruleList = ruleList;
    }

    @Override
    public boolean test(KgGraph<IVertexId> kgGraph) {
        Map<String, Object> context = RunnerUtil.kgGraph2Context(this.initRuleContext, kgGraph);
        return RuleRunner.getInstance().check(context, ruleList, "");
    }
}