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

import os
import typing
from abc import abstractmethod
from typing import Optional, Union

from torch.utils.data import Dataset
from transformers import AutoConfig, AutoTokenizer, Trainer

from nn4k.executor import LLMExecutor
from .hf_args import HFSftArgs, HFModelArgs
from nn4k.executor.huggingface.nn_hf_trainer import NNHFTrainer


class HFLLMExecutor(LLMExecutor):
    """
    Base Executor for huggingface models.
    """

    def __init__(self, init_args: dict, **kwargs):
        super().__init__(init_args=init_args, **kwargs)
        # model_model could be either 'train' or 'inference' or model load
        self.model_mode = None

    @classmethod
    def from_config(cls, nn_config: Union[dict]) -> "HFLLMExecutor":
        """
        Create an HFLLMExecutor instance from `nn_config`.
        """
        executor = cls(nn_config)
        return executor

    def execute_sft(self, args: dict = None, callbacks=None, **kwargs):
        args = args or self.init_args

        self.load_model(args=args, mode="train")

        # parse args into HFSftArgs dataclass for more convenient features
        from transformers import HfArgumentParser

        parser = HfArgumentParser(HFSftArgs)
        hf_sft_args: HFSftArgs
        hf_sft_args, *_ = parser.parse_dict(args, allow_extra_keys=True)

        # load checkpoint path if necessary.
        resume_from_checkpoint_path = self._get_last_checkpoint(hf_sft_args)

        # load and map dataset
        train_dataset, eval_dataset = self._init_dataset(hf_sft_args)

        # init trainer
        trainer: Trainer = self._init_trainer(
            train_dataset, eval_dataset, hf_sft_args, callbacks
        )

        # start training
        train_result = trainer.train(resume_from_checkpoint=resume_from_checkpoint_path)

        # save trained model after train complete
        trainer.save_model(hf_sft_args.output_dir)

        # save train metrics
        train_metrics = train_result.metrics
        train_metrics["train_samples_len"] = len(train_dataset)
        trainer.log_metrics("train", train_metrics)
        trainer.save_metrics("train", train_metrics)
        trainer.save_state()

        return self

    def _get_last_checkpoint(self, sft_args: HFSftArgs) -> Optional[str]:  # noqa
        """
        try to find checkpoint in sft_args.output_dir.
        If sft_args.resume_from_checkpoint in ['True', 'true', True, ''], try to return the checkpoint dir with the
        largest checkpoint index. The largest checkpoint dir path will be returned.
        If sft_args.resume_from_checkpoint in [None, 'False', 'false', False], means not necessary to resume from
        checkpoint, None will be returned.
        If sft_args.resume_from_checkpoint is the checkpoint subfolder dir name, the 'output_dir/resume_from_checkpoint'
        path will be returned if exists. Be aware, if the dir does not exist, ValueError will be raised.
        """
        output_dir_contains_file = (
            os.path.isdir(sft_args.output_dir)
            and len(os.listdir(sft_args.output_dir)) > 0
        )

        if sft_args.resume_from_checkpoint in ["True", "true", True, ""]:
            resume_from_checkpoint_bool = True
            if output_dir_contains_file:
                from transformers.trainer_utils import get_last_checkpoint

                resume_from_checkpoint_path = get_last_checkpoint(sft_args.output_dir)
            else:
                resume_from_checkpoint_path = None
            assert (
                resume_from_checkpoint_path is not None
            ), f"cannot find last checkpoint dir in {sft_args.output_dir}"
        elif sft_args.resume_from_checkpoint in [None, "False", "false", False]:
            resume_from_checkpoint_bool = False
            resume_from_checkpoint_path = None
        else:
            resume_from_checkpoint_bool = True
            resume_from_checkpoint_path = os.path.join(
                sft_args.output_dir, sft_args.resume_from_checkpoint
            )
            assert os.path.isdir(
                resume_from_checkpoint_path
            ), f"{resume_from_checkpoint_path} is not a dir."

        if (
            output_dir_contains_file
            and not sft_args.overwrite_output_dir
            and not resume_from_checkpoint_bool
        ):
            raise ValueError(
                f"Output_dir ({sft_args.output_dir}) is not empty. Maybe you mean --resume_from_checkpoint"
                '="True" to resume a training or --overwrite_output_dir to overwrite output_dir.'
            )

        return resume_from_checkpoint_path

    def map_fn(self, dataset, **kwargs):
        """
        dataset map and template function. The default implement follows the BelleGroup/train_0.5M_CN format, means
        'instruction', 'input' and 'output' are necessary. Since some other popular dataset like tatsu-lab/alpaca
        provides these columns as well, it is also supported.
        """
        args: HFSftArgs = kwargs.get("args", None)
        instruction = dataset["instruction"]
        input_text = dataset["input"]
        output_text = dataset["output"]
        bos_token = self.tokenizer.bos_token or ""
        eos_token = self.tokenizer.eos_token
        input_prompt = f"{bos_token}{instruction} {input_text}{eos_token}"
        tokenized_full_prompt = self._tokenize_dataset(
            input_prompt, args.max_input_length
        )
        return tokenized_full_prompt

    def _init_dataset(
        self, args: HFSftArgs
    ) -> typing.Tuple[Union[Dataset], Union[Dataset]]:  # noqa
        """
        init and map dataset, for train and eval
        """
        with args.main_process_first(desc="initialize dataset"):
            train_dataset = None
            if args.train_dataset_path:
                train_dataset = (
                    self._load_dataset(args.train_dataset_path, "train")
                    .shuffle()
                    .map(self.map_fn, fn_kwargs={"args": args})
                )

            eval_dataset = None
            if args.eval_dataset_path:
                eval_dataset = (
                    self._load_dataset(args.eval_dataset_path, "train")
                    .shuffle()
                    .map(self.map_fn, fn_kwargs={"args": args})
                )

            return train_dataset, eval_dataset

    def _load_dataset(self, data_path, split="train"):  # noqa
        from nn4k.utils.io.dataset_utils import DatasetUtils

        return DatasetUtils.auto_dataset(data_path, split)

    def load_model(self, args: dict = None, mode=None, **kwargs):
        """
        load model and tokenizer. If the model with the same mode is already loaded, will not load again.
        """

        assert (
            mode is not None
        ), f"mode should be either 'train' or 'inference' for HFLLMExecutor, {mode} is illegal."

        if self.model_mode == mode and self._model is not None:
            return

        from transformers import HfArgumentParser
        from nn4k.executor.huggingface import HFModelArgs

        parser = HfArgumentParser(HFModelArgs)
        hf_model_args, *_ = parser.parse_dict(args, allow_extra_keys=True)

        self.model_mode = mode
        self._tokenizer = self._hf_tokenizer_loader(hf_model_args)
        self._model = self._hf_model_loader(
            hf_model_args, mode, hf_model_args.nn_device
        )

        if self.tokenizer.eos_token_id is None:
            self.tokenizer.eos_token_id = self.model.config.eos_token_id
        if self.tokenizer.pad_token_id is None:
            self.tokenizer.pad_token_id = self.tokenizer.eos_token_id

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

    @abstractmethod
    def _hf_model_loader(
        self,
        args: HFModelArgs,
        mode,
        resume_from_checkpoint=False,
        device=None,
        **kwargs,
    ):
        """
        load model into given device for hugging face.
        """
        pass

    def _hf_tokenizer_loader(self, args: HFModelArgs, **kwargs):  # noqa
        """
        hugging face tokenizer loader
        """
        tokenizer = AutoTokenizer.from_pretrained(
            pretrained_model_name_or_path=args.pretrained_model_name_or_path,
            use_fast=False,
            revision=args.nn_version,
            trust_remote_code=args.trust_remote_code,
        )
        return tokenizer

    def _hf_model_config_loader(self, args: HFModelArgs, **kwargs):  # noqa
        """
        hugging face model config loader
        """
        model_config = AutoConfig.from_pretrained(
            args.pretrained_model_name_or_path,
            trust_remote_code=args.trust_remote_code,
            **kwargs,
        )
        return model_config

    def _init_trainer(
        self, train_dataset, eval_dataset, sft_args: HFSftArgs, callbacks=None
    ) -> Trainer:
        """
        hugging face model trainer initializer
        """
        trainer = NNHFTrainer(
            model=self.model,
            args=sft_args,
            train_dataset=train_dataset,
            eval_dataset=eval_dataset,
            tokenizer=self.tokenizer,
            data_collator=self._data_collator(),
            callbacks=callbacks,
        )

        return trainer

    @abstractmethod
    def _data_collator(self, return_tensors="pt", **kwargs):
        """
        data collator used in trainer
        """
        pass

    def _tokenize_dataset(self, prompt_text, max_length):
        """
        tokenize dataset, by default will cut the input to the max_length
        """
        tokenized_dataset = self.tokenizer(
            prompt_text, truncation=True, max_length=max_length
        )
        input_ids = tokenized_dataset["input_ids"]
        attention_mask = tokenized_dataset["attention_mask"]

        # append eos token if necessary
        # input length is shorter than max_length
        if len(input_ids) < max_length:
            if input_ids[-1] != self.tokenizer.eos_token_id:
                input_ids.append(self.tokenizer.eos_token_id)
                attention_mask.append(1)
        else:
            input_ids[max_length - 1] = self.tokenizer.eos_token_id
            attention_mask[max_length - 1] = 1

        # labels are copy of input_ids
        tokenized_dataset["labels"] = tokenized_dataset["input_ids"].copy()

        return tokenized_dataset
