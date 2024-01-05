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

import importlib
from typing import Tuple


def split_module_class_name(name: str, text: str) -> Tuple[str, str]:
    """
    Split `name` as module name and class name pair.

    :param name: fully qualified class name, e.g. ``foo.bar.MyClass``
    :type name: str
    :param text: describe the kind of the class, used in the exception message
    :type text: str
    :rtype: Tuple[str, str]
    :raises RuntimeError: if `name` is not a fully qualified class name
    """
    i = name.rfind(".")
    if i == -1:
        message = "invalid %s class name: %s" % (text, name)
        raise RuntimeError(message)
    module_name = name[:i]
    class_name = name[i + 1 :]
    return module_name, class_name


def dynamic_import_class(name: str, text: str):
    """
    Import the class specified by `name` dyanmically.

    :param name: fully qualified class name, e.g. ``foo.bar.MyClass``
    :type name: str
    :param text: describe the kind of the class, use in the exception message
    :type text: str
    :raises RuntimeError: if `name` is not a fully qualified class name, or
                          the class is not in the module specified by `name`
    :raises ModuleNotFoundError: the module specified by `name` is not found
    """
    module_name, class_name = split_module_class_name(name, text)
    module = importlib.import_module(module_name)
    class_ = getattr(module, class_name, None)
    if class_ is None:
        message = "class %r not found in module %r" % (class_name, module_name)
        raise RuntimeError(message)
    if not isinstance(class_, type):
        message = "%r is not a class" % (name,)
        raise RuntimeError(message)
    return class_
