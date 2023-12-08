from pydantic import BaseConfig, BaseModel


class Runnable(BaseModel):

    last: bool = False

    @property
    def input_types(self):
        return

    @property
    def output_types(self):
        return

    def invoke(self, input):
        raise NotImplementedError("To be implemented in subclass")

    def __rshift__(self, other):
        raise NotImplementedError("To be implemented in subclass")

    class Config(BaseConfig):

        arbitrary_types_allowed = True