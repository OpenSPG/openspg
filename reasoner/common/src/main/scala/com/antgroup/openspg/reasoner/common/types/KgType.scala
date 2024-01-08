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

package com.antgroup.openspg.reasoner.common.types

trait KgType {
  def isNullable: Boolean = false
}

case object KTString extends KgType
case object KTCharacter extends KgType
case object KTInteger extends KgType
case object KTLong extends KgType
case object KTDouble extends KgType
// corresponding to java object
case object KTObject extends KgType
case object KTDate extends KgType
case object KTBoolean extends KgType
case object KTParameter extends KgType

/**
 * list type
 * @param elementType element type
 */
final case class KTList(elementType: KgType) extends KgType

/**
 * array type
 * @param elementType element type
 */
final case class KTArray(elementType: KgType) extends KgType

/**
 * Standard entity in Knowledge Graph.
 * @param label entity type name
 * @param spreadable is spreadable.
 */
final case class KTStd(label: String, spreadable: Boolean) extends KgType

/**
 * Meta concept in Knowledge Graph.
 * @param label meta concept name
 */
final case class KTConcept(label: String) extends KgType

/**
 * Custom semantic type, which linked to entity in Knowledge Graph.
 * @param label entity type  TODO add link function
 */
final case class KTAdvanced(label: String) extends KgType

/**
 * multi version property, default version number unit is ms
 * @param elementType
 */
final case class KTMultiVersion(elementType: KgType) extends KgType
