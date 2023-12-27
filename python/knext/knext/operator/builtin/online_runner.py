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
        self.max_retry_times = int(self.params.get("max_retry_times", "3"))

    def load_model(self):
        model_config = json.loads(self.params["model_config"])
        return LLMInvoker.from_config(model_config)

    def load_operator(self):
        import importlib.util

        prompt_config = json.loads(self.params["prompt_config"])
        prompt_ops = []
        for op_config in prompt_config:
            spec = importlib.util.spec_from_file_location(
                op_config["modulePath"], op_config["filePath"]
            )
            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module)

            op_clazz = getattr(module, op_config["className"])
            params = op_config.get("params", {})
            op_obj = op_clazz(**params)
            prompt_ops.append(op_obj)

        return prompt_ops

    def invoke(self, record: Dict[str, str]) -> List[SPGRecord]:

        collector = []
        input_params = [record]
        for op in self.prompt_ops:
            next_params = []
            for input_param in input_params:
                retry_times = 0
                while retry_times < self.max_retry_times:
                    try:
                        query = op.build_prompt(input_param)
                        # response = self.model.remote_inference(query)
                        response = "test"
                        collector.extend(op.parse_response(response))
                        next_params.extend(
                            op.build_next_variables(input_param, response)
                        )
                        break
                    except Exception as e:
                        retry_times += 1
                        raise e
            input_params = next_params
        return collector
