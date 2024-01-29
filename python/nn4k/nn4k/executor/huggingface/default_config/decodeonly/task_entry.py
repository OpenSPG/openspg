from nn4k.invoker.base import NNInvoker


def main():
    NNInvoker.from_config("local_sft.json5").local_sft()
    # Inference example, not implemented yet.
    # NNInvoker.from_config("inferece_args.json").local_inference("你是谁")


if __name__ == "__main__":
    main()
