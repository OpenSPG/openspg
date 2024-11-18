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

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._

import cats.data.NonEmptyList

abstract class AbstractTreeNode[T <: AbstractTreeNode[T]: TypeTag]
    extends Product
    with Traversable[T] {
  self: T =>

  implicit protected def ct: ClassTag[T] = {
    ClassTag[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe))
  }

  def rewrite(f: PartialFunction[T, T]): T = {
    BottomUp[T](f).transform(self)
  }

  def rewriteTopDown(f: PartialFunction[T, T]): T = {
    TopDown[T](f).transform(self)
  }

  def arity: Int = children.length

  def isLeaf: Boolean = children.isEmpty

  def height: Int = transform[Int] { case (_, childHeights) =>
    (0 :: childHeights).max + 1
  }

  override def size: Int = transform[Int] { case (_, childSizes) =>
    childSizes.sum + 1
  }

  def map[O <: AbstractTreeNode[O]: ClassTag](f: T => O): O = transform[O] {
    case (node, transformedChildren) =>
      f(node).withNewChildren(transformedChildren.toArray)
  }

  def transform[O](f: (T, List[O]) => O): O = {
    try {
      Transform(f).transform(self)
    } catch {
      case ex: StackOverflowError =>
        throw new RuntimeException(ex)
    }
  }

  def withNewChildren(newChildren: Array[T]): T

  override def foreach[O](f: T => O): Unit = transform[O] { case (node, _) =>
    f(node)
  }

  /**
   * Checks if the parameter tree is contained within this tree. A tree always contains itself.
   *
   * @param other other tree
   * @return true, iff `other` is contained in that tree
   */
  def containsTree(other: T): Boolean = transform[Boolean] { case (node, childrenContain) =>
    (node == other) || childrenContain.contains(true)
  }

  /**
   * Prints a tree representation of the node.
   */
  def show(): Unit = {
    // scalastyle:off
    println(pretty)
    // scalastyle:on
  }

  /**
   * Returns a string-tree representation of the node.
   *
   * @return tree-style representation of that node and all children
   */
  def pretty: String = {
    val lines = new ArrayBuffer[String]

    @tailrec
    def recTreeToString(toPrint: List[T], prefix: String, stack: List[List[T]]): Unit = {
      toPrint match {
        case Nil =>
          stack match {
            case Nil =>
            case top :: remainingStack =>
              recTreeToString(top, prefix.dropRight(4), remainingStack)
          }
        case last :: Nil =>
          lines += s"$prefix└─${last.toString}"
          recTreeToString(last.children.toList, s"$prefix    ", Nil :: stack)
        case next :: siblings =>
          lines += s"$prefix├─${next.toString}"
          recTreeToString(next.children.toList, s"$prefix│   ", siblings :: stack)
      }
    }

    recTreeToString(List(this), "", Nil)
    lines.mkString("\n")
  }

  def children: Array[T]

  override def toString: String = s"${getClass.getSimpleName}${if (args.isEmpty) ""
  else s"(${args.mkString(", ")})"}"

  /**
   * Arguments that should be printed. The default implementation excludes children.
   */
  def args: Iterator[Any] = {
    lazy val treeType = typeOf[T].erasure
    currentMirror
      .reflect(this)
      .symbol
      .typeSignature
      .members
      .collect { case a: TermSymbol => a }
      .filter(_.isCaseAccessor)
      .filterNot(_.isMethod)
      .toList
      .map(currentMirror.reflect(this).reflectField)
      .map(fieldMirror => fieldMirror -> fieldMirror.get)
      .filter { case (fieldMirror, value) =>
        def containsChildren: Boolean =
          fieldMirror.symbol.typeSignature.typeArgs.head <:< treeType

        value match {
          case c: T if containsChild(c) => false
          case _: Option[_] if containsChildren => false
          case _: NonEmptyList[_] if containsChildren => false
          case _: Iterable[_] if containsChildren => false
          case _: Array[_] if containsChildren => false
          case _ => true
        }
      }
      .map { case (termSymbol, value) =>
        s"${termSymbol.symbol.name.toString.trim}=$value"
      }
      .reverseIterator
  }

  /**
   * Checks if `other` is a direct child of this tree.
   *
   * @param other other tree
   * @return true, iff `other` is a direct child of this tree
   */
  def containsChild(other: T): Boolean = {
    children.contains(other)
  }

  /**
   * Turns all arguments in `args` into a string that describes the arguments.
   *
   * @return argument string
   */
  def argString: String = args.mkString(", ")

}
