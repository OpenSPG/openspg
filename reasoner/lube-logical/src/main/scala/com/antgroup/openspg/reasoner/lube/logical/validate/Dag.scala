/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.lube.logical.validate

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

import com.antgroup.openspg.reasoner.common.exception.SystemError
import com.antgroup.openspg.reasoner.common.trees.AbstractTreeNode

class Dag[T <: AbstractTreeNode[T]: TypeTag]() {
  private val nodes = new mutable.HashMap[String, T]()
  private val edges = new mutable.HashMap[String, ListBuffer[String]]()

  def order(): List[String] = {
    val sorted = new mutable.ListBuffer[String]()
    while (sorted.size < nodes.size) {
      var isCircle = true
      for (node <- nodes.keySet) {
        val preNodes = preOrder(node)
        if (preNodes.diff(sorted.toSet).isEmpty && !sorted.contains(node)) {
          sorted.append(node)
          isCircle = false
        }
      }
      if (isCircle) {
        throw SystemError(s"has circle dependency $edges")
      }
    }
    sorted.toList
  }

  def preOrder(node: String): Set[String] = {
    edges.filter(_._2.contains(node)).map(_._1).toSet
  }

  def popRoot(): T = {
    popNode("result")
  }

  def popNode(nodeName: String): T = {
    if (edges.contains(nodeName)) {
      edges.apply(nodeName).clear()
    }
    nodes.apply(nodeName)
  }

  def getNode(node: String): T = {
    nodes.apply(node)
  }

  def getDependencies(node: String): List[T] = {
    val dependencies = edges.apply(node).toList
    dependencies.map(nodes.apply(_))
  }

  def hasPreOrder(node: String): Boolean = {
    !edges.values.flatten.find(_.equals(node)).isEmpty
  }

  def map[O <: AbstractTreeNode[O]: TypeTag](f: T => O): Dag[O] = {
    val newDag = new Dag[O]
    for (node <- nodes) {
      newDag.addNode(node._1, f(node._2))
    }
    for (edge <- edges) {
      for (to <- edge._2)
        newDag.addEdge(edge._1, to)
    }
    newDag
  }

  def addNode(name: String, node: T): Unit = {
    nodes.put(name, node)
  }

  def addEdge(start: String, end: String): Unit = {
    if (!edges.contains(start)) {
      edges.put(start, new ListBuffer[String]())
    }
    edges.apply(start).append(end)
  }

  def getEdges: Map[String, List[String]] = {
    edges.map(e => (e._1, e._2.toList)).toMap
  }

  def getNodes: Map[String, T] = {
    nodes.toMap
  }

}
