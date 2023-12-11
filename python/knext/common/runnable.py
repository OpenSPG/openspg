from typing import TypeVar, Sequence, Generic, Type

from pydantic import BaseConfig, BaseModel

Other = TypeVar("Other")

Input = TypeVar("Input", contravariant=True)
Output = TypeVar("Output", covariant=True)


class Runnable(Generic[Input, Output], BaseModel):

    last: bool = False

    @property
    def input_types(self) -> Type[Input]:
        return

    @property
    def output_types(self) -> Type[Output]:
        return

    def invoke(self, input: Input) -> Sequence[Output]:
        raise NotImplementedError("To be implemented in subclass")

    def __rshift__(self, other: Other):
        raise NotImplementedError("To be implemented in subclass")

    class Config(BaseConfig):
        arbitrary_types_allowed = True
