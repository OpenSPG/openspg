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

from nn4k.invoker import NNInvoker

invoker = NNInvoker.from_config("openai_emb.json")
vecs = invoker.remote_inference(
    ["How old are you?", "What is your age?"], type="Embedding"
)
similarity = sum(x * y for x, y in zip(*vecs))
print("similarity: %g" % similarity)
