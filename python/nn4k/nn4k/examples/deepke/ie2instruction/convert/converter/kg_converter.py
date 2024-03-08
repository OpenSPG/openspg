import json
from collections import defaultdict, OrderedDict
from ie2instruction.convert.converter.converter import Converter


class KGConverter(Converter):
    def __init__(self, language = "zh", NAN="NAN", prefix = ""):
        super().__init__(language, NAN, prefix)

    def get_label_dict(self, records):
        head_labels = defaultdict(list)
        for rel in records:
            head = rel['head']
            relation = rel['relation']
            tail = rel['tail']
            head_labels[head].append([relation, tail])
        labels = {}
        for head, value in head_labels.items():
            relation_labels = defaultdict(list)
            for it in value:
                relation_labels[it[0]].append(it[1])
            labels[head] = relation_labels
        return labels


    def convert_target(self, text, schema, label_dict): 
        outputs = defaultdict(list)
        # 排序, 按照头实体在文本中的位置
        sorted_label_dict = sorted(label_dict.items(), key=lambda x:text.find(x[0]))
        for it in schema:
            tmp_outputs = {}
            for head, value in sorted_label_dict:
                entity_attr = OrderedDict()
                flag = False
                for attribute in it['attributes']:
                    if attribute in value.keys():
                        flag = True
                        tails = value[attribute]
                        tails_index = []
                        for tail in tails:
                            tails_index.append([text.find(tail), tail])
                        tails_index.sort(key=lambda x:x[0])    # 按照尾实体在文本中的位置排序
                        tails = []
                        for tail in tails_index:
                            tails.append(tail[1])
                        if len(tails) == 1:  # 如果只有一个尾实体, 字符串
                            entity_attr[attribute] = tails[0]
                        else:                # 如果有多个尾实体, 列表
                            entity_attr[attribute] = tails
                if not flag:
                    entity_attr = {}
                tmp_outputs[head] = dict(entity_attr)
            outputs[it['entity_type']] = tmp_outputs
        output_text = json.dumps(dict(outputs), ensure_ascii=False)
        return self.prefix + output_text
    







