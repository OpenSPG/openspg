from knext.ca.common.client import register_emb_client
from knext.ca.common.utils import logger
import dashscope
from http import HTTPStatus
import os
import time
import json
import requests


@register_emb_client("aliyun")
class MayaEmbClient:
    def __init__(self, api_key, debug=False):
        self.api_key = api_key
        self.debug = debug

    def generate(self, query):
        resp = dashscope.TextEmbedding.call(
            model=dashscope.TextEmbedding.Models.text_embedding_v1,
            api_key=self.api_key,
            input=query)
        
        if resp.status_code == HTTPStatus.OK:
            return resp.output.embeddings.embedding
        else:
            return None
