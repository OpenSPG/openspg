package com.antgroup.openspg.reasoner.util

import scala.collection.JavaConverters._

object Convert2ScalaUtil {

  def toScalaImmutableMap[T1, T2](jMap: java.util.Map[T1, T2]): Map[T1, T2] = {
    jMap.asScala.toMap;
  }

  def toScalaSeq[T](jList: java.util.List[T]): Seq[T] = {
    jList.asScala
  }

  def toScalaList[T](jList: java.util.List[T]): List[T] = {
    jList.asScala.toList
  }

  def toScalaImmutableSet[T](jSet: java.util.Set[T]): Set[T] = {
    jSet.asScala.toSet
  }

}
