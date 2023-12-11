from typing import Dict

from knext.component.builder.base import SinkWriter


class KGSinkWriter(SinkWriter):
    """The Sink Component that writing data to KG storage.

    Args:
        None
    Examples:
        sink = KGSinkWriter()

    """

    @property
    def input_types(self):
        return Dict[str, str]

    @property
    def output_types(self):
        return None

    def invoke(self, input):
        pass

    def to_rest(self):
        """Transforms `SinkToKgComponent` to REST model `GraphStoreSinkNodeConfig`."""
        return dict(
            {
                "properties": {},
            },
            **super().to_dict(),
        )

    def submit(self):
        pass
