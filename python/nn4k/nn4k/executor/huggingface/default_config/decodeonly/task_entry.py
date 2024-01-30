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
    NNInvoker.from_config("local_sft.json5").local_sft()
    # Inference example, not implemented yet.
    # NNInvoker.from_config("inferece_args.json").local_inference("你是谁")


if __name__ == "__main__":
    main()
