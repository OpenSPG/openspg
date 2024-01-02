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

from .base import NNInvokerConfig, LLMInvokerConfig, NNInvoker, LLMInvoker

NNInvoker.register_invoker_class(LLMInvoker)

from .openai_invoker import OpenAIInvoker

NNInvoker.register_invoker_class(OpenAIInvoker)
del OpenAIInvoker
