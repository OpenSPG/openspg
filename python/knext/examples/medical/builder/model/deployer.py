# -*- coding: utf-8 -*-
#
#  Copyright 2023 Ant Group CO., Ltd.
#
#  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software distributed under the License
#  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied.

import argparse
import json
from http.server import HTTPServer, BaseHTTPRequestHandler


def MakeHandlerClassFromParams(model_path: str):
    from transformers import AutoTokenizer, AutoModel
    tokenizer = AutoTokenizer.from_pretrained(model_path, trust_remote_code=True)
    model = AutoModel.from_pretrained(model_path, trust_remote_code=True).half().cuda()
    model = model.eval()
    print("----------->init model success<------------")

    class Request(BaseHTTPRequestHandler):
        def do_POST(self):
            content_len = int(self.headers.get('Content-Length'))
            post_data = str(self.rfile.read(content_len), encoding="utf-8")
            json_data = json.loads(post_data)
            print(json_data)
            prompt_data = json_data["prompt"]

            history = []
            if json_data.get("history"):
                history_data = json.loads(json_data["history"])
                for data in history_data:
                    history.append((data[0], data[1]))

            response, history = model.chat(tokenizer, prompt_data, history=history)

            self.send_response(200)
            self.send_header('Content-type', 'text/html;charset=utf-8')
            self.end_headers()
            self.wfile.write(response.encode("utf-8"))

    return Request


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--model_path", help="local model path")
    parser.add_argument("-H", "--host", help="host", default="localhost")
    parser.add_argument("-p", "--port", help="port", default="8888")
    args = parser.parse_args()

    server = HTTPServer((args.host, int(args.port)), MakeHandlerClassFromParams(args.model_path))
    print("Starting server, listen at: %s:%s" % (args.host, args.port))
    server.serve_forever()
