# Copyright 2023 OpenSPG Authors
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
import importlib.util
from pathlib import Path

# Client registration dictionaries
_LLM_CLIENTS = {}
_EMB_CLIENTS = {}

# Registration decorator for LLM clients
def register_llm_client(name):
    def decorator(cls):
        _LLM_CLIENTS[name] = cls
        return cls
    return decorator

# Function to get an LLM client instance
def get_llm_client(client_name, client_config=None):
    if client_config is None:
        client_config = {}
    if client_name in _LLM_CLIENTS:
        return _LLM_CLIENTS[client_name](**client_config)
    else:
        raise ValueError(f"No client registered with the name '{client_name}'")

# Registration decorator for EMB clients
def register_emb_client(name):
    def decorator(cls):
        _EMB_CLIENTS[name] = cls
        return cls
    return decorator

# Function to get an EMB client instance
def get_emb_client(client_name, client_config=None):
    if client_config is None:
        client_config = {}
    if client_name in _EMB_CLIENTS:
        return _EMB_CLIENTS[client_name](**client_config)
    else:
        raise ValueError(f"No client registered with the name '{client_name}'")

# Automatically register modules in the current directory
def _auto_register():
    current_dir = Path(__file__).parent  # Get the directory where __init__.py resides
    for file in current_dir.glob('*.py'):
        if file.name != '__init__.py':  # Exclude __init__.py itself
            module_name = file.stem  # Get the module name without .py extension
            spec = importlib.util.spec_from_file_location(module_name, file)
            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module)

_auto_register()
