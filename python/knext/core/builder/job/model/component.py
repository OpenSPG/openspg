# -*- coding: utf-8 -*-
#
#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

import os
from abc import ABC
from collections import defaultdict
from enum import Enum
from typing import List, Type, Dict, Union, TypeVar

from knext import rest
from knext.core.builder.operator.operator import Operator
from knext.core.schema import Schema
from knext.core.schema.model.base import SpgTypeEnum


class MappingTypeEnum(str, Enum):
    Entity = "SPG_TYPE"
    Relation = "RELATION"


class ComponentTypeEnum(str, Enum):
    SourceCsv = "CSV_SOURCE"
    Mapping = "MAPPING"
    Extract = "EXTRACT"
    SinkToKg = "GRAPH_SINK"


T = TypeVar("T", bound="Component")


class Component(ABC):
    """
    Base class for all component.
    """

    def __init__(self, type: ComponentTypeEnum, name: str):
        self.id = str(id(self))
        self.type = type
        self.name = name

        self.next = []
        self.pre = []

    def rename(self, name: str):
        self.name = name
        return self

    def to_dict(self):
        return {"id": self.id, "name": self.name}

    def _translate(self):
        return self

    def _to_rest(self):
        pass

    def __rshift__(self, other: Union[T, List[T]]):
        last = [self]
        while last and last[0].next:
            last = last[0].next

        if isinstance(other, list):
            if isinstance(last, list):
                for l in last:
                    l.next = other
                for o in other:
                    o._translate().pre = last
            else:
                last.next = other
                for o in other:
                    o._translate().pre = [last]
        else:
            o = other._translate()
            if isinstance(last, list):
                for l in last:
                    l.next = [o]
                o.pre = last
            else:
                last.next = [o]
                o.pre = [last]

        return self


class SourceCsvComponent(Component):
    """A source component that reading data from CSV file.

    Args:
        local_path: The local path of CSV file.
        columns: The column names that need to be read from the CSV file.
        start_row: The starting number of rows read from the CSV file.
                    If the CSV file includes a header, it needs to be greater than or equal to 2.
    Examples:
        source = SourceCsvComponent(
                    local_path="./builder/job/data/App.csv",
                    columns=["id", 'riskMark', 'useCert'],
                    start_row=2
                )
    """

    def __init__(self, local_path: str, columns: List[str], start_row: int):
        super().__init__(type=ComponentTypeEnum.SourceCsv, name="CSV")
        self.local_path = local_path
        self.columns = columns
        self.start_row = start_row

    def _to_rest(self):
        """Transforms `SourceCsvComponent` to REST model `CsvSourceNodeConfig`."""
        api_client = rest.ObjectStoreApi()
        url = api_client.object_store_post(
            name=self.local_path.split("/")[-1], file=self.local_path
        ).absolute_path

        config = rest.CsvSourceNodeConfig(
            start_row=self.start_row, url=url, columns=self.columns
        )
        return rest.Node(**super().to_dict(), node_config=config)


class KnowledgeExtractComponent(Component):
    """A Process Component that transforming unstructured data into structured data.

    Args:
        output_fields: All output column names after knowledge extraction processing.
    Examples:
        extract = KnowledgeExtractComponent(
                    output_fields=["id", 'riskMark', 'useCert']
                ).set_operator("DemoExtractOp")

    """

    def __init__(
        self,
        output_fields: List[str],
    ):
        super().__init__(type=ComponentTypeEnum.Extract, name="知识抽取")
        self.output_fields = output_fields
        self.operator = None
        self.params = None

    def set_operator(self, op_name: str, params: Dict[str, str] = None):
        """Sets knowledge extract operator to this component."""
        self.operator = op_name
        self.params = params
        return self

    def _to_rest(self):
        """Transforms `KnowledgeExtractComponent` to REST model `ExtractNodeConfig`."""
        client = Operator()
        operator_config = client._generate_op_config(
            op_name=self.operator, params=self.params
        )
        config = rest.ExtractNodeConfig(
            output_fields=self.output_fields, operator_config=operator_config
        )

        return rest.Node(**super().to_dict(), node_config=config)


