from typing import Union

from nn4k.invoker import NNInvoker


class OpenAIInvoker(NNInvoker):

    @classmethod
    def from_config(cls, nn_config: Union[str, dict]):
        import openai

        o = cls.__new__(cls)
        o._openai_client = openai.OpenAI()
        o._open_ai_model = nn_config.get("open_ai_model")
        # TODO config key
        # TODO complete
        return o

    def remote_inference(self, input, **kwargs):
        # TODO
        pass
