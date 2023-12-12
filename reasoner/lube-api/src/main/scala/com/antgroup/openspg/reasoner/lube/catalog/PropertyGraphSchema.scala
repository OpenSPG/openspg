package com.antgroup.openspg.reasoner.lube.catalog

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.SchemaException
import com.antgroup.openspg.reasoner.common.graph.edge.{Direction, SPO}
import com.antgroup.openspg.reasoner.common.types.KgType
import com.antgroup.openspg.reasoner.lube.catalog.struct.{Edge, Field, Node, NodeType}
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

class PropertyGraphSchema(
    val nodes: mutable.Map[String, Node],
    val edges: mutable.Map[SPO, Edge]) extends Serializable {

  def getNodeField(nodeTypes: Set[String], fieldName: String): Field = {
    for (nodeType <- nodeTypes) {
      val field = getNodeField(nodeType, fieldName)
      if (field != null) {
        return field
      }
    }
    throw SchemaException(s"Cannot find $fieldName in $nodeTypes")
  }

  def getNodeField(nodeType: String, fieldName: String): Field = {
    val node = nodes.get(nodeType)
    if (node.isEmpty) {
      null
    } else {
      val tmpFields = node.get.properties.filter(_.name.equals(fieldName))
      if (tmpFields.isEmpty) {
        throw SchemaException(s"Cannot find $fieldName in $nodeType")
      } else {
        tmpFields.head
      }
    }
  }

  def addVertexField(nodeType: String, field: Field): Unit = {
    val node = nodes.get(nodeType)
    if (!node.isEmpty) {
      val props = new mutable.HashSet[Field]()
      props.++=(node.get.properties.filter(!_.name.equals(field.name)))
      props.add(field)
      nodes.put(nodeType, node.get.copy(properties = props.toSet))
    }
  }

  def getEdgeField(spoStr: Set[String], fieldName: String): Field = {
    for (spo <- spoStr) {
      val field = getEdgeField(spo, fieldName)
      if (field != null) {
        return field
      }
    }
    throw SchemaException(s"Cannot find $fieldName in $spoStr")
  }

  def getEdgeField(spoStr: String, fieldName: String): Field = {
    val spoArray = spoStr.split(SPO.SPLITTER)
    getEdgeField(spoArray(0), spoArray(1), spoArray(2), fieldName)
  }

  def addEdgeField(spo: SPO, field: Set[Field]): Unit = {
    val edge = edges.get(spo)
    if (!edge.isEmpty) {
      if (field != null) {
        val props = edge.get.properties ++ field
        edges.put(spo, edge.get.copy(properties = props))
      }
    } else {
      if (field != null) {
        edges.put(spo, Edge(spo.getS, spo.getP, spo.getO, field, false))
      } else {
        edges.put(spo, Edge(spo.getS, spo.getP, spo.getO, Set.empty, false))
      }
    }
  }

  def getEdgeField(
      startNode: String,
      typeName: String,
      endNode: String,
      fieldName: String): Field = {
    val spo = new SPO(startNode, typeName, endNode)
    val edge = edges.get(spo)
    if (edge.isEmpty) {
      null
    } else {
      val tmpFields = edge.get.properties.filter(_.name.equals(fieldName))
      if (tmpFields.isEmpty) {
        null
      } else {
        tmpFields.head
      }
    }
  }

  def getTargetType(startNode: String, edgeType: String, direction: Direction): Set[String] = {
    val flag = direction match {
      case Direction.OUT => s"${startNode}${SPO.SPLITTER}${edgeType}"
      case Direction.IN => s"${edgeType}${SPO.SPLITTER}${startNode}"
      case _ => ""
    }
    val eSet = edges.map(e => (e._1.toString, e._2)).filter(_._1.contains(flag))
    direction match {
      case Direction.OUT => eSet.values.map(_.endNode).toSet
      case Direction.IN => eSet.values.map(_.startNode).toSet
      case _ => Set.empty
    }
  }

  def toJson: String = {
    implicit val formats =
      Serialization.formats(
        FullTypeHints(
          List.apply(classOf[KgType]))) + new SPOKeySerializer + new EnumNameSerializer(NodeType)
    write(this)
  }

}

class SPOKeySerializer
    extends CustomKeySerializer[SPO](format =>
      ({ case str: String => new SPO(str) }, { case spo: SPO => spo.toString }))
