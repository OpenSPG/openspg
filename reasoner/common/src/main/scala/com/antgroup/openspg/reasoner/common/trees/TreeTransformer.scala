/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.reasoner.common.trees

import scala.reflect.ClassTag

abstract class TreeTransformer[T <: AbstractTreeNode[T]: ClassTag, O] {
  def transform(tree: T): O
}

abstract class TreeTransformerWithContext[T <: AbstractTreeNode[T]: ClassTag, O, C] {
  def transform(tree: T, context: C): (O, C)
}

abstract class TreeRewriter[T <: AbstractTreeNode[T]: ClassTag] extends TreeTransformer[T, T]

abstract class TreeRewriterWithContext[T <: AbstractTreeNode[T]: ClassTag, C]
    extends TreeTransformerWithContext[T, T, C] {
  def transform(tree: T, context: C): (T, C)
}

/**
 * Applies the given partial function starting from the leaves of this tree.
 */
case class BottomUp[T <: AbstractTreeNode[T]: ClassTag](rule: PartialFunction[T, T])
    extends TreeRewriter[T] {

  def transform(tree: T): T = {
    val childrenLength = tree.children.length
    val afterChildren = if (childrenLength == 0) {
      tree
    } else {
      val updatedChildren = {
        val childrenCopy = new Array[T](childrenLength)
        var i = 0
        while (i < childrenLength) {
          childrenCopy(i) = transform(tree.children(i))
          i += 1
        }
        childrenCopy
      }
      tree.withNewChildren(updatedChildren)
    }
    if (rule.isDefinedAt(afterChildren)) rule(afterChildren) else afterChildren
  }

}

/**
 * Applies the given partial function starting from the leaves of this tree.
 * An additional context is being recursively passed
 * from the leftmost child to its siblings and eventually to its parent.
 */
case class BottomUpWithContext[T <: AbstractTreeNode[T]: ClassTag, C](
    rule: PartialFunction[(T, C), (T, C)])
    extends TreeRewriterWithContext[T, C] {

  def transform(tree: T, context: C): (T, C) = {
    val childrenLength = tree.children.length
    var updatedContext = context
    val afterChildren = if (childrenLength == 0) {
      tree
    } else {
      val updatedChildren = new Array[T](childrenLength)
      var i = 0
      while (i < childrenLength) {
        val pair = transform(tree.children(i), updatedContext)
        updatedChildren(i) = pair._1
        updatedContext = pair._2
        i += 1
      }
      tree.withNewChildren(updatedChildren)
    }
    if (rule.isDefinedAt(afterChildren -> updatedContext)) {
      rule(afterChildren -> updatedContext)
    } else {
      afterChildren -> updatedContext
    }
  }

}

/**
 * Applies the given partial function starting from the root of this tree.
 *
 * @note Note the applied rule cannot insert new parent nodes.
 */
case class TopDown[T <: AbstractTreeNode[T]: ClassTag](rule: PartialFunction[T, T])
    extends TreeRewriter[T] {

  def transform(tree: T): T = {
    val afterSelf = if (rule.isDefinedAt(tree)) rule(tree) else tree
    val childrenLength = afterSelf.children.length
    if (childrenLength == 0) {
      afterSelf
    } else {
      val updatedChildren = {
        val childrenCopy = new Array[T](childrenLength)
        var i = 0
        while (i < childrenLength) {
          childrenCopy(i) = transform(afterSelf.children(i))
          i += 1
        }
        childrenCopy
      }
      afterSelf.withNewChildren(updatedChildren)
    }
  }

}

/**
 * Applies the given partial function starting from the root of this tree.
 * An additional context is being recursively passed
 * from the leftmost child to its siblings and eventually to its parent.
 */
case class TopDownWithContext[T <: AbstractTreeNode[T]: ClassTag, C](
    rule: PartialFunction[(T, C), (T, C)])
    extends TreeRewriterWithContext[T, C] {

  def transform(tree: T, context: C): (T, C) = {
    var (afterSelf, updatedContext) = if (rule.isDefinedAt(tree -> context)) {
      rule(tree -> context)
    } else {
      tree -> context
    }

    val childrenLength = afterSelf.children.length
    val afterChildren = if (childrenLength == 0) {
      afterSelf
    } else {
      val updatedChildren = new Array[T](childrenLength)
      var i = 0
      while (i < childrenLength) {
        val pair = transform(afterSelf.children(i), updatedContext)
        updatedChildren(i) = pair._1
        updatedContext = pair._2
        i += 1
      }
      afterSelf.withNewChildren(updatedChildren)
    }
    afterChildren -> updatedContext
  }

}

/**
 * Applies the given transformation starting from the leaves of this tree.
 */
case class Transform[T <: AbstractTreeNode[T]: ClassTag, O](transform: (T, List[O]) => O)
    extends TreeTransformer[T, O] {

  def transform(tree: T): O = {
    val children = tree.children
    val childrenLength = children.length
    if (childrenLength == 0) {
      transform(tree, List.empty[O])
    } else {
      val transformedChildren = {
        var tmpChildren = List.empty[O]
        var i = 0
        while (i < childrenLength) {
          tmpChildren ::= transform(children(i))
          i += 1
        }
        tmpChildren.reverse
      }
      transform(tree, transformedChildren)
    }
  }

}
