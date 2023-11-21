# -*- coding: utf-8 -*-
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

import os
from typing import Optional

import jieba
import numpy as np
import torch
from datasets import load_dataset
from nltk.translate.bleu_score import sentence_bleu, SmoothingFunction
from rouge_chinese import Rouge
from transformers import (
    AutoConfig,
    AutoModel,
    AutoTokenizer,
    DataCollatorForSeq2Seq,
    HfArgumentParser,
    Seq2SeqTrainingArguments,
    Trainer,
)
from transformers.trainer import TRAINING_ARGS_NAME

from arguments import ModelArguments, DataTrainingArguments


class PrefixTrainer(Trainer):
    def __init__(self, *args, save_changed=False, **kwargs):
        self.save_changed = save_changed
        super().__init__(*args, **kwargs)

    def _save(self, output_dir: Optional[str] = None, state_dict=None):
        # If we are executing this function, we are the process zero, so we don't check for that.
        output_dir = output_dir if output_dir is not None else self.args.output_dir
        os.makedirs(output_dir, exist_ok=True)
        print(f"Saving model checkpoint to {output_dir}")
        # Save a trained model and configuration using `save_pretrained()`.
        # They can then be reloaded using `from_pretrained()`

        print("Saving PrefixEncoder")
        state_dict = self.model.state_dict()
        filtered_state_dict = {}
        for k, v in self.model.named_parameters():
            if v.requires_grad:
                filtered_state_dict[k] = state_dict[k]
        self.model.save_pretrained(output_dir, state_dict=filtered_state_dict)
        if self.tokenizer is not None:
            self.tokenizer.save_pretrained(output_dir)

        # Good practice: save your training arguments together with the trained model
        torch.save(self.args, os.path.join(output_dir, TRAINING_ARGS_NAME))


def load_training_dataset(tokenizer, data_args, model_args, training_args):
    # Load dataset
    data_files = {}
    data_files["train"] = data_args.train_file
    extension = data_args.train_file.split(".")[-1]

    # Preprocess dataset
    raw_datasets = load_dataset(
        extension,
        data_files=data_files,
        cache_dir=model_args.cache_dir,
        use_auth_token=True if model_args.use_auth_token else None,
    )
    if "train" not in raw_datasets:
        raise ValueError("--do_train requires a train dataset")
    train_dataset = raw_datasets["train"]
    with training_args.main_process_first(desc="train dataset map pre-processing"):
        return train_dataset.map(
            preprocess(tokenizer, data_args),
            batched=True,
            num_proc=data_args.preprocessing_num_workers,
            load_from_cache_file=not data_args.overwrite_cache,
            desc="Running tokenizer on train dataset",
        )


def load_data_collator(tokenizer, model, data_args):
    label_pad_token_id = (
        -100 if data_args.ignore_pad_token_for_loss else tokenizer.pad_token_id
    )
    return DataCollatorForSeq2Seq(
        tokenizer,
        model=model,
        label_pad_token_id=label_pad_token_id,
        pad_to_multiple_of=None,
        padding=False,
    )


def load_trainer(
    tokenizer, model, train_dataset, data_collator, data_args, training_args
):
    # Override the decoding parameters of Seq2SeqTrainer
    training_args.generation_max_length = (
        training_args.generation_max_length
        if training_args.generation_max_length is not None
        else data_args.val_max_target_length
    )
    training_args.generation_num_beams = (
        data_args.num_beams
        if data_args.num_beams is not None
        else training_args.generation_num_beams
    )

    # Init PrefixTrainer
    return PrefixTrainer(
        model=model,
        args=training_args,
        train_dataset=train_dataset,
        tokenizer=tokenizer,
        data_collator=data_collator,
        compute_metrics=compute_metrics(tokenizer, data_args)
        if training_args.predict_with_generate
        else None,
    )


