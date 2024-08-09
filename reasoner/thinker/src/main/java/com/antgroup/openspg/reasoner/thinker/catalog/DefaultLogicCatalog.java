package com.antgroup.openspg.reasoner.thinker.catalog;

import com.antgroup.openspg.reasoner.lube.catalog.AbstractConnection;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

import java.util.ArrayList;
import java.util.List;

public class DefaultLogicCatalog extends LogicCatalog {
    private Catalog kgCatalog;
    private List<Rule> rules;

    private DefaultLogicCatalog() {
        rules = new ArrayList<>();
    }

    public DefaultLogicCatalog(List<Rule> rules, Catalog kgCatalog) {
        this.rules = rules;
        this.kgCatalog = kgCatalog;
    }

    @Override
    public LogicNetwork loadLogicNetwork() {
        LogicNetwork logicNetwork = new LogicNetwork();
        for (Rule r : rules) {
            logicNetwork.addRule(r);
        }
        return logicNetwork;
    }

    @Override
    public SemanticPropertyGraph getKnowledgeGraph() {
        return kgCatalog.getKnowledgeGraph();
    }

    @Override
    public Map<AbstractConnection, Set<String>> getConnections() {
        return kgCatalog.getConnections();
    }

    @Override
    public Set<Field> getDefaultNodeProperties() {
        return kgCatalog.getDefaultNodeProperties();
    }

    @Override
    public Set<Field> getDefaultEdgeProperties() {
        return kgCatalog.getDefaultEdgeProperties();
    }
}
