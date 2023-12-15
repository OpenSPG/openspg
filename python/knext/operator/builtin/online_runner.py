import json
import sys
from typing import Dict, List

from knext.common.class_register import register_from_package

from knext.api.operator import ExtractOp
from knext.operator.base import BaseOp
from knext.operator.spg_record import SPGRecord
from nn4k.invoker import LLMInvoker


class BuiltInOnlineLLMBasedExtractOp(ExtractOp):
    def __init__(self, params: Dict[str, str] = None):
        """

        Args:
            params: {"model_name": "openai", "token": "**"}
        """
        super().__init__(params)
        model_config = json.loads(params["model_config"])
        prompt_config = json.loads(params["prompt_config"])
        register_from_package(params["operator_dir"], BaseOp)
        self.model = LLMInvoker.from_config(model_config)
        self.prompt_ops = [BaseOp.by_name(config["className"])(**config["params"]) for config in prompt_config]

    def eval(self, record: Dict[str, str]) -> List[SPGRecord]:

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
                response = self.model.remote_inference(query)
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


if __name__ == '__main__':
    config = {
        "invoker_type": "OpenAI",
        "openai_api_key": "EMPTY",
        "openai_api_base": "http://localhost:38000/v1",
        "openai_model_name": "vicuna-7b-v1.5",
        "openai_max_tokens": 1000
    }
    model = LLMInvoker.from_config(config)
    query = """
    已知SPO关系包括:[录音室专辑(录音室专辑)-发行年份-文本]。从下列句子中提取定义的这些关系。最终抽取结果以json格式输出。
input:《范特西》是周杰伦的第二张音乐专辑，由周杰伦担任制作人，于2001年9月14日发行，共收录《爱在西元前》《威廉古堡》《双截棍》等10首歌曲 [1]。
输出格式为:{"spo":[{"subject":,"predicate":,"object":},]}
"output":
    """

    response = model.remote_inference(query)
    print(response)