class RelationMappingComponent(Component):
    """A Process Component that mapping data to relation type.

    Args:
        subject_name: The subject name import from SchemaHelper.
        predicate_name: The predicate name.
        object_name: The object name import from SchemaHelper.
    Examples:
        mapping = RelationMappingComponent(
                    subject_name=DEFAULT.App,
                    predicate_name=DEFAULT.App.useCert,
                    object_name=DEFAULT.Cert,
                ).add_field("src_id", "srcId") \
                 .add_field("dst_id", "dstId")

    """

    RELATION_BASE_FIELDS = ["srcId", "dstId"]

    def __init__(
        self,
        subject_name: Union[Type, str],
        predicate_name: str,
        object_name: Union[Type, str],
    ):
        super().__init__(type=ComponentTypeEnum.Mapping, name="关系映射")

        if isinstance(subject_name, str):
            self.subject_name = subject_name
        else:
            assert hasattr(
                subject_name, "__typename__"
            ), f"Cannot find `__typename__` of `{subject_name}` in schema_helper."
            self.subject_name = subject_name.__typename__
        if isinstance(object_name, str):
            self.object_name = object_name
        else:
            assert hasattr(
                object_name, "__typename__"
            ), f"Cannot find `__typename__` of `{object_name}` in schema_helper."
            self.object_name = object_name.__typename__
        self.predicate_name = predicate_name
        self.mapping = dict()
        self.mapping_type = MappingTypeEnum.Relation

        self.filters = list()

    def add_field(self, source_field: str, target_field: str):
        """Adds a field mapping from source data to property of spg_type.

        :param source_field: The source field to be mapped.
        :param target_field: The target field to map the source field to.
        :return: self
        """
        self.mapping[target_field] = source_field
        return self

    def add_filter(self, column_name: str, column_value: str):
        """Adds data filtering rule.
        Only the column that meets `column_ame=column_value` will execute the mapping.

        :param column_name: The column name to be filtered.
        :param column_value: The column value to be filtered.
        :return: self
        """
        self.filters.append((column_name, column_value))
        return self

    def _to_rest(self):
        """Transforms `RelationMappingComponent` to REST model `MappingNodeConfig`."""
        assert all(
            field in self.mapping.keys()
            for field in RelationMappingComponent.RELATION_BASE_FIELDS
        ), f"{self.__class__.__name__} must include mapping to {str(RelationMappingComponent.RELATION_BASE_FIELDS)}"

        mapping = defaultdict(list)
        for dst_name, src_name in self.mapping.items():
            mapping[src_name].append(dst_name)

        mapping_filters = [
            rest.MappingFilter(column_name=name, column_value=value)
            for name, value in self.filters
        ]
        mapping_configs = [
            rest.MappingConfig(source=src_name, target=tgt_names)
            for src_name, tgt_names in mapping.items()
        ]
        mapping_schemas = []

        config = rest.MappingNodeConfig(
            spg_name=f"{self.subject_name}_{self.predicate_name}_{self.object_name}",
            mapping_type=self.mapping_type,
            mapping_filters=mapping_filters,
            mapping_schemas=mapping_schemas,
            mapping_configs=mapping_configs,
        )
        return rest.Node(**super().to_dict(), node_config=config)


