#
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
#

LR=2e-2
NUM_GPUS=0
mkdir output

CUDA_VISIBLE_DEVICES=0 python3 trainer.py \
  --model_name_or_path THUDM/chatglm2-6b \
  --train_file dataset/NER/processed.json \
  --preprocessing_num_workers 10 \
  --prompt_column content \
  --response_column summary \
  --overwrite_cache \
  --output_dir output/adgen-chatglm2-6b-pt-$PRE_SEQ_LEN-$LR \
  --overwrite_output_dir \
  --max_source_length 64 \
  --max_target_length 128 \
  --per_device_train_batch_size 1 \
  --per_device_eval_batch_size 1 \
  --gradient_accumulation_steps 16 \
  --predict_with_generate \
  --max_steps 3000 \
  --logging_steps 10 \
  --save_steps 1000 \
  --learning_rate $LR \
  --pre_seq_len $PRE_SEQ_LEN \
  --quantization_bit 4
