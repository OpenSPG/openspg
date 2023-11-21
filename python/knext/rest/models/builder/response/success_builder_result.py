# coding: utf-8

"""
    knext

    No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)  # noqa: E501

    The version of the OpenAPI document: 1.0.0
    Generated by: https://openapi-generator.tech
"""

#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

import pprint
import re  # noqa: F401

import six

from knext.rest.configuration import Configuration


class SuccessBuilderResult(object):
    """NOTE: This class is auto generated by OpenAPI Generator.
    Ref: https://openapi-generator.tech

    Do not edit the class manually.
    """

    """
    Attributes:
      openapi_types (dict): The key is attribute name
                            and the value is attribute type.
      attribute_map (dict): The key is attribute name
                            and the value is json key in definition.
    """
    openapi_types = {"total_cnt": "str", "error_cnt": "str", "error_table_file": "str"}

    attribute_map = {
        "total_cnt": "totalCnt",
        "error_cnt": "errorCnt",
        "error_table_file": "errorTableFile",
    }

    def __init__(
        self,
        total_cnt=None,
        error_cnt=None,
        error_table_file=None,
        local_vars_configuration=None,
    ):  # noqa: E501
        """SuccessBuilderResult - a model defined in OpenAPI"""  # noqa: E501
        if local_vars_configuration is None:
            local_vars_configuration = Configuration()
        self.local_vars_configuration = local_vars_configuration

        self._total_cnt = None
        self._error_cnt = None
        self._error_table_file = None
        self.discriminator = None

        if total_cnt is not None:
            self.total_cnt = total_cnt
        if error_cnt is not None:
            self.error_cnt = error_cnt
        if error_table_file is not None:
            self.error_table_file = error_table_file

    @property
    def total_cnt(self):
        """Gets the total_cnt of this SuccessBuilderResult.  # noqa: E501


        :return: The total_cnt of this SuccessBuilderResult.  # noqa: E501
        :rtype: str
        """
        return self._total_cnt

    @total_cnt.setter
    def total_cnt(self, total_cnt):
        """Sets the total_cnt of this SuccessBuilderResult.


        :param total_cnt: The total_cnt of this SuccessBuilderResult.  # noqa: E501
        :type: str
        """

        self._total_cnt = total_cnt

    @property
    def error_cnt(self):
        """Gets the error_cnt of this SuccessBuilderResult.  # noqa: E501


        :return: The error_cnt of this SuccessBuilderResult.  # noqa: E501
        :rtype: str
        """
        return self._error_cnt

    @error_cnt.setter
    def error_cnt(self, error_cnt):
        """Sets the error_cnt of this SuccessBuilderResult.


        :param error_cnt: The error_cnt of this SuccessBuilderResult.  # noqa: E501
        :type: str
        """

        self._error_cnt = error_cnt

    @property
    def error_table_file(self):
        """Gets the error_table_file of this SuccessBuilderResult.  # noqa: E501


        :return: The error_table_file of this SuccessBuilderResult.  # noqa: E501
        :rtype: str
        """
        return self._error_table_file

    @error_table_file.setter
    def error_table_file(self, error_table_file):
        """Sets the error_table_file of this SuccessBuilderResult.


        :param error_table_file: The error_table_file of this SuccessBuilderResult.  # noqa: E501
        :type: str
        """

        self._error_table_file = error_table_file

    def to_dict(self):
        """Returns the model properties as a dict"""
        result = {}

        for attr, _ in six.iteritems(self.openapi_types):
            value = getattr(self, attr)
            if isinstance(value, list):
                result[attr] = list(
                    map(lambda x: x.to_dict() if hasattr(x, "to_dict") else x, value)
                )
            elif hasattr(value, "to_dict"):
                result[attr] = value.to_dict()
            elif isinstance(value, dict):
                result[attr] = dict(
                    map(
                        lambda item: (item[0], item[1].to_dict())
                        if hasattr(item[1], "to_dict")
                        else item,
                        value.items(),
                    )
                )
            else:
                result[attr] = value

        return result

    def to_str(self):
        """Returns the string representation of the model"""
        return pprint.pformat(self.to_dict())

    def __repr__(self):
        """For `print` and `pprint`"""
        return self.to_str()

    def __eq__(self, other):
        """Returns true if both objects are equal"""
        if not isinstance(other, SuccessBuilderResult):
            return False

        return self.to_dict() == other.to_dict()

    def __ne__(self, other):
        """Returns true if both objects are not equal"""
        if not isinstance(other, SuccessBuilderResult):
            return True

        return self.to_dict() != other.to_dict()
