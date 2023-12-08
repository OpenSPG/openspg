from abc import ABC

from knext import rest


class RESTable(ABC):

    @property
    def upstream_types(self):
        raise NotImplementedError("To be implemented in subclass")

    @property
    def downstream_types(self):
        raise NotImplementedError("To be implemented in subclass")

    def to_rest(self):
        raise NotImplementedError("To be implemented in subclass")

    @classmethod
    def from_rest(cls, node: rest.Node):
        raise NotImplementedError("To be implemented in subclass")

    def submit(self):
        raise NotImplementedError("To be implemented in subclass")
