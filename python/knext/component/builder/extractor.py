import json
from typing import Union, Dict, List, Sequence

from knext.client.operator import OperatorClient
from knext.common.runnable import Input, Output
from knext.component.builder.base import SPGExtractor
from knext.operator.spg_record import SPGRecord
from knext import rest
from knext.operator.op import PromptOp, ExtractOp

# try:
from nn4k.invoker.base import LLMInvoker, NNInvoker  # noqa: F403

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
        raise NotImplementedError(f"{self.__class__.__name__} does not support being invoked separately.")

    def submit(self):
        raise NotImplementedError(f"{self.__class__.__name__} does not support being submitted separately.")

    def to_rest(self):
        """Transforms `LLMBasedExtractor` to REST model `ExtractNodeConfig`."""
        params = {}
        params["model_config"] = json.dumps(self.llm._nn_config)
        api_client = OperatorClient()._rest_client.api_client
        params["prompt_config"] = json.dumps([api_client.sanitize_for_serialization(op.to_rest()) for op in self.prompt_ops])
        from knext.operator.builtin.online_runner import _BuiltInOnlineExtractor
        extract_op = _BuiltInOnlineExtractor(params)
        print(extract_op.eval({"input": "甲状腺结节是指在甲状腺内的肿块，可随吞咽动作随甲状腺而上下移动，是临床常见的病症，可由多种病因引起。临床上有多种甲状腺疾病，如甲状腺退行性变、炎症、自身免疫以及新生物等都可以表现为结节。甲状腺结节可以单发，也可以多发，多发结节比单发结节的发病率高，但单发结节甲状腺癌的发生率较高。患者通常可以选择在普外科，甲状腺外科，内分泌科，头颈外科挂号就诊。有些患者可以触摸到自己颈部前方的结节。在大多情况下，甲状腺结节没有任何症状，甲状腺功能也是正常的。甲状腺结节进展为其它甲状腺疾病的概率只有1%。有些人会感觉到颈部疼痛、咽喉部异物感，或者存在压迫感。当甲状腺结节发生囊内自发性出血时，疼痛感会更加强烈。治疗方面，一般情况下可以用放射性碘治疗，复方碘口服液(Lugol液)等，或者服用抗甲状腺药物来抑制甲状腺激素的分泌。目前常用的抗甲状腺药物是硫脲类化合物，包括硫氧嘧啶类的丙基硫氧嘧啶(PTU)和甲基硫氧嘧啶(MTU)及咪唑类的甲硫咪唑和卡比马唑。"}))
        exit()
        config = rest.ExtractNodeConfig(
            output_fields=self.output_fields, operator_config=extract_op.to_rest()
        )

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
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

    def invoke(self, input: Input) -> Sequence[Output]:
        raise NotImplementedError(f"{self.__class__.__name__} does not support being invoked separately.")

    def submit(self):
        raise NotImplementedError(f"{self.__class__.__name__} does not support being submitted separately.")

    def to_rest(self):
        """Transforms `UserDefinedExtractor` to REST model `ExtractNodeConfig`."""
        operator_config = self.extract_op.to_rest()
        config = rest.UserDefinedExtractNodeConfig(
            output_fields=self.output_fields, operator_config=operator_config
        )

        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        return cls()
