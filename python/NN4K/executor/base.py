from abc import ABC, abstractmethod


class ModelExecutor(ABC):
    """
    对应xflow AntLLM
    """

    @classmethod
    def from_config(cls,
                    args='sys',
                    **kwargs):
        pass

    def __init__(self,
             backend_model,
             backend_tokenizer,
             init_args,
             **kwargs):
        self.backend_model = backend_model
        self.backend_tokenizer = backend_tokenizer
        self.init_args = init_args
        self.kwargs = kwargs


class LLMExecutor(ModelExecutor):

    @abstractmethod
    def sft_train(self, args=None, callbacks=None, **kwargs):
        raise NotImplementedError("")

    @abstractmethod
    def rl_tuning(self, args=None, callbacks=None, **kwargs):
        raise NotImplementedError("")

    @abstractmethod
    def batch_inference(self, args, **kwargs):
        pass

    @abstractmethod
    def inference(self, input, inference_args, **kwargs):
        raise NotImplementedError()


class HfLLMExecutor(ModelExecutor):

    pass


class DeepKEExecutor(ModelExecutor):

    pass


