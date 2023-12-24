# -*- coding: utf-8 -*-
# Copyright 2023 Ant Group CO., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.
from enum import Enum
from typing import Union, Dict, List, Tuple, Sequence, Optional

from knext import rest
from knext.common.runnable import Input, Output

from knext.common.schema_helper import SPGTypeHelper, PropertyHelper
from knext.component.builder.base import Mapping
from knext.operator.op import LinkOp, FuseOp, PredictOp
from knext.operator.spg_record import SPGRecord


class LinkingStrategyEnum(str, Enum):
    IDEquals = "ID_EQUALS"


class FusingStrategyEnum(str, Enum):
    pass


class PredictingStrategyEnum(str, Enum):
    pass


class SPGTypeMapping(Mapping):
    """A Process Component that mapping data to entity/event/concept/standard type.

    Args:
        spg_type_name: The SPG type name of subject import from SPGTypeHelper.
    Examples:
        mapping = SPGTypeMapping(
            spg_type_name=DEFAULT.App
        ).add_mapping_field("id", DEFAULT.App.id) \
            .add_mapping_field("name", DEFAULT.App.name) \
            .add_mapping_field("riskMark", DEFAULT.App.riskMark) \
            .add_predicting_field(DEFAULT.App.useCert)
    """

    """The SPG type name of subject import from SPGTypeHelper."""
    spg_type_name: Union[str, SPGTypeHelper]

    mapping: Dict[str, str] = dict()

    filters: List[Tuple[str, str]] = list()

    subject_fusing_strategy: Optional[Union[FusingStrategyEnum, FuseOp]] = None

    object_linking_strategies: Dict[str, Union[LinkingStrategyEnum, LinkOp]] = dict()

    predicate_predicting_strategies: Dict[str, Union[PredictingStrategyEnum, PredictOp]] = dict()

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return SPGRecord

    @property
    def input_keys(self):
        return None

    @property
    def output_keys(self):
        return self.output_fields

    def set_fusing_strategy(self, fusing_strategy: FuseOp):
        """"""
        self.subject_fusing_strategy = fusing_strategy
        return self

    def add_mapping_field(
        self,
        source_field: str,
        target_field: Union[str, PropertyHelper],
        linking_strategy: Union[LinkingStrategyEnum, LinkOp] = None,
    ):
        """Adds a field mapping from source data to property of spg_type.

        :param source_field: The source field to be mapped.
        :param target_field: The target field (SPG property name) to map the source field to.
        :param linking_strategy: The target field to map the source field to.
        :return: self
        """
        self.mapping[target_field] = source_field
        self.object_linking_strategies[target_field] = linking_strategy
        return self

    def add_predicting_field(
        self,
        field: Union[str, PropertyHelper],
        predicting_strategy: PredictOp = None,
    ):
        self.predicate_predicting_strategies[field] = predicting_strategy
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

    def to_rest(self):
        """
        Transforms `SPGTypeMapping` to REST model `SpgTypeMappingNodeConfig`.
        """
        from knext.client.schema import SchemaClient
        client = SchemaClient()
        spg_type = client.query_spg_type(self.spg_type_name)

        mapping_filters = [
            rest.MappingFilter(column_name=name, column_value=value)
            for name, value in self.filters
        ]
        mapping_configs = []
        for tgt_name, src_name in self.mapping.items():
            linking_strategy = self.object_linking_strategies.get(tgt_name, None)
            if isinstance(linking_strategy, LinkOp):
                strategy_config = rest.OperatorLinkingConfig(
                    operator_config=linking_strategy.to_rest()
                )
            elif linking_strategy == LinkingStrategyEnum.IDEquals:
                strategy_config = rest.IdEqualsLinkingConfig()
            elif not linking_strategy:
                object_type_name = spg_type.properties[tgt_name].object_type_name
                if object_type_name in LinkOp.bind_schemas:
                    op_name = LinkOp.bind_schemas[object_type_name]
                    op = LinkOp.by_name(op_name)()
                    strategy_config = rest.OperatorLinkingConfig(
                        operator_config=op.to_rest()
                    )
                else:
                    strategy_config = None
            else:
                raise ValueError(f"Invalid linking_strategy [{linking_strategy}].")
            mapping_configs.append(
                rest.MappingConfig(
                    source=src_name,
                    target=tgt_name,
                    strategy_config=strategy_config,
                )
            )

        predicting_configs = []
        for predicate_name, predicting_strategy in self.predicate_predicting_strategies.items():
            if isinstance(predicting_strategy, PredictOp):
                strategy_config = rest.OperatorPredictingConfig(
                    operator_config=predicting_strategy.to_rest()
                )
            elif not predicting_strategy:
                if (self.spg_type_name, predicate_name) in PredictOp.bind_schemas:
                    op_name = PredictOp.bind_schemas[(self.spg_type_name, predicate_name)]
                    op = PredictOp.by_name(op_name)()
                    strategy_config = rest.OperatorPredictingConfig(
                        operator_config=op.to_rest()
                    )
                else:
                    strategy_config = None
            else:
                raise ValueError(f"Invalid predicting_strategy [{predicting_strategy}].")
            if strategy_config:
                predicting_configs.append(
                    strategy_config
                )

        if isinstance(self.subject_fusing_strategy, FuseOp):
            fusing_config = rest.OperatorFusingConfig(
                operator_config=self.fusing_strategy.to_rest()
            )
        elif not self.subject_fusing_strategy:
            if self.spg_type_name in FuseOp.bind_schemas:
                op_name = FuseOp.bind_schemas[self.spg_type_name]
                op = FuseOp.by_name(op_name)()
                fusing_config = rest.OperatorFusingConfig(
                    operator_config=op.to_rest()
                )
            else:
                fusing_config = None
        else:
            raise ValueError(f"Invalid fusing_strategy [{self.subject_fusing_strategy}].")

        config = rest.SpgTypeMappingNodeConfig(
            spg_type=self.spg_type_name,
            mapping_filters=mapping_filters,
            mapping_configs=mapping_configs,
            subject_fusing_config=fusing_config,
            predicting_configs=predicting_configs
        )
        return rest.Node(**super().to_dict(), node_config=config)

    def invoke(self, input: Input) -> Sequence[Output]:
        raise NotImplementedError(f"`invoke` method is not currently supported for {self.__class__.__name__}.")

    @classmethod
    def from_rest(cls, node: rest.Node):
        raise NotImplementedError(f"`invoke` method is not currently supported for {cls.__name__}.")

    def submit(self):
        pass


