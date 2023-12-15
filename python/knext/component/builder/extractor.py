from typing import Union, Dict, List, Sequence

from knext.common.runnable import Input, Output
from knext.common.schema_helper import SPGTypeHelper, PropertyHelper
from knext.component.builder.base import SPGExtractor
from knext.operator.spg_record import SPGRecord
from knext import rest
from knext.operator.op import PromptOp, ExtractOp

# try:
from nn4k.invoker.base import NNInvoker  # noqa: F403
# except ImportError:
#     pass


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
    """PromptOps"""
    prompt_ops: List[PromptOp]

    # spg_type_name: Union[str, SPGTypeHelper] = None
    #
    # property_names: List[Union[str, PropertyHelper]] = None

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return Union[Dict[str, str], SPGRecord]

    @property
    def input_keys(self):
        return None

    @property
    def output_keys(self):
        return self.output_fields

    def invoke(self, input: Input) -> Sequence[Output]:
        pass

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
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return Union[Dict[str, str], SPGRecord]

    @property
    def input_keys(self):
        return None

    @property
    def output_keys(self):
        return self.output_fields

    def set_operator(self, op_name: str, params: Dict[str, str] = None):
        """Sets knowledge extract operator to this component."""
        self.extract_op = ExtractOp.by_name(op_name)(params)
        return self

    def invoke(self, input: Input) -> Output:
        pass

    def to_rest(self):
        """Transforms `UserDefinedExtractor` to REST model `ExtractNodeConfig`."""
        # operator_config = client._generate_op_config(
        #     op_name=self.extract_op.name, params=self.extract_op.params
        # )
        operator_config = {}
        config = rest.UserDefinedExtractNodeConfig(
            output_fields=self.output_fields, operator_config=operator_config
        )

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def submit(self):
        pass
