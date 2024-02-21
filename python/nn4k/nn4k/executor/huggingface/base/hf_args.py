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

from dataclasses import dataclass, field
from typing import Optional

from transformers import TrainingArguments

from nn4k.executor import NNAdapterModelArgs
from nn4k.executor.base import NNInferenceArgs


@dataclass
class HFModelArgs(NNAdapterModelArgs):
    """
    Huggingface Model is designed to support adapter models such as lora, therefore should inherit from
    NNAdapterModelArgs dataclass
    """

    torch_dtype: Optional[str] = field(
        default="auto",
        metadata={
            "help": (
                "Override the default `torch.dtype` and load the model under this dtype. If `auto` is passed, the "
                "dtype will be automatically derived from the model's weights."
            )
        },
    )
    qlora_bits_and_bytes_config: Optional[dict] = field(
        default=None,
        metadata={
            "help": "Quantization configs to load qlora, "
            "same as :class:`transformers.utils.quantization_config.BitsAndBytesConfig`"
        },
    )
    trust_remote_code: bool = field(
        default=True,
        metadata={
            "help": "Whether or not to allow for custom models defined on the Hub in their own modeling files."
        },
    )
    from_tf: bool = field(
        default=False,
        metadata={
            "help": " Load the model weights from a TensorFlow checkpoint save file, default to False"
        },
    )
    padding_side: Optional[str] = field(
        default=None,
        metadata={
            "help": "Padding side of the tokenizer when padding batch inputs",
            "choices": [None, "left", "right"],
        },
    )

    def __post_init__(self):
        super().__post_init__()
        # for hf models, if model path has higher priority then name, since you don't need to download the model(or
        # from cache) again.
        self.pretrained_model_name_or_path = self.nn_model_path or self.nn_name


@dataclass
class HFSftArgs(HFModelArgs, TrainingArguments):
    """
    args to use for huggingface model sft task
    """

    train_dataset_path: Optional[str] = field(
        default=None,
        metadata={
            "help": "Should not be None. A file or dir path to train dataset, If a dir path, "
            "all files inside should have the same file extension."
        },
    )
    eval_dataset_path: Optional[str] = field(
        default=None,
        metadata={
            "help": "A file or dir path to eval dataset. If a dir path, all files inside should have the same "
            "file extension. If set, do_eval flag will be set to True"
        },
    )
    max_input_length: int = field(
        default=1024,
        metadata={"help": "max length of input"},
    )
    resume_from_checkpoint: Optional[str] = field(
        default=None,
        metadata={
            "help": "The path to a folder with a valid checkpoint for your model."
        },
    )

    def __post_init__(self):
        HFModelArgs.__post_init__(self)
        TrainingArguments.__post_init__(self)
        assert self.train_dataset_path is not None, "train_dataset_path must be set."
        if self.train_dataset_path and not self.do_train:
            self.do_train = True
            print(
                f"a train_dataset_path is set but do_train flag is not set, automatically set do_train to True"
            )
        if self.eval_dataset_path and not self.do_eval:
            self.do_eval = True
            print(
                f"a eval_dataset_path is set but do_eval flag is not set, automatically set do_eval to True"
            )


@dataclass
class HFInferArgs(NNInferenceArgs):
    delete_heading_new_lines: bool = field(
        default=False,
        metadata={
            "help": "sometimes the output will have a additional question mark and new line marks in the beginning"
            "try to get rid of these marks by setting this to True. Different model will have different "
            "behavior, please check the result carefully."
        },
    )

    tokenize_config: dict = field(
        default_factory=lambda: {
            "add_special_tokens": False,
            "padding": False,
            "truncation": False,
        },
        metadata={
            "help": "padding: https://huggingface.co/docs/transformers/pad_truncation#padding-and-truncation"
        },
    )

    decode_config: dict = field(
        default_factory=lambda: {
            "skip_special_tokens": True,
            "clean_up_tokenization_spaces": True,
        },
        metadata={
            "help": "check https://huggingface.co/docs/transformers/main_classes/tokenizer#transformers.PreTrainedTokenizer.__call__"
        },
    )

    def __post_init__(self):
        super().__post_init__()

        # HF specific map
        self.update_if_not_none(
            "max_output_length", "generate_config", "max_new_tokens"
        )
        self.update_if_not_none("max_input_length", "tokenize_config", "max_length")
