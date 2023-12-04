# coding: utf-8
# Copyright (c) Antfin, Inc. All rights reserved.
import sys
from abc import ABC



class ModelInvoker(ABC):
    """
    对应 xflow ModelHubEntry
    """

    def submit_sft(self, submit_mode='k8s'):
        pass

    def submit_rl_tuning(self, submit_mode='k8s'):
        pass

    def deploy(cls, args, deploy_mode='k8s'):
        pass

    def inference(self, input, **kwargs):
        """
        这个是从已有的服务中获取inference
        Args:
            args:
            **kwargs:

        Returns:

        """
        pass


    @classmethod
    def from_config(cls, args='sys'):
        return cls()



class OpenAI(ModelInvoker):

    def __init__(self, token):
        self.token = token
        pass

    def inference(self, input, **kwargs):
        import requests
        requests.post(url="https://api.openai.com", params={"input": input, "token": self.token})

