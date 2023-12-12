package com.antgroup.openspg.reasoner.warehouse.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig;
import org.apache.commons.lang3.NotImplementedException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class AbstractGraphLoader implements GraphLoader {
    /**
     * graph loader config
     */
    protected final GraphLoaderConfig graphLoaderConfig;

    /**
     * loader
     */
    public AbstractGraphLoader(GraphLoaderConfig graphLoaderConfig) {
        this.graphLoaderConfig = graphLoaderConfig;
        log.info("graphLoaderConfig," + this.graphLoaderConfig);
    }

    /**
     * recall one hot graph from data source
     * @param vertexId
     * @return
     */
    public VertexSubGraph queryOneHotGraphState(IVertexId vertexId) {
        throw new NotImplementedException("not support queryOneHotGraphState");
    }

    /**
     * get graph loader config
     * @return
     */
    public GraphLoaderConfig getGraphLoaderConfig() {
        return graphLoaderConfig;
    }
}
