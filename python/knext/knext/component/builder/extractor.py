import json
from typing import Union, Dict, List, Sequence

from knext.client.operator import OperatorClient
from knext.common.runnable import Input, Output
from knext.component.builder.base import SPGExtractor
from knext.operator.spg_record import SPGRecord
from knext import rest
from knext.operator.op import PromptOp, ExtractOp

try:
    from nn4k.invoker.base import NNInvoker  # noqa: F403
except ImportError:
    pass


class LLMBasedExtractor(SPGExtractor):
    """A Process Component that transforming unstructured data into structured data.

    Examples:
        extract = UserDefinedExtractor(
                    output_fields=["id", 'riskMark', 'useCert']
                ).set_operator("DemoExtractOp")

    """

    """Knowledge extract operator of this component."""
    llm: NNInvoker
    """PromptOps."""
    prompt_ops: List[PromptOp]

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
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being invoked separately."
        )

    def submit(self):
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being submitted separately."
        )

    def to_rest(self):
        """Transforms `LLMBasedExtractor` to REST model `ExtractNodeConfig`."""
        params = dict()
        params["model_config"] = json.dumps(self.llm._nn_config)
        params["prompt_config"] = json.dumps([OperatorClient().serialize(op.to_rest()) for op in self.prompt_ops])
        from knext.operator.builtin.online_runner import _BuiltInOnlineExtractor

        extract_op = _BuiltInOnlineExtractor(params)
        config = rest.UserDefinedExtractNodeConfig(operator_config=extract_op.to_rest())

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass


class UserDefinedExtractor(SPGExtractor):
    """A Process Component that transforming unstructured data into structured data.

    Examples:
        extract = UserDefinedExtractor(
                    extract_op=DemoExtractOp(params={"config": "1"})
                )

    """

    """Knowledge extract operator of this component."""
    extract_op: ExtractOp

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return Union[Dict[str, str], SPGRecord]

    def invoke(self, input: Input) -> Sequence[Output]:
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being invoked separately."
        )

    def submit(self):
        raise NotImplementedError(
            f"{self.__class__.__name__} does not support being submitted separately."
        )

    def to_rest(self):
        """Transforms `UserDefinedExtractor` to REST model `UserDefinedExtractNodeConfig`."""
        operator_config = self.extract_op.to_rest()
        config = rest.UserDefinedExtractNodeConfig(operator_config=operator_config)

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        return cls()
