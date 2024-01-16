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

from typing import Union
from nn4k.executor import LLMExecutor


class HfLLMExecutor(LLMExecutor):
    @classmethod
    def from_config(cls, nn_config: dict) -> "HfLLMExecutor":
        """
        Create an HfLLMExecutor instance from `nn_config`.
        """
        executor = cls(nn_config)
        return executor

    def execute_sft(self, args=None, callbacks=None, **kwargs):
        raise NotImplementedError(
            f"{self.__class__.__name__} will support SFT in the next version."
        )

    def load_model(self, args=None, **kwargs):
        import torch
        from transformers import AutoTokenizer
        from transformers import AutoModelForCausalLM
        from nn4k.consts import NN_NAME_KEY, NN_NAME_TEXT
        from nn4k.consts import NN_VERSION_KEY, NN_VERSION_TEXT
        from nn4k.consts import NN_DEVICE_KEY, NN_TRUST_REMOTE_CODE_KEY
        from nn4k.utils.config_parsing import get_string_field

        nn_config: dict = args or self.init_args
        if self._model is None:
            nn_name = get_string_field(nn_config, NN_NAME_KEY, NN_NAME_TEXT)
            nn_version = nn_config.get(NN_VERSION_KEY)
            if nn_version is not None:
                nn_version = get_string_field(
                    nn_config, NN_VERSION_KEY, NN_VERSION_TEXT
                )
            model_path = nn_name
            revision = nn_version
            use_fast_tokenizer = False
            device = nn_config.get(NN_DEVICE_KEY)
            trust_remote_code = nn_config.get(NN_TRUST_REMOTE_CODE_KEY, False)
            if device is None:
                device = "cuda" if torch.cuda.is_available() else "cpu"
            tokenizer = AutoTokenizer.from_pretrained(
                model_path,
                use_fast=use_fast_tokenizer,
                revision=revision,
                trust_remote_code=trust_remote_code,
            )
            model = AutoModelForCausalLM.from_pretrained(
                model_path,
                low_cpu_mem_usage=True,
                torch_dtype=torch.float16,
                revision=revision,
                trust_remote_code=trust_remote_code,
            )
            model.to(device)
            self._tokenizer = tokenizer
            self._model = model

    def inference(
        self,
        data,
        max_input_length: int = 1024,
        max_output_length: int = 1024,
        do_sample: bool = False,
        **kwargs,
    ):
        model = self.model
        tokenizer = self.tokenizer
        input_ids = tokenizer(
            data,
            padding=True,
            return_token_type_ids=False,
            return_tensors="pt",
            truncation=True,
            max_length=max_input_length,
        ).to(model.device)
        output_ids = model.generate(
            **input_ids,
            max_new_tokens=max_output_length,
            do_sample=do_sample,
            eos_token_id=tokenizer.eos_token_id,
            pad_token_id=tokenizer.pad_token_id,
            **kwargs,
        )

        outputs = [
            tokenizer.decode(
                output_id[len(input_ids["input_ids"][idx]) :], skip_special_tokens=True
            )
            for idx, output_id in enumerate(output_ids)
        ]
        return outputs
