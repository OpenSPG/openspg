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

package com.antgroup.openspg.reasoner.lube.common.graph

import scala.collection.mutable

sealed trait IRField {
  def name: String
}

sealed trait RichIRField extends IRField

/**
 * Used to represent node in QueryGraph, the name of IRNode is represented by alias
 * @param name alias
 * @param fields the field names of node
 */
case class IRNode(name: String, fields: mutable.Set[String]) extends IRField

/**
 * Used to represent Edge in QueryGraph, the name of IREdge is represented by alias
 * @param name alias
 * @param fields the field names of edge
 */
case class IREdge(name: String, fields: mutable.Set[String]) extends IRField

/**
 * Used to represent prop of [[IRNode]] and [[IREdge]]
 * @param name the alias of IRNode or IREdge
 * @param field the specific filed of IRNode or IREdge
 */
case class IRProperty(name: String, field: String) extends IRField

/**
 * Used to represent variable during compute or data in row format.
 * @param name
 */
case class IRVariable(name: String) extends IRField

/**
 * A variable representing an external input as parameter.
 * @param name
 */
case class IRParameter(name: String) extends IRField

/**
 * Used to represent path in QueryGraph, eg, A -E1-> B -E2 -> C
 * @param name
 */
case class IRPath(name: String, elements: List[IRField]) extends RichIRField

/**
 * Used to represent array in QueryGraph, eg, Array[Path] to represent transitive path
 * @param name
 * @param element
 */
case class IRArray(element: IRField) extends RichIRField {
  override def name: String = element.name
}
