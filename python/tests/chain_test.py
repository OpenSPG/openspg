import json

response_str = "[{'财政': ['财政收入质量', '财政自给能力', '土地出让收入', '一般公共预算收入', '留抵退税', '税收收入', '税收收入/一般公共预算收入', '一般公共预算支出', '财政自给率', '政府性基金收入', '转移性收入', '综合财力']}]"

response_str = response_str.replace("'", "\"")

output_list = json.loads(response_str)