def preprocess(tokenizer, data_args):
    def preprocess_function_train(examples):
        # Get the column names for input/target.
        prompt_column = data_args.prompt_column
        response_column = data_args.response_column

        max_seq_length = data_args.max_source_length + data_args.max_target_length + 1

        model_inputs = {
            "input_ids": [],
            "labels": [],
        }
        for i in range(len(examples[prompt_column])):
            if examples[prompt_column][i] and examples[response_column][i]:
                query, answer = examples[prompt_column][i], examples[response_column][i]

                a_ids = tokenizer.encode(
                    text=str(query),
                    add_special_tokens=True,
                    truncation=True,
                    max_length=data_args.max_source_length,
                )
                b_ids = tokenizer.encode(
                    text=str(answer),
                    add_special_tokens=False,
                    truncation=True,
                    max_length=data_args.max_target_length,
                )

                context_length = len(a_ids)
                input_ids = a_ids + b_ids + [tokenizer.eos_token_id]
                labels = (
                    [tokenizer.pad_token_id] * context_length
                    + b_ids
                    + [tokenizer.eos_token_id]
                )

                pad_len = max_seq_length - len(input_ids)
                input_ids = input_ids + [tokenizer.pad_token_id] * pad_len
                labels = labels + [tokenizer.pad_token_id] * pad_len
                if data_args.ignore_pad_token_for_loss:
                    labels = [
                        (l if l != tokenizer.pad_token_id else -100) for l in labels
                    ]

                model_inputs["input_ids"].append(input_ids)
                model_inputs["labels"].append(labels)

        return model_inputs

    return preprocess_function_train


def compute_metrics(tokenizer, data_args):
    def metrics(eval_preds):
        preds, labels = eval_preds
        if isinstance(preds, tuple):
            preds = preds[0]
        decoded_preds = tokenizer.batch_decode(preds, skip_special_tokens=True)
        if data_args.ignore_pad_token_for_loss:
            # Replace -100 in the labels as we can't decode them.
            labels = np.where(labels != -100, labels, tokenizer.pad_token_id)
        decoded_labels = tokenizer.batch_decode(labels, skip_special_tokens=True)

        score_dict = {"rouge-1": [], "rouge-2": [], "rouge-l": [], "bleu-4": []}
        for pred, label in zip(decoded_preds, decoded_labels):
            hypothesis = list(jieba.cut(pred))
            reference = list(jieba.cut(label))
            rouge = Rouge()
            scores = rouge.get_scores(" ".join(hypothesis), " ".join(reference))
            result = scores[0]

            for k, v in result.items():
                score_dict[k].append(round(v["f"] * 100, 4))
            bleu_score = sentence_bleu(
                [list(label)],
                list(pred),
                smoothing_function=SmoothingFunction().method3,
            )
            score_dict["bleu-4"].append(round(bleu_score * 100, 4))

        for k, v in score_dict.items():
            score_dict[k] = float(np.mean(v))
        return score_dict

    return metrics


def main():
    # Load parameters
    parser = HfArgumentParser(
        (ModelArguments, DataTrainingArguments, Seq2SeqTrainingArguments)
    )
    model_args, data_args, training_args = parser.parse_args_into_dataclasses()

    # Load config
    config = AutoConfig.from_pretrained(
        model_args.model_name_or_path, trust_remote_code=True
    )
    config.pre_seq_len = model_args.pre_seq_len
    config.prefix_projection = model_args.prefix_projection

    # Load tokenizer
    tokenizer = AutoTokenizer.from_pretrained(
        model_args.model_name_or_path, trust_remote_code=True
    )

    # Load model for P-tuning v2
    model = AutoModel.from_pretrained(
        model_args.model_name_or_path, config=config, trust_remote_code=True
    )
    model = model.half()
    model.transformer.prefix_encoder.float()

    # Load training dataset
    train_dataset = load_training_dataset(
        tokenizer, data_args, model_args, training_args
    )

    # Load data collator
    data_collator = load_data_collator

    # Load trainer
    trainer = load_trainer(
        tokenizer, model, train_dataset, data_collator, data_args, training_args
    )

    # Training
    checkpoint = None
    if training_args.resume_from_checkpoint is not None:
        checkpoint = training_args.resume_from_checkpoint
    model.gradient_checkpointing_enable()
    model.enable_input_require_grads()
    train_result = trainer.train(resume_from_checkpoint=checkpoint)

    # Save model
    trainer.save_model()  # Saves the tokenizer too for easy upload

    # Save metrics
    metrics = train_result.metrics
    max_train_samples = (
        data_args.max_train_samples
        if data_args.max_train_samples is not None
        else len(train_dataset)
    )
    metrics["train_samples"] = min(max_train_samples, len(train_dataset))
    trainer.log_metrics("train", metrics)
    trainer.save_metrics("train", metrics)

    # Save state
    trainer.save_state()


if __name__ == "__main__":
    main()
