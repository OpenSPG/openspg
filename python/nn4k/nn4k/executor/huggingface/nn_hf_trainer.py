import os

import safetensors
import torch
from torch.utils.data import DataLoader, Dataset, RandomSampler, SequentialSampler
from packaging import version

from peft import PeftModel
from transformers import PretrainedConfig, Trainer, __version__
from transformers.integrations import is_deepspeed_available
from transformers.modeling_utils import load_sharded_checkpoint
from transformers.trainer import logger
from transformers.utils import (
    ADAPTER_SAFE_WEIGHTS_NAME,
    ADAPTER_WEIGHTS_NAME,
    CONFIG_NAME,
    SAFE_WEIGHTS_INDEX_NAME,
    SAFE_WEIGHTS_NAME,
    WEIGHTS_INDEX_NAME,
    WEIGHTS_NAME,
    is_accelerate_available,
    is_peft_available,
    is_sagemaker_mp_enabled,
)

if is_accelerate_available():
    from accelerate import Accelerator, skip_first_batches
    from accelerate import __version__ as accelerate_version
    from accelerate.utils import (
        DistributedDataParallelKwargs,
        GradientAccumulationPlugin,
        load_fsdp_model,
        load_fsdp_optimizer,
        save_fsdp_model,
        save_fsdp_optimizer,
    )

    DATA_SAMPLERS = [RandomSampler]
    if version.parse(accelerate_version) > version.parse("0.23.0"):
        from accelerate.data_loader import SeedableRandomSampler

        DATA_SAMPLERS += [SeedableRandomSampler]

    if is_deepspeed_available():
        from accelerate.utils import DeepSpeedSchedulerWrapper


class NNHFTrainer(Trainer):
    """
    only trying to fix resume checkpoint for lora adapter, will be replaced by using Trainer when the bug is
    fixed in huggingface trainer. The PR is offered: https://github.com/huggingface/transformers/pull/28547
    """

    def _load_from_checkpoint(self, resume_from_checkpoint, model=None):
        # the following code only trying to fix resuming checkpoint for adapter model(Peft)
        if model is None:
            model = self.model

        if not (is_peft_available() and isinstance(model, PeftModel)):
            return super()._load_from_checkpoint(resume_from_checkpoint, model)

        adapter_name_path = ""
        if isinstance(model, PeftModel):
            adapter_name_path = (
                model.active_adapter
                if model.active_adapter not in ["default", None]
                else ""
            )

        config_file = os.path.join(resume_from_checkpoint, CONFIG_NAME)
        adapter_weights_file = os.path.join(
            resume_from_checkpoint, adapter_name_path, ADAPTER_WEIGHTS_NAME
        )
        adapter_safe_weights_file = os.path.join(
            resume_from_checkpoint, adapter_name_path, ADAPTER_SAFE_WEIGHTS_NAME
        )
        weights_file = os.path.join(resume_from_checkpoint, WEIGHTS_NAME)
        weights_index_file = os.path.join(resume_from_checkpoint, WEIGHTS_INDEX_NAME)
        safe_weights_file = os.path.join(resume_from_checkpoint, SAFE_WEIGHTS_NAME)
        safe_weights_index_file = os.path.join(
            resume_from_checkpoint, SAFE_WEIGHTS_INDEX_NAME
        )

        if not any(
            os.path.isfile(f)
            for f in [
                weights_file,
                safe_weights_file,
                weights_index_file,
                safe_weights_index_file,
                os.path.join(adapter_weights_file),
                os.path.join(adapter_safe_weights_file),
            ]
        ):
            raise ValueError(
                f"Can't find a valid checkpoint at {resume_from_checkpoint}"
            )

        logger.info(f"Loading model from {resume_from_checkpoint}.")

        if os.path.isfile(config_file):
            config = PretrainedConfig.from_json_file(config_file)
            checkpoint_version = config.transformers_version
            if checkpoint_version is not None and checkpoint_version != __version__:
                logger.warning(
                    f"You are resuming training from a checkpoint trained with {checkpoint_version} of "
                    f"Transformers but your current version is {__version__}. This is not recommended and could "
                    "yield to errors or unwanted behaviors."
                )

        if os.path.isfile(weights_file) or os.path.isfile(safe_weights_file):
            # If the model is on the GPU, it still works!
            if is_sagemaker_mp_enabled():
                if os.path.isfile(
                    os.path.join(resume_from_checkpoint, "user_content.pt")
                ):
                    # If the 'user_content.pt' file exists, load with the new smp api.
                    # Checkpoint must have been saved with the new smp api.
                    import smdistributed.modelparallel.torch as smp

                    smp.resume_from_checkpoint(
                        path=resume_from_checkpoint,
                        tag=WEIGHTS_NAME,
                        partial=False,
                        load_optimizer=False,
                    )
                else:
                    # If the 'user_content.pt' file does NOT exist, load with the old smp api.
                    # Checkpoint must have been saved with the old smp api.
                    if hasattr(self.args, "fp16") and self.args.fp16 is True:
                        logger.warning(
                            "Enabling FP16 and loading from smp < 1.10 checkpoint together is not suppported."
                        )
                    state_dict = torch.load(weights_file, map_location="cpu")
                    # Required for smp to not auto-translate state_dict from hf to smp (is already smp).
                    state_dict["_smp_is_partial"] = False
                    load_result = model.load_state_dict(state_dict, strict=True)
                    # release memory
                    del state_dict
            elif self.is_fsdp_enabled:
                load_fsdp_model(
                    self.accelerator.state.fsdp_plugin,
                    self.accelerator,
                    model,
                    resume_from_checkpoint,
                )
            else:
                # We load the model state dict on the CPU to avoid an OOM error.
                if self.args.save_safetensors and os.path.isfile(safe_weights_file):
                    state_dict = safetensors.torch.load_file(
                        safe_weights_file, device="cpu"
                    )
                else:
                    state_dict = torch.load(weights_file, map_location="cpu")

                # workaround for FSDP bug https://github.com/pytorch/pytorch/issues/82963
                # which takes *args instead of **kwargs
                load_result = model.load_state_dict(state_dict, False)
                # release memory
                del state_dict
                self._issue_warnings_after_load(load_result)

        # Load adapters following PR # 24096
        elif is_peft_available() and isinstance(model, PeftModel):
            # If train a model using PEFT & LoRA, assume that adapter have been saved properly.
            if hasattr(model, "active_adapter") and hasattr(model, "load_adapter"):
                adapter_model_path = os.path.join(
                    resume_from_checkpoint, adapter_name_path
                )
                if os.path.exists(adapter_model_path):
                    model.load_adapter(
                        adapter_model_path, model.active_adapter, is_trainable=True
                    )
                else:
                    logger.warning(
                        "The intermediate checkpoints of PEFT may not be saved correctly, "
                        f"consider using a custom callback to save {ADAPTER_WEIGHTS_NAME} in corresponding saving folders. "
                        "Check some examples here: https://github.com/huggingface/peft/issues/96"
                    )
            else:
                logger.warning(
                    "Could not load adapter model, make sure to have `peft>=0.3.0` installed"
                )
        else:
            # We load the sharded checkpoint
            load_result = load_sharded_checkpoint(
                model,
                resume_from_checkpoint,
                strict=is_sagemaker_mp_enabled(),
                prefer_safe=self.args.save_safetensors,
            )
            if not is_sagemaker_mp_enabled():
                self._issue_warnings_after_load(load_result)
