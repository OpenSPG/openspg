# -*- coding: utf-8 -*-
# Copyright 2023 Ant Group CO., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

import pprint
from typing import Dict, Any, List


class SPGRecord:
    """Data structure in operator, used to store entity information."""

    def __init__(self, spg_type_name: str = "", properties: Dict[str, str] = None):
        self._spg_type_name = None
        self._properties = None

        self._spg_type_name = spg_type_name
        if properties is None:
            properties = {}
        self._properties = properties

    @property
    def spg_type_name(self) -> str:
        """Gets the spg_type_name of this SPGRecord.  # noqa: E501


        :return: The spg_type_name of this SPGRecord.  # noqa: E501
        :rtype: str
        """
        return self._spg_type_name

    @spg_type_name.setter
    def spg_type_name(self, spg_type_name: str):
        """Sets the spg_type_name of this SPGRecord.


        :param spg_type_name: The spg_type_name of this SPGRecord.  # noqa: E501
        :type: str
        """
        self._spg_type_name = spg_type_name

    @property
    def properties(self) -> Dict[str, str]:
        """Gets the properties of this SPGRecord.  # noqa: E501


        :return: The properties of this SPGRecord.  # noqa: E501
        :rtype: dict
        """
        return self._properties

    @properties.setter
    def properties(self, properties: Dict[str, str]):
        """Sets the properties of this SPGRecord.


        :param properties: The properties of this SPGRecord.  # noqa: E501
        :type: dict
        """
        self._properties = properties

    def get_property(self, name: str, default_value: str = None) -> str:
        """Gets a property of this SPGRecord by name.  # noqa: E501


        :param name: The property name.  # noqa: E501
        :param default_value: If property value is None, the default_value will be return.  # noqa: E501
        :return: A property value.  # noqa: E501
        :rtype: str
        """
        return self.properties.get(name, default_value)

    def update_property(self, name: str, value: str):
        """Updates a property of this SPGRecord.  # noqa: E501


        :param name: The updated property name.  # noqa: E501
        :param value: The updated property value.  # noqa: E501
        :type: str
        """
        self.properties[name] = value

    def update_properties(self, properties: Dict[str, str]):
        """Updates properties of this SPGRecord.  # noqa: E501


        :param properties: The updated properties.  # noqa: E501
        :type: dict
        """
        self.properties.update(properties)

    def remove_property(self, name: str):
        """Removes a property of this SPGRecord.  # noqa: E501


        :param name: The property name.  # noqa: E501
        :type: str
        """
        self.properties.pop(name)

    def remove_properties(self, names: List[str]):
        """Removes properties by given names.  # noqa: E501


        :param names: A list of property names.  # noqa: E501
        :type: list
        """
        for name in names:
            self.properties.pop(name)

    def to_str(self):
        """Returns the string representation of the model"""
        return pprint.pformat(self.to_dict())

    def to_dict(self):
        """Returns the model properties as a dict"""
        return {
            "SPGTypeName": self.spg_type_name,
            "properties": self.properties,
        }

    @classmethod
    def from_dict(cls, input: Dict[str, Any]):
        """Returns the model from a dict"""
        return cls(input.get("SPGTypeName"), input.get("properties"))

    def __repr__(self):
        """For `print` and `pprint`"""
        return pprint.pformat(self.to_dict())
