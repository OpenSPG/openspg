from knext import rest
from knext.component.builder.base import SinkWriter
from knext.operator.spg_record import SPGRecord


class KGSinkWriter(SinkWriter):
    """The Sink Component that writing data to KG storage.

    Args:
        None
    Examples:
        sink = KGSinkWriter()

    """

    @property
    def input_types(self):
        return SPGRecord

    @property
    def output_types(self):
        return None

    def to_rest(self):
        """Transforms `KGSinkWriter` to REST model `GraphStoreSinkNodeConfig`."""
        config = rest.GraphStoreSinkNodeConfig()
        return rest.Node(**super().to_dict(), node_config=config)

    @classmethod
    def from_rest(cls, node: rest.Node):
        pass

    def invoke(self, input):
        pass

    def submit(self):
        pass
