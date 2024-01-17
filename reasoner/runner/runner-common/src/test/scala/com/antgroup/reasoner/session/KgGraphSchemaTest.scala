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

package com.antgroup.reasoner.session

import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  PartialGraphPattern,
  PatternConnection,
  PatternElement
}
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar}
import com.antgroup.openspg.reasoner.rdg.common.FoldRepeatEdgeInfo
import com.antgroup.openspg.reasoner.util.{KgGraphSchema, PathConnection}
import org.scalatest.flatspec.AnyFlatSpec

class KgGraphSchemaTest extends AnyFlatSpec {

  "convert2KgGraphSchema()" should "alias can not change" in {
    val schema1 = KgGraphSchema.convert2KgGraphSchema(
      PartialGraphPattern(
        "A",
        Map.apply(("A" -> PatternElement("A", Set.apply("Film"), null))),
        Map.apply()))

    assert(schema1.root.alias == "A")
  }

  "convert2KgGraphSchema()" should "connection's source alias equals root's alias" in {
    val root = new PatternElement("A", Set.apply("Film"), null)
    val b = PatternElement("B", Set.apply("FilmDirector"), null)
    val schema1 = KgGraphSchema.convert2KgGraphSchema(
      PartialGraphPattern(
        "A",
        Map.apply(("A" -> root), ("B" -> b)),
        Map.apply(
          root.alias -> Set.apply(
            new PatternConnection(
              "E1",
              "B",
              Set.apply("directFilm"),
              root.alias,
              Direction.IN,
              null)))))

    assert(schema1.root.alias == "A")
    assert(schema1.topology.get(schema1.root.alias).get.head.source == "A")
    assert(schema1.topology.get(schema1.root.alias).get.head.direction == Direction.OUT)
  }

  "expandSchema()" should "add new pattern" in {
    val aliasA = new PatternElement("A", Set.apply("Film"), null)
    val aliasB = new PatternElement("B", Set.apply("FilmDirector"), null)
    val p1 = PartialGraphPattern(
      aliasA.alias,
      Map.apply(("A" -> aliasA), ("B" -> aliasB)),
      Map.apply(
        aliasA.alias -> Set.apply(
          new PatternConnection(
            "E1",
            aliasB.alias,
            Set.apply("directFilm"),
            aliasA.alias,
            Direction.IN,
            null))))

    val aliasC = new PatternElement("C", Set.apply("FilmWriter"), null)
    val p2 = PartialGraphPattern(
      aliasB.alias,
      Map.apply(("B" -> aliasB), ("C" -> aliasC)),
      Map.apply(
        aliasB.alias -> Set.apply(
          new PatternConnection(
            "E3",
            aliasB.alias,
            Set.apply("workmates"),
            aliasC.alias,
            Direction.OUT,
            null))))

    val schema1 = KgGraphSchema.convert2KgGraphSchema(p1)
    val schema2 = KgGraphSchema.schemaChangeRoot(schema1, "B")
    val schema3 = KgGraphSchema.expandSchema(schema2, p2)
    assert(schema3.root.alias == "B")
  }

  "foldPathEdgeSchema()" should "create new path connection" in {
    val aliasA = PatternElement("A", Set.apply("Account"), null)
    val aliasB = PatternElement("B1", Set.apply("Account"), null)
    val schema = PartialGraphPattern(
      aliasA.alias,
      Map.apply("A" -> aliasA, "B1" -> aliasB),
      Map.apply(
        aliasA.alias -> Set.apply(
          new PatternConnection(
            "E1",
            aliasA.alias,
            Set.apply("trade"),
            aliasB.alias,
            Direction.OUT,
            null))))

    val foldSchema1 =
      KgGraphSchema.foldPathEdgeSchema(schema, new FoldRepeatEdgeInfo("E1", "E", "B1", "B"))
    assert(foldSchema1.root == aliasA)
    assert(foldSchema1.rootAlias == "A")
    assert(foldSchema1.nodes("A") == aliasA)
    assert(foldSchema1.nodes("B").alias == "B")
    val pc: PathConnection = foldSchema1.topology("A").head.asInstanceOf[PathConnection]
    assert(pc.alias == "E")
    assert(pc.source == "A")
    assert(pc.target == "B")
    assert(pc.vertexSchemaList.isEmpty)
    assert(pc.edgeSchemaList.length == 1)

    val aliasB2 = PatternElement("B2", Set.apply("Account"), null)
    val schema2 = PartialGraphPattern(
      "B",
      Map.apply("B" -> foldSchema1.nodes("B"), "B2" -> aliasB2),
      Map.apply(
        aliasA.alias -> Set.apply(
          new PatternConnection(
            "E2",
            "B",
            Set.apply("trade"),
            aliasB2.alias,
            Direction.OUT,
            null))))
    val expendSchema = KgGraphSchema.expandSchema(foldSchema1, schema2)

    val foldSchema2 =
      KgGraphSchema.foldPathEdgeSchema(expendSchema, new FoldRepeatEdgeInfo("E2", "E", "B2", "B"))
    assert(foldSchema2.root == null)
    assert(foldSchema2.rootAlias == "E.B1")
    assert(foldSchema2.nodes("A") == aliasA)
    assert(foldSchema2.nodes("B").alias == "B")
    val pc2: PathConnection = foldSchema2.topology("A").head.asInstanceOf[PathConnection]
    assert(pc2.alias == "E")
    assert(pc2.source == "A")
    assert(pc2.target == "B")
    assert(pc2.vertexSchemaList.length == 1)
    assert(pc2.edgeSchemaList.length == 2)

    val aliasB3 = PatternElement("B3", Set.apply("Account"), null)
    val schema3 = PartialGraphPattern(
      "B",
      Map.apply("B" -> foldSchema2.nodes("B"), "B3" -> aliasB3),
      Map.apply(
        aliasA.alias -> Set.apply(
          new PatternConnection(
            "E3",
            "B",
            Set.apply("trade"),
            aliasB3.alias,
            Direction.OUT,
            null))))
    val expendSchema2 = KgGraphSchema.expandSchema(foldSchema2, schema3)
    val foldSchema3 = KgGraphSchema.foldPathEdgeSchema(
      expendSchema2,
      new FoldRepeatEdgeInfo("E3", "E", "B3", "B"))
    assert(foldSchema3.root == null)
    assert(foldSchema3.rootAlias == "E.B2")
    assert(foldSchema3.nodes("A") == aliasA)
    assert(foldSchema3.nodes("B").alias == "B")
    val pc3: PathConnection = foldSchema3.topology("A").head.asInstanceOf[PathConnection]
    assert(pc3.alias == "E")
    assert(pc3.source == "A")
    assert(pc3.target == "B")
    assert(pc3.vertexSchemaList.length == 2)
    assert(pc3.edgeSchemaList.length == 3)

  }

