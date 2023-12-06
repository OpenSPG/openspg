package com.antgroup.openspg.reasoner.common.concept

sealed abstract class Predicate(name: String)
final class SubClassOf() extends Predicate("subClassOf")
final class SubCategoryOf() extends Predicate("subCategoryOf")
final class IsA() extends Predicate("isA")
