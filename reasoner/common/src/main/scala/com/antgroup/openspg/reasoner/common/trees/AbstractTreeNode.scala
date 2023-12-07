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

package com.antgroup.openspg.reasoner.common.trees

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import scala.util.hashing.MurmurHash3

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

  def withNewChildren(newChildren: Array[T]): T = {
    if (sameAsCurrentChildren(newChildren)) {
      self
    } else {
      val copyMethod = AbstractTreeNode.copyMethod(self)
      val copyMethodParamTypes = copyMethod.symbol.paramLists.flatten.zipWithIndex
      val valueAndTypeTuples = copyMethodParamTypes.map { case (param, index) =>
        val value = if (index < productArity) {
          // Access product element to retrieve the value
          productElement(index)
        } else {
          typeOf[T] // Workaround to get implicit tag without reflection
        }
        value -> param.typeSignature
      }
      val updatedConstructorParams = updateConstructorParams(newChildren, valueAndTypeTuples)
      try {
        copyMethod(updatedConstructorParams: _*).asInstanceOf[T]
      } catch {
        case e: Exception =>
          throw InvalidConstructorArgument(
            s"""|Expected valid constructor arguments for $productPrefix
              |Old children: ${children.mkString(", ")}
              |New children: ${newChildren.mkString(", ")}
              |Current product: ${productIterator.mkString(", ")}
              |Constructor arguments updated with new children: ${updatedConstructorParams
              .mkString(", ")}.""".stripMargin,
            Some(e))
      }
    }
  }

  @inline private final def updateConstructorParams(
      newChildren: Array[T],
      currentValuesAndTypes: List[(Any, Type)]): Array[Any] = {
    // Returns true if `instance` could be an element of
    // List/NonEmptyList/Option container type `tpe`
    def couldBeElementOf(instance: Any, tpe: Type): Boolean = {
      currentMirror.reflect(instance).symbol.toType <:< tpe.typeArgs.head
    }

    val (unassignedChildren, constructorParams) =
      currentValuesAndTypes.foldLeft(newChildren.toList -> Vector.empty[Any]) {
        case ((remainingChildren, currentConstructorParams), nextValueAndType) =>
          nextValueAndType match {
            case (c: T, _) =>
              remainingChildren match {
                case Nil =>
                  throw new IllegalArgumentException(
                    s"""|When updating with new children: Did not have a child left to assign to
                        | the child that was previously $c Inferred constructor
                        | parameters so far: ${getClass.getSimpleName}(${currentConstructorParams
                      .mkString(", ")}, ...)""".stripMargin)
                case h :: t => t -> (currentConstructorParams :+ h)
              }
            case (_: Option[_], tpe) if tpe.typeArgs.head <:< typeOf[T] =>
              val option: Option[T] = remainingChildren.headOption.filter { c =>
                couldBeElementOf(c, tpe)
              }
              remainingChildren.drop(option.size) -> (currentConstructorParams :+ option)
            case (_: List[_], tpe) if tpe.typeArgs.head <:< typeOf[T] =>
              val childrenList: List[T] = remainingChildren.takeWhile { c =>
                couldBeElementOf(c, tpe)
              }
              remainingChildren.drop(
                childrenList.size) -> (currentConstructorParams :+ childrenList)
            case (_: NonEmptyList[_], tpe) if tpe.typeArgs.head <:< typeOf[T] =>
              val childrenList = NonEmptyList.fromListUnsafe(remainingChildren.takeWhile { c =>
                couldBeElementOf(c, tpe)
              })
              remainingChildren.drop(
                childrenList.size) -> (currentConstructorParams :+ childrenList)
            case (value, _) =>
              remainingChildren -> (currentConstructorParams :+ value)
          }
      }

    if (unassignedChildren.nonEmpty) {
      throw new IllegalArgumentException(s"""|Could not assign children [${unassignedChildren
        .mkString(", ")}] to parameters of ${getClass.getSimpleName}
            |Inferred constructor parameters: ${getClass.getSimpleName}(${constructorParams
        .mkString(", ")})""".stripMargin)
    }

    constructorParams.toArray
  }

  @inline private final def sameAsCurrentChildren(newChildren: Array[T]): Boolean = {
    val childrenLength = children.length
    if (childrenLength != newChildren.length) {
      false
    } else {
      var i = 0
      while (i < childrenLength && children(i) == newChildren(i)) i += 1
      i == childrenLength
    }
  }

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

  def children: Array[T] = {
    if (productIterator.isEmpty) {
      Array.empty[T]
    } else {
      val copyMethod = AbstractTreeNode.copyMethod(self)
      lazy val treeType = typeOf[T].erasure
      lazy val paramTypes: Seq[Type] =
        copyMethod.symbol.paramLists.head.map(_.typeSignature).toIndexedSeq
      productIterator.toArray.zipWithIndex.flatMap {
        case (t: T, _) => Some(t)
        case (o: Option[_], i) if paramTypes(i).typeArgs.head <:< treeType =>
          o.asInstanceOf[Option[T]]
        case (l: List[_], i) if paramTypes(i).typeArgs.head <:< treeType =>
          l.asInstanceOf[List[T]]
        case (nel: NonEmptyList[_], i) if paramTypes(i).typeArgs.head <:< treeType =>
          nel.toList.asInstanceOf[List[T]]
        case _ => Nil
      }
    }
  }

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

/**
 * Caches an instance of the copy method per case class type.
 */
object AbstractTreeNode {

  import scala.reflect.runtime.universe
  import scala.reflect.runtime.universe._

  private final lazy val mirror = universe.runtimeMirror(getClass.getClassLoader)
  // No synchronization required: No problem if a cache entry is lost due to a concurrent write.
  @volatile private var cachedCopyMethods = Map.empty[Class[_], MethodMirror]

  @inline protected final def copyMethod(instance: AbstractTreeNode[_]): MethodMirror = {
    val instanceClass = instance.getClass
    cachedCopyMethods.getOrElse(
      instanceClass, {
        val copyMethod = reflectCopyMethod(instance)
        cachedCopyMethods = cachedCopyMethods.updated(instanceClass, copyMethod)
        copyMethod
      })
  }

  @inline private final def reflectCopyMethod(instance: Object): MethodMirror = {
    try {
      val instanceMirror = mirror.reflect(instance)
      val tpe = instanceMirror.symbol.asType.toType
      val copyMethodSymbol = tpe.decl(TermName("copy")).asMethod
      instanceMirror.reflectMethod(copyMethodSymbol)
    } catch {
      case e: Exception =>
        throw new UnsupportedOperationException(
          s"Could not reflect the copy method of ${instance.toString.filterNot(_ == '$')}",
          e)
    }
  }

}

case class InvalidConstructorArgument(
    message: String,
    originalException: Option[Exception] = None)
    extends RuntimeException(message, originalException.orNull)
