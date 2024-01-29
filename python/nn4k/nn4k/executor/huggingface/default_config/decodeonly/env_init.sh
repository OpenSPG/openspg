# The scripts are tested by the following package installed
export WANDB_DISABLED=true

#Only if you have a cuda OOM, try this setting
#export PYTORCH_CUDA_ALLOC_CONF=max_split_size_mb:32

pip install peft==0.5.0
pip install json5 # only necessary if you use json5 file as a config file
pip install numpy==1.23.1
pip install transformers==4.36.2
#pip install xformers==0.0.23.post1 # only necessary if you want to accelerate loading model in memery efficient way