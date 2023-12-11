from abc import ABC


class SchemaHelper(ABC):

    __type_name__: str

    def __init__(self, type_name: str):
        self.__type_name__ = type_name


class SPGTypeHelper(SchemaHelper):
    def __init__(self, type_name: str):
        super().__init__(type_name)


class PropertyHelper(SchemaHelper):
    def __init__(self, type_name: str):
        super().__init__(type_name)
