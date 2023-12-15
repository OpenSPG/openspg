from collections import defaultdict
from enum import Enum
from typing import Union, Dict, List, Tuple, Sequence

from knext import rest
from knext.common.runnable import Input, Output

from knext.common.schema_helper import SPGTypeHelper, PropertyHelper
from knext.component.builder.base import Mapping
from knext.operator.op import LinkOp, FuseOp
from knext.operator.spg_record import SPGRecord


class MappingTypeEnum(str, Enum):
    SPGType = "SPG_TYPE"
    Relation = "RELATION"


class LinkStrategyEnum(str, Enum):
    IDEquals = "ID_EQUALS"


SPG_TYPE_BASE_FIELDS = ["id"]

RELATION_BASE_FIELDS = ["src_id", "dst_id"]


class SPGTypeMapping(Mapping):
    """A Process Component that mapping data to entity/event/concept type.

    Args:
        spg_type_name: The SPG type name import from SPGTypeHelper.
    Examples:
        mapping = SPGTypeMapping(
            spg_type_name=DEFAULT.App
        ).add_field("id", DEFAULT.App.id) \
            .add_field("id", DEFAULT.App.name) \
            .add_field("riskMark", DEFAULT.App.riskMark) \
            .add_field("useCert", DEFAULT.App.useCert)

    """

    spg_type_name: Union[str, SPGTypeHelper]

    fuse_op: FuseOp = None

    mapping: Dict[str, str] = dict()

    filters: List[Tuple[str, str]] = list()

    link_strategies: Dict[str, Union[LinkStrategyEnum, LinkOp]] = dict()

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

    def add_field(
        self,
        source_field: str,
        target_field: Union[str, PropertyHelper],
        link_strategy: Union[LinkStrategyEnum, LinkOp] = None,
    ):
        """Adds a field mapping from source data to property of spg_type.

        :param source_field: The source field to be mapped.
        :param target_field: The target field to map the source field to.
        :return: self
        """
        self.mapping[target_field] = source_field
        self.link_strategies[target_field] = link_strategy
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
        Transforms `EntityMappingComponent` to REST model `MappingNodeConfig`.
        """
        schema = {}
        # TODO generate schema with link_strategy

        mapping_filters = [
            rest.MappingFilter(column_name=name, column_value=value)
            for name, value in self.filters
        ]
        mapping_configs = []
        for tgt_name, src_name in self.mapping.items():
            link_strategy = self.link_strategies.get(tgt_name, None)
            if isinstance(link_strategy, LinkOp):
                property_normalizer = rest.OperatorPropertyNormalizerConfig(
                    config=link_strategy.to_rest()
                )
            elif link_strategy == LinkStrategyEnum.IDEquals:
                property_normalizer = rest.IdEqualsPropertyNormalizerConfig()
            elif not link_strategy:
                property_normalizer = None
            else:
                raise ValueError(f"Invalid link_strategy {link_strategy}")
            mapping_configs.append(
                rest.MappingConfig(
                    source=src_name,
                    target=tgt_name,
                    normalizer_config=property_normalizer,
                )
            )

        config = rest.SpgTypeMappingNodeConfig(
            spg_type=self.spg_type_name,
            mapping_filters=mapping_filters,
            mapping_configs=mapping_configs,
        )
        return rest.Node(**super().to_dict(), node_config=config)

    def invoke(self, input: Input) -> Sequence[Output]:
        pass

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def submit(self):
        pass


class RelationMapping(Mapping):
    """A Process Component that mapping data to relation type.

    Args:
        subject_name: The subject name import from SPGTypeHelper.
        predicate_name: The predicate name.
        object_name: The object name import from SPGTypeHelper.
    Examples:
        mapping = RelationMappingComponent(
                    subject_name=DEFAULT.App,
                    predicate_name=DEFAULT.App.useCert,
                    object_name=DEFAULT.Cert,
                ).add_field("src_id", "srcId") \
                 .add_field("dst_id", "dstId")

    """

    subject_name: Union[str, SPGTypeHelper]
    predicate_name: Union[str, PropertyHelper]
    object_name: Union[str, SPGTypeHelper]

    mapping: Dict[str, str] = dict()

    filters: List[Tuple[str, str]] = list()

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

    spg_type_name: Union[str, SPGTypeHelper]

    mapping: Dict[str, str] = dict()

    filters: List[Tuple[str, str]] = list()

    link_strategies: Dict[str, Union[LinkStrategyEnum, LinkOp]] = dict()

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

    def add_field(
        self,
        source_field: str,
        target_field: Union[str, PropertyHelper],
        link_strategy: Union[LinkStrategyEnum, LinkOp] = None,
    ):
        """Adds a field mapping from source data to property of spg_type.

        :param source_field: The source field to be mapped.
        :param target_field: The target field to map the source field to.
        :return: self
        """
        self.mapping[target_field] = source_field
        self.link_strategies[target_field] = link_strategy
        return self

    def invoke(self, input: Input) -> Sequence[Output]:
        pass

    def to_rest(self) -> rest.Node:
        # TODO generate schema with link_strategy

        mapping_filters = [
            rest.MappingFilter(column_name=name, column_value=value)
            for name, value in self.filters
        ]
        mapping_configs = [
            rest.MappingConfig(source=src_name, target=tgt_names)
            for src_name, tgt_names in self.mapping.items()
        ]

        node_configs = []

        config = rest.SubGraphMappingNodeConfig(
            children_node_configs=node_configs,
        )
        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def submit(self):
        pass
