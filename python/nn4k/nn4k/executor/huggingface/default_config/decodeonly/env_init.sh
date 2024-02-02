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

# The scripts are tested by the following package installed
export WANDB_DISABLED=true

#Only if you have a cuda OOM, try this setting
#export PYTORCH_CUDA_ALLOC_CONF=max_split_size_mb:32

pip install peft==0.5.0
pip install json5 # only necessary if you use json5 file as a config file
pip install numpy==1.23.1
pip install transformers==4.36.2
pip install accelerate>=0.21.0
pip install bitsandbytes>=0.39.0 #only necessary if you use qlora
#pip install xformers==0.0.23.post1 # only necessary if you want to accelerate loading model in memery efficient way