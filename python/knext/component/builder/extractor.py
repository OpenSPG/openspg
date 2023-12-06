from abc import ABC
from typing import Union, Mapping, Dict, List

from NN4K.invoker.base import ModelInvoker
from knext import rest
from knext.component.base import RESTable, Component, ComponentTypeEnum, ComponentLabelEnum, Runnable, Input, Output
from knext.component.builder.source_reader import SourceReader
from knext.core.builder.operator.model.op import PromptOp


class SPGExtractor(RESTable, Component, ABC):

    @property
    def upstream_types(self):
        return Union[SourceReader, SPGExtractor]

    @property
    def downstream_types(self):
        return Union[SPGExtractor, Mapping]

    @property
    def type(self):
        return ComponentTypeEnum.Builder

    @property
    def label(self):
        return ComponentLabelEnum.Extractor


class LLMBasedExtractor(Runnable, SPGExtractor):
    """A Process Component that transforming unstructured data into structured data.

    Examples:
        extract = UserDefinedExtractor(
                    output_fields=["id", 'riskMark', 'useCert']
                ).set_operator("DemoExtractOp")

    """

    """All output column names after knowledge extraction processing."""
    output_fields: List[str]
    """Knowledge extract operator of this component."""
    llm: ModelInvoker

    prompt_ops: List[PromptOp]

    spg_type_name: Union[str, SPGTypeHelper]

    property_names: List[Union[str, PropertyHelper]]

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return SPGRecord

    def to_rest(self):
        """Transforms `LLMBasedExtractor` to REST model `ExtractNodeConfig`."""
        # operator_config = client._generate_op_config(
        #     op_name=self.extract_op.name, params=self.extract_op.params
        # )
        operator_config = {}
        config = rest.ExtractNodeConfig(
            output_fields=self.output_fields, operator_config=operator_config
        )

        return rest.Node(**super().to_dict(), node_config=config)

    def invoke(self, input: Input) -> Output:
        pass

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def submit(self):
        pass


class UserDefinedExtractor(Runnable[Dict[str, str], Dict[str, str]], SPGExtractor):
    """A Process Component that transforming unstructured data into structured data.

    Examples:
        extract = UserDefinedExtractor(
                    output_fields=["id", 'riskMark', 'useCert']
                ).set_operator("DemoExtractOp")

    """

    """All output column names after knowledge extraction processing."""
    output_fields: List[str]
    """Knowledge extract operator of this component."""
    extract_op: ExtractOp

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return Dict[str, str]

    @property
    def name(self):
        return self.__class__.__name__

    def set_operator(self, op_name: str, params: Dict[str, str] = None):
        """Sets knowledge extract operator to this component."""
        self.extract_op = ExtractOp.by_name(op_name)(params)
        return self

    def to_rest(self):
        """Transforms `UserDefinedExtractor` to REST model `ExtractNodeConfig`."""
        # operator_config = client._generate_op_config(
        #     op_name=self.extract_op.name, params=self.extract_op.params
        # )
        operator_config = {}
        config = rest.ExtractNodeConfig(
            output_fields=self.output_fields, operator_config=operator_config
        )

        return rest.Node(**super().to_dict(), node_config=config)
