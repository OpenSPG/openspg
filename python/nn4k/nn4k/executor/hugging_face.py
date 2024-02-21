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
from nn4k.executor import LLMExecutor


class HFEmbeddingExecutor(LLMExecutor):
    @classmethod
    def from_config(cls, nn_config: dict) -> "HFEmbeddingExecutor":
        """
        Create an HFEmbeddingExecutor instance from `nn_config`.
        """
        executor = cls(nn_config)
        return executor

    def load_model(self, args=None, **kwargs):
        import torch
        from sentence_transformers import SentenceTransformer
        from nn4k.consts import NN_NAME_KEY, NN_NAME_TEXT
        from nn4k.consts import NN_VERSION_KEY, NN_VERSION_TEXT
        from nn4k.consts import NN_DEVICE_KEY
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
            if device is None:
                device = "cuda" if torch.cuda.is_available() else "cpu"
            #
            # SentenceTransformer will support `revision` soon. See:
            #
            #   https://github.com/UKPLab/sentence-transformers/pull/2419
            #
            model = SentenceTransformer(
                model_path,
                device=device,
            )
            self._model = model

    def inference(self, data, args=None, **kwargs):
        model = self.model
        embeddings = model.encode(data)
        return embeddings
