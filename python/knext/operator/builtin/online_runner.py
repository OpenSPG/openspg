from typing import Dict, List

from knext.api.operator import ExtractOp
from knext.models.runtime.vertex import Vertex
from NN4K.invoker.base import ModelInvoker


class BuiltInOnlineLLMBasedExtractOp(ExtractOp):
    def __init__(self, params: Dict[str, str] = None):
        """

        Args:
            params: {"model_name": "openai", "token": "**"}
        """
        super().__init__(params)
        self.model = ModelInvoker.from_config(params)
        self.prompt_ops = []

    def eval(self, record: Dict[str, str]) -> List[Vertex]:

        # 对于单条数据【record】执行多层抽取
        # 每次抽取都需要执行op.build_prompt()->model.predict()->op.parse_response()流程
        # 且每次抽取后可能得到多条结果，下次抽取需要对多条结果分别进行抽取。
        record_list = [record]
        # 循环所有prompt算子，算子数量决定对单条数据执行几层抽取
        for index, op in enumerate(self.prompt_ops):
            extract_result_list = []
            # record_list可能有多条数据，对多条数据都要进行抽取
            while record_list:
                _record = record_list.pop()
                # 生成完整query
                query = op.build_prompt(_record)
                # 模型预测，生成模型输出结果
                response = self.model.inference(query)
                # response = self.model[op.name]
                # 模型结果的后置处理，可能会拆分成多条数据 List[dict[str, str]]
                result_list = op.parse_response(response)
                # 把输入的record和模型输出的result拼成一个新的dict，作为这次抽取最终结果
                for result in result_list:
                    _ = _record.copy()
                    _.update(result)
                    extract_result_list.append(_)
            # record_list为空时，执行下一层抽取
            if index == len(self.prompt_ops) - 1:
                return extract_result_list
            else:
                record_list.extend(extract_result_list)
