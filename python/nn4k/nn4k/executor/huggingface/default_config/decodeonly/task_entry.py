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

from nn4k.invoker.base import NNInvoker


def main():
    # example for local sft
    # NNInvoker.from_config("local_sft.json5").local_sft()

    # example for local inference
    invoker = NNInvoker.from_config("local_infer.json5")
    answer = invoker.local_inference(
        "What could LLM do for human?",
        tokenize_config={"padding": True},
        delete_heading_new_lines=True,
    )
    # doing so to avoid load model everytime. You could hold a invoker, which has alreday load the model at the first time.
    answer2 = invoker.local_inference(
        "What could LLM do for a programmer",
        tokenize_config={"padding": True},
        delete_heading_new_lines=True,
    )


if __name__ == "__main__":
    main()
