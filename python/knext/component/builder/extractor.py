from typing import Union, Dict, List

from knext.component.builder.base import SPGExtractor
from knext.operator.spg_record import SPGRecord
from nn4k.invoker.base import NNInvoker
from knext import rest
from knext.component.base import SPGTypeHelper, PropertyHelper
from knext.operator.op import PromptOp, ExtractOp


class LLMBasedExtractor(SPGExtractor):
    """A Process Component that transforming unstructured data into structured data.

    Examples:
        extract = UserDefinedExtractor(
                    output_fields=["id", 'riskMark', 'useCert']
                ).set_operator("DemoExtractOp")

    """

    """All output column names after knowledge extraction processing."""
    output_fields: List[str]
    """Knowledge extract operator of this component."""
    llm: NNInvoker

    prompt_ops: List[PromptOp]

    spg_type_name: Union[str, SPGTypeHelper]

    property_names: List[Union[str, PropertyHelper]]

    @property
    def input_types(self):
        return Dict[str, str]

    @property
    def output_types(self):
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

    def invoke(self, input):
        pass

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def submit(self):
        pass


class UserDefinedExtractor(SPGExtractor):
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
    def input_types(self):
        return Dict[str, str]

    @property
    def output_types(self):
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
