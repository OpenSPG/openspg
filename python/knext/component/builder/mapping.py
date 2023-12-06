from abc import ABC
from typing import Union, Dict, List, Tuple

from knext.component.base import RESTable, Component, ComponentTypeEnum, ComponentLabelEnum, Runnable
from knext.component.builder.extractor import SPGExtractor
from knext.component.builder.sink_writer import SinkWriter
from knext.component.builder.source_reader import SourceReader
from knext.operator.spg_record import SPGRecord


class Mapping(RESTable, Component, ABC):

    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SinkWriter]

    @property
    def type(self):
        return ComponentTypeEnum.Builder

    @property
    def label(self):
        return ComponentLabelEnum.Mapping



class SPGTypeMapping(Runnable[Dict[str, str], SPGRecord], Mapping):
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

    mapping: Dict[str, str] = dict()

    filters: List[Tuple[str, str]] = list()

    def add_field(self, source_field: str, target_field: Union[str, PropertyHelper], link_op: LinkOp,
                  norm_op: NormalizeOp):
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

    def to_rest(self):
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


class RelationMappingComponent(Component):
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

    RELATION_BASE_FIELDS = ["srcId", "dstId"]

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
            mapping_type=MappingTypeEnum.Relation,
            mapping_filters=mapping_filters,
            mapping_schemas=mapping_schemas,
            mapping_configs=mapping_configs,
        )
        return rest.Node(**super().to_dict(), node_config=config)