class RelationMapping(Mapping):
    """A Process Component that mapping data to relation type.

    Args:
        subject_name: The subject name import from SPGTypeHelper.
        predicate_name: The predicate name.
        object_name: The object name import from SPGTypeHelper.
    Examples:
        mapping = RelationMapping(
                    subject_name=DEFAULT.App,
                    predicate_name=DEFAULT.App.useCert,
                    object_name=DEFAULT.Cert,
                ).add_mapping_field("src_id", "srcId") \
                 .add_mapping_field("dst_id", "dstId")

    """

    """The SPG type names of (subject, predicate, object) triplet imported from SPGTypeHelper and PropertyHelper."""
    subject_name: Union[str, SPGTypeHelper]
    predicate_name: Union[str, PropertyHelper]
    object_name: Union[str, SPGTypeHelper]

    mapping: Dict[str, str] = dict()

    filters: List[Tuple[str, str]] = list()

    def add_mapping_field(self, source_field: str, target_field: str):
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

    def to_rest(self):
        """Transforms `RelationMappingComponent` to REST model `MappingNodeConfig`."""

        mapping_filters = [
            rest.MappingFilter(column_name=name, column_value=value)
            for name, value in self.filters
        ]
        mapping_configs = [
            rest.MappingConfig(source=src_name, target=tgt_name)
            for tgt_name, src_name in self.mapping.items()
        ]

        config = rest.RelationMappingNodeConfig(
            relation=f"{self.subject_name}_{self.predicate_name}_{self.object_name}",
            mapping_filters=mapping_filters,
            mapping_configs=mapping_configs,
        )
        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def invoke(self, input: Input) -> Sequence[Output]:
        pass

    def submit(self):
        pass


