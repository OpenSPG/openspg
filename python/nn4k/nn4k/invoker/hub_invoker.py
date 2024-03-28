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

import requests
import json
from nn4k.invoker.base import NNInvoker
from dataclasses import dataclass, field
from typing import Optional
from transformers import HfArgumentParser


class HubInvoker(NNInvoker):
    """
    Invoking Entry Interfaces for remote inference service.
    """
    def __init__(self, nn_config: dict, **kwargs):
        super().__init__(nn_config, **kwargs)
        parser = HfArgumentParser(HubInvokerArgs)
        self.invoker_args: HubInvokerArgs
        self.invoker_args, *_ = parser.parse_dict(self.init_args, allow_extra_keys=True)

    def _check_input(self, input, **kwargs):
        """
        Filter the input that can not currently be processed.
        """
        if isinstance(input, list):
            raise NotImplementedError(
                f"Hub invoker does not support batch inference at the moment."
            )
        else:
            prompt = input
        
        return prompt
    
    def _call_service(self, prompt):
        """
        Call the remote services to do the inference.
        """
        url = self.invoker_args.hub_infer_url
        headers = {'Content-Type': 'application/json'}

        data = {
            "text_input": prompt,
            "parameters": self.invoker_args.generate_config
        }

        response = requests.post(url=url, headers=headers, data=json.dumps(data))

        if response.status_code == 200:
            response_result = response.json()['text_output']
            newline_pos = response_result.find("\n")
            if newline_pos != -1:
                return response_result[newline_pos+1: ]
            else:
                return response_result
        else:
            raise NN4KException(
                f"response error No: {response.status_code},  response error text: {response.text}"
            )
    
    def remote_inference(self, input, **kwargs):
        """
        Inference via existing remote services.
        """
        prompt = self._check_input(input, **kwargs)
        output = self._call_service(prompt)

        return output

    
    @classmethod
    def from_config(cls, nn_config: dict) -> "HubInvoker":
        """
        Create an HubInvoker instance  from 'nn_config'.
        """
        invoker = cls(nn_config)

        return invoker


@dataclass
class HubInvokerArgs:
    """
    Base HubInvoker-supported related args.
    """
    hub_infer_url: Optional[str] = field(
        default=None,
        metadata={"help": ("The remote inference service url.")}
    )
    generate_config: Optional[dict] = field(
        default=None,
        metadata={"help": ("Inference related configs.")}
    )


class NN4KException(Exception):
    """
    Wrapping a NN4K Exception class.
    """
    pass
