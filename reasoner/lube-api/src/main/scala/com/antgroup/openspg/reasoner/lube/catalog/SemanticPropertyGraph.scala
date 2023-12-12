package com.antgroup.openspg.reasoner.lube.catalog

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.NotDefineException
import com.antgroup.openspg.reasoner.common.graph.edge.{Direction, SPO}
import com.antgroup.openspg.reasoner.common.types.KgType
import com.antgroup.openspg.reasoner.lube.catalog.struct.{Edge, Field, Node}

/**
 * A graph defined by Property Graph Model with enhanced semantics.
 * KGReasoner use [[SemanticPropertyGraph]] to represent Knowledge Graph.
 */
class SemanticPropertyGraph(
    val graphName: String,
    val graphSchema: PropertyGraphSchema,
    val ruleDefines: mutable.Map[String, SemanticRule],
    val conceptMap: ConceptGraph)
    extends Serializable {
  override def toString: String = graphName

  def addProperty(nodeLabel: String, propertyName: String, propertyType: KgType): Unit = {
    addProperty(nodeLabel, propertyName, propertyType, true)
  }

  def addProperty(
      nodeLabel: String,
      propertyName: String,
      propertyType: KgType,
      resolved: Boolean): Unit = {
    val node = getNode(nodeLabel)
    if (node == null) {
      return
    }
    val property = new Field(propertyName, propertyType, resolved)
    graphSchema.addVertexField(nodeLabel, property)
  }

  def getNode(nodeLabel: String): Node = {
    if (nodeLabel.contains("/")) {
      graphSchema.nodes(nodeLabel.split("/")(0))
    } else {
      graphSchema.nodes(nodeLabel)
    }
  }

  def getEdge(spoStr: String): Edge = {
    var spo = new SPO(spoStr)
    if (spo.getP.equals("belongTo") && !graphSchema.edges.contains(spo)) {
      spo = new SPO(spo.getS, spo.getP, spo.getO.split("/")(0))
    }
    graphSchema.edges(spo)
  }

  def containsNode(nodeLabel: String): Boolean = {
    if (nodeLabel.contains("/")) {
      graphSchema.nodes.contains(nodeLabel.split("/")(0))
    } else {
      graphSchema.nodes.contains(nodeLabel)
    }
  }

  def containsEdge(spoStr: String): Boolean = {
    var spo = new SPO(spoStr)
    if (spo.getP.equals("belongTo") && !graphSchema.edges.contains(spo)) {
      spo = new SPO(spo.getS, spo.getP, spo.getO.split("/")(0))
    }
    graphSchema.edges.contains(spo)
  }

  def addEdge(
      srcLabel: String,
      label: String,
      dstLabel: String,
      direction: Direction,
      fields: Set[Field],
      resolved: Boolean): Unit = {
    var spo: SPO = null
    direction match {
      case Direction.OUT | Direction.BOTH =>
        spo = new SPO(srcLabel, label, dstLabel)
      case Direction.IN =>
        spo = new SPO(dstLabel, label, srcLabel)
      case _ =>
    }
    graphSchema.addEdgeField(spo, fields)
  }

  def merge(other: SemanticPropertyGraph): Unit = {
    other.graphSchema.nodes.foreach(n => {
      n._2.properties.foreach(p => { graphSchema.addVertexField(n._1, p) })
    })

    other.graphSchema.edges.foreach(e => {
      graphSchema.addEdgeField(e._1, e._2.properties)
    })
  }

  def getRule(define: String): SemanticRule = {
    if (!ruleDefines.contains(define)) {
      throw NotDefineException(s"$define not defined in SPG.")
    }
    ruleDefines(define)
  }

  def registerRule(define: String, rule: SemanticRule): Unit = {
    ruleDefines += (define -> rule)
  }

}
