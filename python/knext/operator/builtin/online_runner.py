import json
from typing import Dict, List

from knext.api.operator import ExtractOp
from knext.operator.spg_record import SPGRecord
from nn4k.invoker import LLMInvoker


class _BuiltInOnlineExtractor(ExtractOp):

    def __init__(self, params: Dict[str, str] = None):
        """

        Args:
            params: {"model_name": "openai", "token": "**"}
        """
        super().__init__(params)
        self.model = self.load_model()
        self.prompt_ops = self.load_operator()

    def load_model(self):
        model_config = json.loads(self.params["model_config"])
        return LLMInvoker.from_config(model_config)

    def load_operator(self):
        import importlib.util
        prompt_config = json.loads(self.params["prompt_config"])
        prompt_ops = []
        for op_config in prompt_config:
            # 创建模块规范和模块对象
            spec = importlib.util.spec_from_file_location(op_config["modulePath"], op_config["filePath"])
            module = importlib.util.module_from_spec(spec)

            # 加载模块
            spec.loader.exec_module(module)

            op_clazz = getattr(module, op_config["className"])
            op_obj = op_clazz(**op_config["params"])
            prompt_ops.append(op_obj)

        return prompt_ops


    def eval(self, record: Dict[str, str]) -> List[SPGRecord]:

        # 对于单条数据【record】执行多层抽取
        # 每次抽取都需要执行op.build_prompt()->model.predict()->op.parse_response()流程
        # 且每次抽取后可能得到多条结果，下次抽取需要对多条结果分别进行抽取。
        collector = []
        input_params = [record]
        # 循环所有prompt算子，算子数量决定对单条数据执行几层抽取
        for op in self.prompt_ops:
            next_params = []
            # record_list可能有多条数据，对多条数据都要进行抽取
            for input_param in input_params:
                # 生成完整query
                query = op.build_prompt(input_param)
                # 模型预测，生成模型输出结果
                response = self.model.remote_inference(query)
                # response = "test"
                # response = '{"spo": [{"subject": "甲状腺结节", "predicate": "常见症状", "object": "头疼"}]}'
                # 模型结果的后置处理，可能会拆分成多条数据 List[dict[str, str]]
                if hasattr(op, "parse_response"):
                    collector.extend(op.parse_response(response))
                if hasattr(op, "build_variables"):
                    next_params.extend(op.build_variables(input_param, response))

            input_params = next_params
        return collector


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
