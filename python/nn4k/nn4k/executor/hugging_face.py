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

import dataclasses
from dataclasses import dataclass
from typing import Union, Optional

from nn4k.executor import LLMExecutorConfig, LLMExecutor


@dataclass
class HfLLMExecutorConfig(LLMExecutorConfig):
    pass


class HfLLMExecutor(LLMExecutor):
    @classmethod
    def try_parse_config(
        cls, nn_config: Union[str, dict]
    ) -> Optional[HfLLMExecutorConfig]:
        config = super().try_parse_config(nn_config)
        if config is None:
            return None
        config = HfLLMExecutorConfig(**dataclasses.asdict(config))
        return config

    @classmethod
    def _from_config(cls, nn_config: HfLLMExecutorConfig) -> "HfLLMExecutor":
        model = None
        tokenizer = None
        init_args = nn_config
        inference_args = None
        kwargs = dict()
        executor = cls(
            model=model,
            tokenizer=tokenizer,
            init_args=init_args,
            inference_args=inference_args,
            **kwargs
        )
        return executor

    def execute_sft(self, args=None, callbacks=None, **kwargs):
        return super().execute_sft(args=args, callbacks=callbacks, **kwargs)

    def execute_rl_tuning(self, args=None, callbacks=None, **kwargs):
        return super().execute_rl_tuning(args=args, callbacks=callbacks, **kwargs)

    def load_model(self, **kwargs):
        import torch
        from transformers import AutoTokenizer
        from transformers import AutoModelForCausalLM

        if self._model is None:
            model_path = self.init_args.nn_name
            revision = self.init_args.nn_version
            use_fast_tokenizer = False
            device = self.init_args.nn_device
            trust_remote_code = self.init_args.nn_trust_remote_code
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
            self._nn_device = device
            self._tokenizer = tokenizer
            self._model = model

    def inference(self, data, **kwargs):
        if "max_input_length" in kwargs:
            max_input_length = kwargs.pop("max_input_length")
        else:
            max_input_length = 1024
        if "max_output_length" in kwargs:
            max_output_length = kwargs.pop("max_output_length")
        else:
            max_output_length = 1024
        if "do_sample" in kwargs:
            do_sample = kwargs.pop("do_sample")
        else:
            do_sample = False
        model = self.model
        tokenizer = self.tokenizer
        input_ids = tokenizer(
            data,
            padding=True,
            return_token_type_ids=False,
            return_tensors="pt",
            truncation=True,
            max_length=max_input_length,
        ).to(self._nn_device)
        output_ids = model.generate(
            **input_ids,
            max_new_tokens=max_output_length,
            do_sample=do_sample,
            eos_token_id=tokenizer.eos_token_id,
            pad_token_id=tokenizer.pad_token_id,
            **kwargs
        )
        outputs = [
            tokenizer.decode(
                output_id[len(input_ids["input_ids"][idx]) :], skip_special_tokens=True
            )
            for idx, output_id in enumerate(output_ids)
        ]
        return outputs
