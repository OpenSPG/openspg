package com.antgroup.openspg.reasoner.common.trees

import scala.reflect.ClassTag

import com.antgroup.openspg.reasoner.common.exception.SystemError

case class TreeFinder[T <: AbstractTreeNode[T]: ClassTag](f: PartialFunction[T, Unit]) {

  def findExactlyOne(tree: T): T = {
    val results = tree.collect {
      case op if f.isDefinedAt(op) =>
        f(op)
        op
    }
    if (results.size != 1) {
      throw SystemError(s"Failed to extract single matching tree node from $tree")
    } else {
      results.head
    }
  }

}