class EntityMappingComponent(Component):
    """A Process Component that mapping data to entity/event/concept type.

    Args:
        spg_type_name: The SPG type name import from SchemaHelper.
    Examples:
        mapping = EntityMappingComponent(
            spg_type_name=DEFAULT.App
        ).add_field("id", DEFAULT.App.id) \
            .add_field("id", DEFAULT.App.name) \
            .add_field("riskMark", DEFAULT.App.riskMark) \
            .add_field("useCert", DEFAULT.App.useCert)

    """

    ENTITY_BASE_FIELDS = ["id"]

    def __init__(self, spg_type_name: Union[Type, str]):
        super().__init__(type=ComponentTypeEnum.Mapping, name="实体映射")

        if isinstance(spg_type_name, str):
            self.spg_type_name = spg_type_name
        else:
            assert hasattr(
                spg_type_name, "__typename__"
            ), f"Cannot find `__typename__` of `{spg_type_name}` in schema_helper."
            self.spg_type_name = spg_type_name.__typename__
        self.mapping = dict()
        self.mapping_type = MappingTypeEnum.Entity
        self.schema_session = Schema().create_session()
        self.op_client = Operator()

        self.debug_operators = dict()
        self.filters = list()

    def add_field(self, source_field: str, target_field: str):
        """Adds a field mapping from source data to property of spg_type.

        :param source_field: The source field to be mapped.
        :param target_field: The target field to map the source field to.
        :return: self
        """
        self.mapping[target_field] = source_field
        return self

    def add_filter(self, column_name: str, column_value: str):
        """Adds data filtering rule.
        Only the column that meets `column_name=column_value` will execute the mapping.

        :param column_name: The column name to be filtered.
        :param column_value: The column value to be filtered.
        :return: self
        """
        self.filters.append((column_name, column_value))
        return self

    def _to_rest(self):
        """
        Transforms `EntityMappingComponent` to REST model `MappingNodeConfig`.
        """
        assert all(
            field in self.mapping.keys()
            for field in EntityMappingComponent.ENTITY_BASE_FIELDS
        ), f"{self.__class__.__name__} must include mapping to {str(EntityMappingComponent.ENTITY_BASE_FIELDS)}"
        mapping = defaultdict(list)
        schema = {}
        subject_type = self.schema_session.get(self.spg_type_name)
        for dst_name, src_name in self.mapping.items():
            prop = subject_type.properties.get(dst_name)
            mapping[src_name].append(prop.name)
            object_type_name = prop.object_type_name

            object_type = self.schema_session.get(object_type_name)
            if (
                hasattr(object_type, "link_operator")
                and object_type.link_operator is not None
            ):
                schema[dst_name] = object_type.link_operator
            if (
                hasattr(object_type, "normalize_operator")
                and object_type.normalize_operator is not None
            ):
                schema[dst_name] = object_type.normalize_operator
        if os.environ.get("KNEXT_DEBUG"):
            for name, operator in self.debug_operators:
                schema[name] = operator

        mapping_filters = [
            rest.MappingFilter(column_name=name, column_value=value)
            for name, value in self.filters
        ]
        mapping_configs = [
            rest.MappingConfig(source=src_name, target=tgt_names)
            for src_name, tgt_names in mapping.items()
        ]
        mapping_schemas = [
            rest.MappingSchema(name, operator_config=operator_config)
            for name, operator_config in schema.items()
        ]

        config = rest.MappingNodeConfig(
            spg_name=self.spg_type_name,
            mapping_type=self.mapping_type,
            mapping_filters=mapping_filters,
            mapping_schemas=mapping_schemas,
            mapping_configs=mapping_configs,
        )
        return rest.Node(**super().to_dict(), node_config=config)


class SPGMappingComponent(Component):
    """A Process Component that extract SPO triples from long texts,
    and mapping data to entity/event/concept type based on the schema definition.

    Args:
        spg_type_name: The SPG type name import from SchemaHelper.
    Examples:
        mapping = SPGMappingComponent(
            spg_type_name=DEFAULT.Disease
        ).set_operator("DiseaseExtractor")

    """

    def __init__(self, spg_type_name: Union[Type, str]):
        super().__init__(type=ComponentTypeEnum.Mapping, name="SPG抽取映射")

        if isinstance(spg_type_name, str):
            self.spg_type_name = spg_type_name
        else:
            assert hasattr(
                spg_type_name, "__typename__"
            ), f"Cannot find `__typename__` of `{spg_type_name}` in schema_helper."
            self.spg_type_name = spg_type_name.__typename__
        self.schema_session = Schema().create_session()
        self.operator = None
        self.params = None

    def set_operator(self, op_name: str, params: Dict[str, str] = None):
        """Sets knowledge extract operator to this component."""
        self.operator = op_name
        self.params = params
        return self

    def _translate(self):
        """Transforms `SPGMappingComponent` to REST model `ExtractNodeConfig` and `MappingNodeConfig`."""
        extract = KnowledgeExtractComponent([]).set_operator(self.operator, self.params)

        subject_mapping = EntityMappingComponent(self.spg_type_name).add_filter(
            "__vertex_type__", self.spg_type_name
        )
        subject_type = self.schema_session.get(self.spg_type_name)

        object_mappings = []
        for prop in subject_type.properties:
            subject_mapping.add_field(prop, prop)
            object_type_name = subject_type.properties[prop].object_type_name
            object_type = self.schema_session.get(object_type_name)

            if object_type.spg_type_enum == SpgTypeEnum.Basic:
                continue

            object_mapping = (
                EntityMappingComponent(object_type_name)
                .add_filter("__vertex_type__", object_type_name)
                .add_field("id", "id")
                .add_field("name", "name")
            )
            object_mappings.append(object_mapping)

        return extract >> [subject_mapping] + object_mappings


class SinkToKgComponent(Component):
    """The Sink Component that writing data to KG storage.

    Args:
        None
    Examples:
        sink = SinkToKgComponent()

    """

    def __init__(self):
        super().__init__(type=ComponentTypeEnum.SinkToKg, name="图谱")

    def _to_rest(self):
        """Transforms `SinkToKgComponent` to REST model `GraphStoreSinkNodeConfig`."""
        config = rest.GraphStoreSinkNodeConfig()
        return rest.Node(**super().to_dict(), node_config=config)
