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

package com.antgroup.openspg.reasoner.common.types

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException

trait KgType {
  def isNullable: Boolean = false
}

trait BasicKgType extends KgType

trait AdvancedKgType extends KgType

case object KTString extends BasicKgType
case object KTCharacter extends BasicKgType
case object KTInteger extends BasicKgType
case object KTLong extends BasicKgType
case object KTDouble extends BasicKgType
// corresponding to java object
case object KTObject extends BasicKgType
case object KTDate extends BasicKgType
case object KTBoolean extends BasicKgType

/**
 * list type
 * @param elementType element type
 */
final case class KTList(elementType: KgType) extends AdvancedKgType

/**
 * array type
 * @param elementType element type
 */
final case class KTArray(elementType: KgType) extends AdvancedKgType

/**
 * Standard entity in Knowledge Graph.
 * @param label entity type name
 * @param spreadable is spreadable.
 */
final case class KTStd(label: String, basicType: BasicKgType, spreadable: Boolean)
    extends AdvancedKgType

/**
 * Meta concept in Knowledge Graph.
 * @param label meta concept name
 */
final case class KTConcept(label: String) extends AdvancedKgType

/**
 * Custom semantic type, which linked to entity in Knowledge Graph.
 * @param label entity type  TODO add link function
 */
final case class KTAdvanced(label: String) extends AdvancedKgType

/**
 * multi version property, default version number unit is ms
 * @param elementType
 */
final case class KTMultiVersion(elementType: KgType) extends AdvancedKgType

object KgType {

  def getNumberSeq(kgType: KgType): Int = {
    kgType match {
      case KTInteger => 1
      case KTLong => 2
      case KTDouble => 3
      case _ => throw UnsupportedOperationException(s"cannot support number type $kgType")
    }
  }

}
