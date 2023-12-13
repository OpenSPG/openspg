package com.antgroup.reasoner.session

import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.logical.RepeatPathVar
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface
import com.antgroup.openspg.reasoner.lube.physical.PropertyGraph
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig
import com.antgroup.openspg.reasoner.session.KGReasonerSession

class EmptyPropertyGraph extends PropertyGraph[EmptyRDG] {

  /**
   * Start with ids according to the types of start nodes.
   *
   * @param types
   * @return
   */
  override def createRDG(alias: String, types: Set[String]): EmptyRDG = new EmptyRDG()

  /**
   * Start with specific rdg with specific alias.
   *
   * @param rdg
   * @param alias
   * @return
   */
  override def createRDG(alias: String, rdg: EmptyRDG): EmptyRDG = rdg

  /**
   * Start with specific rdg with specific alias which in [[RepeatPathVar]]
   *
   * @param repeatVar
   * @param alias
   * @param rdg
   * @return
   */
  override def createRDGFromPath(
      repeatVar: RepeatPathVar,
      alias: String,
      rdg: EmptyRDG): EmptyRDG = rdg

}

class EmptySession(parser: ParserInterface, catalog: Catalog)
    extends KGReasonerSession[EmptyRDG](parser, catalog) {

  /**
   * Load a graph from Knowledge Graph to [[KGReasonerSession]]
   *
   * @param graphLoaderConfig
   * @return
   */
  override def loadGraph(graphLoaderConfig: GraphLoaderConfig): PropertyGraph[EmptyRDG] =
    new EmptyPropertyGraph()

}
