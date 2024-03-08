import re
import json
from ie2instruction.eval.extracter.extracter import Extracter


class EEAExtracter(Extracter):
    def __init__(self, language="zh", NAN="NAN", prefix="输入中包含的实体是：\n", Reject="No event found."):
        super().__init__(language, NAN, prefix, Reject)
    

    def post_process(self, result):   
        try:      
            rst = json.loads(result)
        except json.decoder.JSONDecodeError:
            print("json decode error", result)
            return False, []
        new_record = []
        if type(rst) != dict:
            print("type(rst) != dict", result)
            return False, []
        for key, values in rst.items():
            if type(key) != str or type(values) != list:
                print("type(key) != str or type(values) != list", result)
                continue
            for value in values:
                if type(value) != dict:
                    print("type(value) != dict", result)
                    continue
                args = []
                for key1, value1 in value.items():
                    if type(value1) == list:
                        for iit in value1:
                            if iit == self.NAN:
                                continue
                            args.append((iit, key1))
                    else:
                        if value1 == self.NAN:
                            continue
                        args.append((value1, key1))
                new_record.append((key, '', tuple(args)))
        return True, new_record

