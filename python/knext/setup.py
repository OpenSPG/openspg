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

import os

from setuptools import setup, find_packages

package_name = "openspg-knext"

# version
cwd = os.path.abspath(os.path.dirname(__file__))
with open(os.path.join(cwd, "KNEXT_VERSION"), "r") as rf:
    version = rf.readline().strip("\n").strip()

# license
license = ""
with open(os.path.join(cwd, "LICENSE"), "r") as rf:
    line = rf.readline()
    while line:
        line = line.strip()
        if line:
            license += "# " + line + "\n"
        else:
            license += "#\n"
        line = rf.readline()

# Generate knext.__init__.py
with open(os.path.join(cwd, "knext/__init__.py"), "w") as wf:
    content = f"""{license}

__package_name__ = "{package_name}"
__version__ = "{version}"

from knext.common.env import init_env

init_env()
"""
    wf.write(content)

setup(
    name=package_name,
    version=version,
    description="knext",
    url="https://github.com/OpenSPG/openspg",
    packages=find_packages(
        where=".",
        exclude=[
            ".*test.py",
            "*_test.py",
            "*_debug.py",
            "*.txt",
            "tests",
            "tests.*",
            "configs",
            "configs.*",
            "test",
            "test.*",
            "*.tests",
            "*.tests.*",
            "*.pyc",
        ],
    ),
    python_requires=">=3.8",
    install_requires=[
        r.strip()
        for r in open("requirements.txt", "r")
        if not r.strip().startswith("#")
    ],
    entry_points={
        "console_scripts": [
            "knext=knext.command.knext_cli:_main",
        ]
    },
    include_package_data=True,
    package_data={
        "bin": ["*"],
    },
)
