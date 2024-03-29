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

import asyncio
import functools


def is_awaitable(value):
    if isinstance(value, asyncio.Task):
        return True
    else:
        return False


async def resolve_awaitables(value):
    # If the value is an awaitable, resolve it
    if is_awaitable(value):
        value = await value
        # After resolution, check if the result is a dict or list
        return await resolve_awaitables(value)

    # If the value is a list, resolve each item
    if isinstance(value, list):
        # Create and return a list with all awaitables resolved
        return [await resolve_awaitables(item) for item in value]

    # If the value is a dict, resolve each value
    if isinstance(value, dict):
        # Create and return a dict with all awaitables resolved
        return {k: await resolve_awaitables(v) for k, v in value.items()}

    # If the value is neither an awaitable, list, nor dict, return it as is
    return value


class CABaseModule(object):
    def __init__(self):
        self.tg = asyncio.get_event_loop()

    def __call__(self, **kwargs):
        return_as_native = kwargs.pop('return_as_native', False)
        task = self.tg.create_task(self._async_invoke(**kwargs))
        if return_as_native:
            return self.tg.run_until_complete(self.wait_all_tasks_finished(task))
        else:
            return task

    async def wait_all_tasks_finished(self, task):
        return await resolve_awaitables(task)

    async def _async_invoke(self, **kwargs):
        # await kwargs if need
        resolved_kwargs = await resolve_awaitables(kwargs)
        func = functools.partial(self.invoke, **resolved_kwargs)
        result = await self.tg.run_in_executor(None, func)
        return result

    async def _resolve_kwargs(self, kwargs):
        resolved = {}
        for key, value in kwargs.items():
            if is_awaitable(value):
                resolved[key] = await value
            else:
                resolved[key] = value
        return resolved

    def invoke(self, **kwargs):
        raise NotImplementedError
