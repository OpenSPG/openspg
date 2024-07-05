import requests
import json
import time
import numpy as np
import random
from knext.ca.common.utils import logger


class IntermediateProcessTool(object):
    '''
    处理中间信息，比如打日志，比如输出到额外接口
    '''

    def __init__(self, debug_mode):
        self.debug_mode = debug_mode

    def set_debug_mode(self, debug_mode):
        self.debug_mode = debug_mode

    def process(self, info_dict):
        pass


class LoggerIntermediateProcessTool(IntermediateProcessTool):
    def __init__(self, debug_mode):
        super().__init__(debug_mode)

    def process(self, info_dict):
        if self.debug_mode:
            if 'log_info' in info_dict:
                debug_str = '\n' + info_dict['status'] + '\n'
                debug_str += info_dict['log_info']
            else:
                debug_str = json.dumps(info_dict, ensure_ascii=False)
            debug_str += '\n'
            logger.info(debug_str)