  "foldPathEdgeSchema()" should "handling complex paths" in {
    val aliasA = PatternElement("A", Set.apply("User"), null)
    val aliasB = PatternElement("B", Set.apply("Account"), null)
    val aliasC = PatternElement("C1", Set.apply("Account"), null)
    val schema = PartialGraphPattern(
      aliasC.alias,
      Map.apply("A" -> aliasA, "B" -> aliasB, "C1" -> aliasC),
      Map.apply(
        aliasA.alias -> Set.apply(
          new PatternConnection(
            "E1",
            aliasA.alias,
            Set.apply("has"),
            aliasB.alias,
            Direction.OUT,
            null)),
        aliasB.alias -> Set.apply(
          new PatternConnection(
            "E1",
            aliasA.alias,
            Set.apply("has"),
            aliasB.alias,
            Direction.OUT,
            null),
          new PatternConnection(
            "E2",
            aliasB.alias,
            Set.apply("trade"),
            aliasC.alias,
            Direction.OUT,
            null)),
        aliasC.alias -> Set.apply(
          new PatternConnection(
            "E2",
            aliasB.alias,
            Set.apply("trade"),
            aliasC.alias,
            Direction.OUT,
            null))))

    val foldSchema1 =
      KgGraphSchema.foldPathEdgeSchema(schema, new FoldRepeatEdgeInfo("E2", "E", "C1", "C"))
    assert(foldSchema1.root.typeNames == aliasC.typeNames)
    assert(foldSchema1.rootAlias == "C")
    assert(foldSchema1.nodes("A") == aliasA)
    assert(foldSchema1.nodes("B").alias == "B")
    val pc: PathConnection = foldSchema1.topology("C").head.asInstanceOf[PathConnection]
    assert(pc.alias == "E")
    assert(pc.source == "B")
    assert(pc.target == "C")
    assert(pc.vertexSchemaList.isEmpty)
    assert(pc.edgeSchemaList.length == 1)

    val aliasC2 = PatternElement("C2", Set.apply("Account"), null)
    val schema2 = PartialGraphPattern(
      "C",
      Map.apply("C" -> foldSchema1.nodes("C"), "C2" -> aliasC2),
      Map.apply(
        "C" -> Set.apply(
          new PatternConnection(
            "E2",
            "C",
            Set.apply("trade"),
            aliasC2.alias,
            Direction.OUT,
            null))))
    val expendSchema = KgGraphSchema.expandSchema(foldSchema1, schema2)

    val foldSchema2 =
      KgGraphSchema.foldPathEdgeSchema(expendSchema, new FoldRepeatEdgeInfo("E2", "E", "C2", "C"))
    assert(foldSchema2.root == null)
    assert(foldSchema2.rootAlias == "E.C1")
    assert(foldSchema2.nodes("A") == aliasA)
    assert(foldSchema2.nodes("B") == aliasB)
    val pc2: PathConnection = foldSchema2.topology("C").head.asInstanceOf[PathConnection]
    assert(pc2.alias == "E")
    assert(pc2.source == "B")
    assert(pc2.target == "C")
    assert(pc2.vertexSchemaList.length == 1)
    assert(pc2.edgeSchemaList.length == 2)

  }

  "schemaAliasMapping()" should "works" in {
    val aliasA = PatternElement("A", Set.apply("User"), null)
    val aliasB = PatternElement("B", Set.apply("Account"), null)
    val aliasC = PatternElement("C", Set.apply("Account"), null)
    val schema = PartialGraphPattern(
      aliasB.alias,
      Map.apply("A" -> aliasA, "B" -> aliasB, "C" -> aliasC),
      Map.apply(
        aliasB.alias -> Set.apply(
          new PatternConnection(
            "E1",
            aliasB.alias,
            Set.apply("has"),
            aliasA.alias,
            Direction.IN,
            null),
          new PatternConnection(
            "E2",
            aliasB.alias,
            Set.apply("trade"),
            aliasC.alias,
            Direction.OUT,
            null))))

    val schema2 = KgGraphSchema.convert2KgGraphSchema(schema)

    val schema3 = KgGraphSchema.schemaAliasMapping(
      schema2,
      Map.apply(
        NodeVar("A", Set.empty) -> NodeVar("A_rename", Set.empty),
        NodeVar("B", Set.empty) -> NodeVar("B_rename", Set.empty),
        EdgeVar("E1", Set.empty) -> EdgeVar("E1_rename", Set.empty)))
    assert(schema3.rootAlias == "B_rename")
  }
}
