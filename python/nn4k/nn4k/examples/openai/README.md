# NN4K example: inference with OpenAI

## Install dependencies

```bash
python3 -m venv .env
source .env/bin/activate
python -m pip install --upgrade pip
python -m pip install openspg-nn4k
```

## Edit configurations

Edit configurations in [openai_infer.json](./openai_infer.json).

* Set ``openai_api_base`` to an OpenAI api compatible base url.
  To invoke the official OpenAI api service, set this field to
  ``https://api.openai.com/v1``.

* Set ``openai_api_key`` to a valid OpenAI api key.
  For the official OpenAI api service, you can find your api key
  ``sk-xxx`` in the [API keys](https://platform.openai.com/api-keys) page.

## Run the example

```bash
python openai_infer.py
```