class SubGraphMapping(Mapping):
    """A Process Component that mapping data to relation type.

    Args:
        spg_type_name: The SPG type name import from SPGTypeHelper.
    Examples:
        mapping = SubGraphMapping(
                    spg_type_name=DEFAULT.App,
                ).add_mapping_field("id", DEFAULT.App.id) \
                 .add_mapping_field("name", DEFAULT.App.name) \
                 .add_mapping_field("useCert", DEFAULT.App.useCert)
                 .add_predicting_field(

    """
    
    """"""
    spg_type_name: Union[str, SPGTypeHelper]

    mapping: Dict[str, str] = dict()

    filters: List[Tuple[str, str]] = list()

    subject_fusing_strategy: Optional[FuseOp] = None

    predicate_predicting_strategies: Dict[str, PredictOp] = dict()

    object_fuse_strategies: Dict[str, FuseOp] = dict()

    @property
    def input_types(self) -> Input:
        return Union[Dict[str, str], SPGRecord]

    @property
    def output_types(self) -> Output:
        return SPGRecord

    @property
    def input_keys(self):
        return None

    @property
    def output_keys(self):
        return self.output_fields

    def set_fusing_strategy(self, fusing_strategy: FuseOp):
        self.subject_fusing_strategy = fusing_strategy
        return self

    def add_mapping_field(
        self,
        source_field: str,
        target_field: Union[str, PropertyHelper],
        fusing_strategy: Union[FusingStrategyEnum, FuseOp] = None,
    ):
        """Adds a field mapping from source data to property of spg_type.

        :param source_field: The source field to be mapped.
        :param target_field: The target field to map the source field to.
        :return: self
        """
        self.mapping[target_field] = source_field
        self.object_fuse_strategies[target_field] = fusing_strategy
        return self

    def add_predicting_field(
        self,
        target_field: Union[str, PropertyHelper],
        predicting_strategy: PredictOp = None,
    ):
        self.predicate_predicting_strategies[target_field] = predicting_strategy
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

    def to_rest(self):
        """
        Transforms `SubGraphMapping` to REST model `SpgTypeMappingNodeConfig`.
        """
        from knext.client.schema import SchemaClient
        client = SchemaClient()
        spg_type = client.query_spg_type(self.spg_type_name)

        mapping_filters = [
            rest.MappingFilter(column_name=name, column_value=value)
            for name, value in self.filters
        ]
        mapping_configs = []
        for tgt_name, src_name in self.mapping.items():
            fusing_strategy = self.object_fuse_strategies.get(tgt_name, None)
            if isinstance(fusing_strategy, FuseOp):
                strategy_config = rest.OperatorFusingConfig(
                    operator_config=fusing_strategy.to_rest()
                )
            elif not self.subject_fusing_strategy:
                object_type_name = spg_type.properties[tgt_name].object_type_name
                if object_type_name in FuseOp.bind_schemas:
                    op_name = FuseOp.bind_schemas[object_type_name]
                    op = FuseOp.by_name(op_name)()
                    strategy_config = rest.OperatorFusingConfig(
                        operator_config=op.to_rest()
                    )
                else:
                    strategy_config = rest.NewInstanceFusingConfig()
            else:
                raise ValueError(f"Invalid fusing_strategy [{fusing_strategy}].")
            mapping_configs.append(
                rest.MappingConfig(
                    source=src_name,
                    target=tgt_name,
                    strategy_config=strategy_config,
                )
            )

        predicting_configs = []
        for predicate_name, predicting_strategy in self.predicate_predicting_strategies.items():
            if isinstance(predicting_strategy, PredictOp):
                strategy_config = rest.OperatorPredictingConfig(
                    operator_config=predicting_strategy.to_rest()
                )
            elif not predicting_strategy:
                object_type_name = spg_type.properties[predicate_name].object_type_name
                if (self.spg_type_name, predicate_name, object_type_name) in PredictOp.bind_schemas:
                    op_name = PredictOp.bind_schemas[(self.spg_type_name, predicate_name, object_type_name)]
                    op = PredictOp.by_name(op_name)()
                    strategy_config = rest.OperatorPredictingConfig(
                        operator_config=op.to_rest()
                    )
                else:
                    strategy_config = None
            else:
                raise ValueError(f"Invalid predicting_strategy [{predicting_strategy}].")
            if strategy_config:
                predicting_configs.append(
                    rest.PredictingConfig(target=predicate_name,predicting_config=strategy_config)
                )

        if isinstance(self.subject_fusing_strategy, FuseOp):
            fusing_config = rest.OperatorFusingConfig(
                operator_config=self.fusing_strategy.to_rest()
            )
        elif not self.subject_fusing_strategy:
            if self.spg_type_name in FuseOp.bind_schemas:
                op_name = FuseOp.bind_schemas[self.spg_type_name]
                op = FuseOp.by_name(op_name)()
                fusing_config = rest.OperatorFusingConfig(
                    operator_config=op.to_rest()
                )
            else:
                fusing_config = rest.NewInstanceFusingConfig()
        else:
            raise ValueError(f"Invalid fusing_strategy [{self.subject_fusing_strategy}].")

        config = rest.SubGraphMappingNodeConfig(
            spg_type=self.spg_type_name,
            mapping_filters=mapping_filters,
            mapping_configs=mapping_configs,
            subject_fusing_config=fusing_config,
            predicting_configs=predicting_configs
        )
        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def invoke(self, input: Input) -> Sequence[Output]:
        pass

    def submit(self):
        pass
