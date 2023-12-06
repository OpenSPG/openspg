from abc import ABC
from ctypes import Union

from knext.component.base import RESTable, Component


class SinkWriter(RESTable, Component, ABC):

    @property
    def upstream_types(self):
        return Union[Mapping, Evaluator]

    @property
    def downstream_types(self):
        return None

    @property
    def type(self):
        return ComponentTypeEnum.Builder

    @property
    def label(self):
        return ComponentLabelEnum.SinkWriter



class KGSinkWriter(Runnable[Dict[str, str], None], SinkWriter):
    """The Sink Component that writing data to KG storage.

    Args:
        None
    Examples:
        sink = KGSinkWriter()

    """

    @property
    def input_types(self) -> Input:
        return Dict[str, str]

    @property
    def output_types(self) -> Output:
        return None

    def invoke(self, input: Input) -> Output:
        pass

    def to_rest(self):
        """Transforms `SinkToKgComponent` to REST model `GraphStoreSinkNodeConfig`."""
        return dict(
            {
                "properties": {},
            },
            **super().to_dict(),
        )
