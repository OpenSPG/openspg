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

from nn4k.executor import LLMExecutor


class HfLLMExecutor(LLMExecutor):
    @classmethod
    def _parse_config(cls, nn_config: dict) -> dict:
        from nn4k.utils.config_parsing import get_string_field

        nn_name = get_string_field(nn_config, "nn_name", "NN model name")
        nn_version = get_string_field(nn_config, "nn_version", "NN model version")
        config = dict(
            nn_name=nn_name,
            nn_version=nn_version,
        )
        return config

    @classmethod
    def from_config(cls, nn_config: dict):
        config = cls._parse_config(nn_config)

        o = cls.__new__(cls)
        o._nn_config = nn_config
        o._nn_name = config["nn_name"]
        o._nn_version = config["nn_version"]
        o._nn_device = None
        o._nn_tokenizer = None
        o._nn_model = None

        return o

    def _load_model(self):
        import torch
        from transformers import AutoTokenizer
        from transformers import AutoModelForCausalLM

        if self._nn_model is None:
            model_path = self._nn_name
            revision = self._nn_version
            use_fast_tokenizer = False
            device = self._nn_config.get("nn_device")
            trust_remote_code = self._nn_config.get("nn_trust_remote_code", False)
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
            self._nn_tokenizer = tokenizer
            self._nn_model = model

    def _get_tokenizer(self):
        if self._nn_model is None:
            self._load_model()
        return self._nn_tokenizer

    def _get_model(self):
        if self._nn_model is None:
            self._load_model()
        return self._nn_model

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
        nn_tokenizer = self._get_tokenizer()
        nn_model = self._get_model()
        input_ids = nn_tokenizer(
            data,
            padding=True,
            return_token_type_ids=False,
            return_tensors="pt",
            truncation=True,
            max_length=max_input_length,
        ).to(self._nn_device)
        output_ids = nn_model.generate(
            **input_ids,
            max_new_tokens=max_output_length,
            do_sample=do_sample,
            eos_token_id=nn_tokenizer.eos_token_id,
            pad_token_id=nn_tokenizer.pad_token_id,
            **kwargs
        )
        outputs = [
            nn_tokenizer.decode(
                output_id[len(input_ids["input_ids"][idx]) :], skip_special_tokens=True
            )
            for idx, output_id in enumerate(output_ids)
        ]
        return outputs
