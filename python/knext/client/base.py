import os
from abc import ABC


class Client(ABC):
    def __init__(self, host_addr: str = None, project_id: int = None):
        self._host_addr = host_addr or os.environ.get("KNEXT_HOST_ADDR")
        self._project_id = project_id or os.environ.get("KNEXT_PROJECT_ID")
