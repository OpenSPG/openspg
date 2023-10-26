# -*- coding: utf-8 -*-
#
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

import os
import sys
from pathlib import Path
from typing import Type
import inspect


def register_from_package(path: str, class_type: Type) -> None:
    """
    Register all classes under the given package.
    Only registered classes can be recognized by knext.
    """
    if not append_python_path(path):
        return
    for root, dirs, files in os.walk(path):
        for file_name in files:
            if file_name.endswith(".py"):
                module_name = os.path.splitext(file_name)[0]
                module = __import__(module_name)
                classes = inspect.getmembers(module, inspect.isclass)
                for class_name, class_obj in classes:
                    if (
                        issubclass(class_obj, class_type)
                        and inspect.getmodule(class_obj) == module
                    ):
                        class_type.register(
                            name=class_name, local_path=os.path.join(root, file_name)
                        )(class_obj)


def append_python_path(path: str) -> bool:
    """
    Append the given path to `sys.path`.
    """
    # In some environments, such as TC, it fails when sys.path contains a relative path, such as ".".
    path = Path(path).resolve()
    path = str(path)
    if path not in sys.path:
        sys.path.append(path)
        return True
    return False
