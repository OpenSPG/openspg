from typing import Dict, List

from knext.builder.operator.spg_record import SPGRecord
from knext.schema.model.base import BaseSpgType


class Node(object):
    id: str
    name: str
    label: str
    properties: Dict[str, str]

    def __init__(self, _id: str, name: str, label: str, properties: Dict[str, str]):
        self.name = name
        self.label = label
        self.properties = properties
        if not _id:
            _id = id(self)
        self.id = _id

    @classmethod
    def from_spg_record(cls, spg_record: SPGRecord):
        return cls(
                _id=spg_record.get_property("id"),
                name=spg_record.get_property("name"),
                label=spg_record.spg_type_name,
                properties=spg_record.properties,
            )

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "label": self.label,
            "properties": self.properties
        }


class Edge(object):
    from_id: str
    from_type: str
    to_id: str
    to_type: str
    label: str
    properties: Dict[str, str]

    def __init__(self, from_node: Node, to_node: Node, label: str, properties: Dict[str, str]):
        self.from_id = from_node.id
        self.from_type = from_node.label
        self.to_id = to_node.id
        self.to_type = to_node.label
        self.label = label
        self.properties = properties

    @classmethod
    def from_spg_record(cls, from_node: Node, object_record: SPGRecord, label: str):
        to_node = Node.from_spg_record(object_record)

        return cls(
            from_node=from_node,
            to_node=to_node,
            label=f'{from_node.label}_{label}_{object_record.spg_type_name}',
            properties={}
        )

    def to_dict(self):
        return {
            "fromId": self.from_id,
            "toId": self.to_id,
            "fromType": self.from_type,
            "toType": self.to_type,
            "label": self.label,
            "properties": self.properties
        }


class SubGraph(object):
    nodes: List[Node] = list()
    edges: List[Edge] = list()

    def __init__(self, nodes: List[Node], edges: List[Edge]):
        self.nodes = nodes
        self.edges = edges

    def to_dict(self):
        return {
            "resultNodes": [n.to_dict() for n in self.nodes],
            "resultEdges": [e.to_dict() for e in self.edges]
        }

    @classmethod
    def from_spg_record(cls, spg_types: Dict[str, BaseSpgType], spg_records: List[SPGRecord]):
        nodes, edges = set(), set()
        for subject_record in spg_records:
            from_node = Node.from_spg_record(subject_record)
            nodes.add(from_node)
            spg_type_name = subject_record.spg_type_name
            spg_type = spg_types.get(spg_type_name)
            for prop_name, prop_value in subject_record.properties.items():
                object_type_name = spg_type.properties.get(prop_name).object_type_name
                for object_record in spg_records:
                    if object_record.spg_type_name == object_type_name and object_record.get_property("name") == prop_value:
                        edge = Edge.from_spg_record(from_node, object_record, prop_name)
                        edges.add(edge)
        return cls(nodes=list(nodes), edges=list(edges)).to_dict()
