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

import logging
import inspect


class CustomLogger(logging.Logger):
    def findCaller(self, stack_info=False, stacklevel=1):
        frame = logging.currentframe()
        # Get the desired stack level
        for _ in range(stacklevel):
            if frame is not None:
                frame = frame.f_back
        # Find the first frame outside the logging module
        while frame and frame.f_code.co_filename == logging.__file__:
            frame = frame.f_back
        if not frame:
            return "(unknown file)", 0, "(unknown function)", None
        co = frame.f_code
        return (co.co_filename, frame.f_lineno, co.co_name, None)


logging.setLoggerClass(CustomLogger)
logging.basicConfig(
    level=logging.INFO,
    format="[%(asctime)s @ %(process)d] [%(levelname)s] [%(filename)s:%(lineno)d:%(funcName)s] %(message)s",
)

logger = logging.getLogger(__name__)
