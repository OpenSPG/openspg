import json
NAN = 'NAN'


def ner_post_process(result):  
    try:      
        rst = json.loads(result)
    except json.decoder.JSONDecodeError:
        print("json decode error", result)
        return False, []
    if type(rst) != dict:
        print("type(rst) != dict", result)
        return False, []
    new_record = []
    for key, values in rst.items():
        if type(key) != str or type(values) != list:
            print("type(key) != str or type(values) != list", result)
            continue
        for iit in values:
            if type(iit) != str:
                print("type(iit) != str", result)
                continue
            new_record.append((iit, key))
    return True, new_record

def re_post_process(result):        
    try:      
        rst = json.loads(result)
    except json.decoder.JSONDecodeError:
        print("json decode error", result)
        return False, []
    if type(rst) != dict:
        print("type(rst) != dict", result)
        return False, []
    new_record = []
    for key, values in rst.items():
        if type(key) != str or type(values) != list:
            print("type(key) != str or type(values) != list", result)
            continue
        for iit in values:
            if type(iit) != dict:
                print("type(iit) != dict", result)
                continue
            head = iit.get('subject', '')
            tail = iit.get('object', '')
            if type(head) != str or type(tail) != str:
                print("type(head) != str or type(tail) != str", result)
                continue
            new_record.append((head, key, tail))
    return True, new_record


def kg_post_process(result):   
    try:      
        rst = json.loads(result)
    except json.decoder.JSONDecodeError:
        print("json decode error", result)
        return False, []
    if type(rst) != dict:
        print("type(rst) != dict", result)
        return False, []
    new_record = []
    for key, values in rst.items():  # entity_type
        if type(key) != str or type(values) != dict:
            print("type(key) != str or type(values) != dict", result)
            continue
        for key1, values1 in values.items():   # entity, attributes
            if type(key1) != str or type(values1) != dict:
                print("type(key1) != str or type(values1) != dict", result)
                continue
            for key2, values2 in values1.items(): # key, value
                if type(values2) == list:
                    for iit in values2:
                        new_record.append((key1, key2, iit))
                elif type(values2) == str:
                    new_record.append((key1, key2, values2))
    return True, new_record


def ee_post_process(result):   
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
            trigger = value.get('trigger', '')
            if type(trigger) != str:
                print("type(trigger) != str", result)
                continue
            args = []
            arguments = value.get('arguments', {})
            for key1, value1 in arguments.items():
                if type(value1) == list:
                    for iit in value1:
                        if iit == NAN:
                            continue
                        args.append((iit, key1))
                else:
                    if value1 == NAN:
                        continue
                    args.append((value1, key1))
            new_record.append((key, trigger, tuple(args)))
    return True, new_record


def eet_post_process(result):  
    try:      
        rst = json.loads(result)
    except json.decoder.JSONDecodeError:
        print("json decode error", result)
        return False, []
    if type(rst) != dict:
        print("type(rst) != dict", result)
        return False, []
    new_record = []
    for key, values in rst.items():
        if type(key) != str or type(values) != list:
            print("type(key) != str or type(values) != list", result)
            continue
        for iit in values:
            if type(iit) != str:
                print("type(iit) != str", result)
                continue
            new_record.append((iit, key))
    return True, new_record


def eea_post_process(result):   
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
                        if iit == NAN:
                            continue
                        args.append((iit, key1))
                else:
                    if value1 == NAN:
                        continue
                    args.append((value1, key1))
            new_record.append((key, '', tuple(args)))
    return True, new_record


def get_extract_func(task):
    if task == 'NER':
        return ner_post_process
    elif task == 'RE':
        return re_post_process
    elif task == 'KG':
        return kg_post_process
    elif task == 'EE':
        return ee_post_process
    elif task == 'EET':
        return eet_post_process
    elif task == 'EEA':
        return eea_post_process