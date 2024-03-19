# -*- coding: utf-8 -*-
import os
import re
import uuid

import requests
import json
import ast
from binascii import b2a_hex
from typing import Dict, List
from urllib import request

from knext.builder.operator import ExtractOp, SPGRecord
from knext.builder.operator.sub_graph import SubGraph
from knext.schema.client import SchemaClient
from knext.schema.marklang.schema_ml import SPGSchemaMarkLang
from knext.schema.model.base import SpgTypeEnum
# from knext.common import arks_pb2
from knext.builder.operator.builtin.auto_prompt import SchemaPrompt
from knext.builder.operator.builtin.deepke_prompt import DeepKE_KGPrompt, DeepKE_EEPrompt
from Crypto.Cipher import AES


class AntGPTClient(object):
    def __init__(self, model, max_tokens=4096, temperature=1, n=1):
        self.model = model
        self.max_tokens = str(max_tokens)
        self.temperature = str(temperature)
        self.n = str(n)
        api_key, key = "***", "***"
        self.api_key = api_key
        self.url = "****"
        self.key = key

        self.paras = {
            "gpt-3.5-turbo": {
            },
            "gpt-4": {
            },
        }
        self.paras["gpt-4-32k"] = self.paras["gpt-4"]
        self.paras["gpt-4-turbo"] = self.paras["gpt-4"]

        self.param = self.paras[model]

    def aes_encrypt(self, data):
        """aes加密函数，如果data不是16的倍数【加密文本data必须为16的倍数！】，那就补足为16的倍数
        :param key:
        :param data:
        """
        # print(len(iv.encode('utf-8')))
        iv = "1234567890123456"
        cipher = AES.new(
            self.key.encode("utf-8"), AES.MODE_CBC, iv.encode("utf-8")
        )  # 设置AES加密模式 此处设置为CBC模式
        block_size = AES.block_size

        # 判断data是不是16的倍数，如果不是用b'\0'补足
        if len(data) % block_size != 0:
            add = block_size - (len(data) % block_size)
        else:
            add = 0
        data = data.encode("utf-8") + b"\0" * add
        encrypted = cipher.encrypt(data)  # aes加密
        result = b2a_hex(encrypted)  # b2a_hex encode  将二进制转换成16进制
        return result.decode("utf-8")

    def get_response(self, prompt):
        # import pdb; pdb.set_trace()
        encodeurl = "%s" % self.url.encode("utf8")
        self.param["queryConditions"] = {
            "url": encodeurl,
            "model": self.model,
            "max_tokens": self.max_tokens,
            "temperature": self.temperature,
            "n": self.n,
            "api_key": self.api_key,
            "messages": prompt,
        }
        data = json.dumps(self.param)
        post_data = {"encryptedParam": self.aes_encrypt(data)}
        response = requests.post(
            self.url,
            data=json.dumps(post_data),
            headers={"Content-Type": "application/json"},
        )
        print(response.json())
        x = response.json()["data"]["values"]["data"]
        ast_str = ast.literal_eval("'" + x + "'")
        js = ast_str.replace("&quot;", '"')
        js = js.replace("&#39;", "'")
        data = json.loads(js)
        content = data["choices"][0]["message"]["content"]
        content = content.replace("&rdquo;", "”").replace("&ldquo;", "“")
        return content

    def __call__(self, prompt):
        return self.get_response(prompt)

    def mock_response(self, s=[]):
        mock_response = """
Company(公司): EntityType
    properties:
        name(名称): Text
            desc: 公司的正式名称
        address(地址): Text
            desc: 公司注册的地址
        business(业务): Text
            desc: 公司主要的业务和产品

Individual(个人): EntityType
    properties:
        name(姓名): Text
            desc: 个人的姓名

Investment(投资事件): EventType
    properties:
        subject(主体): Company
        object(客体): Company
            constraint: MultiValue
        amount(金额): Float
        stake(股份): Float
        eventTime(发生时间): Text

UserMilestone(用户里程碑事件): EventType
    properties:
        subject(主体): Company
        userCount(用户数量): Integer
        eventTime(发生时间): Text

Partnership(合作伙伴关系事件): EventType
    properties:
        subject(主体): Company
        object(客体): Company
            constraint: MultiValue
        eventTime(发生时间): Text

Visit(参观访问事件): EventType
    properties:
        subject(主体): Individual
        object(客体): Company
        eventTime(发生时间): Text

ProductLaunch(产品发布事件): EventType
    properties:
        subject(主体): Company
        productName(产品名称): Text
        eventTime(发生时间): Text

OrganizationEstablishment(组织成立事件): EventType
    properties:
        subject(主体): Company
        structure(结构): Text
        eventTime(发生时间): Text

BusinessIntegration(业务整合事件): EventType
    properties:
        subject(主体): Company
        integratedEntity(被整合实体): Company
        eventTime(发生时间): Text

Reorganization(公司重组事件): EventType
    properties:
        subject(主体): Company
        newStructure(新架构): Text
            constraint: MultiValue
        eventTime(发生时间): Text
        """
        return mock_response


class MayaOpenClient(object):
    def __init__(self, max_tokens=4096, temperature=0, n=1, stream=False):
        self.max_tokens = str(max_tokens)
        self.temperature = str(temperature)
        self.n = str(n)
        self.url = "****"
        self.stream = stream

        self.param = {
            "stream": stream,
            "temperature": temperature
        }

    def get_response(self, prompt):
        post_data = {"text_input": prompt, "parameters": self.param}
        response = requests.post(
            self.url,
            data=json.dumps(post_data),
            headers={"Content-Type": "application/json"},
        )
        print(response.json())
        x = response.json()["text_output"]
        return x

    def __call__(self, prompt):
        return self.get_response(prompt)

    def mock_ae_response(self, s=""):
        mock_response = """
     {"公司": {"深圳市腾讯计算机系统有限公司": {}, "腾讯": {"业务": "拓展无线网络寻呼系统"}}}    
           """
        return mock_response

    def mock_ee_response(self, s=""):
        mock_response = """
     {"投资事件": [{"trigger": "投资", "arguments": {"主体": "腾讯", "客体": "DST", "金额": "约3亿美元", "股份": "NAN", "发生时间": "2010年4月12日"}}, {"trigger": "投资", "arguments": {"主体": "腾讯", "客体": "华谊兄弟传媒股份有限公司", "金额": "4.5亿元", "股份": "NAN", "发生时间": "2011年1月21日"}}, {"trigger": "投资", "arguments": {"主体": "腾讯", "客体": "艺龙网", "金额": "8440万美元", "股份": "NAN", "发生时间": "2011年5月17日"}}, {"trigger": "投资", "arguments": {"主体": "腾讯", "客体": "珂兰钻石", "金额": "数千万美元", "股份": "NAN", "发生时间": "2011年6月21日"}}], "用户里程碑事件": [{"trigger": "1亿", "arguments": {"主体": "腾讯", "用户数量": "1亿", "发生时间": "2010年3月5日"}}], "合作伙伴关系事件": [{"trigger": "合作伙伴", "arguments": {"主体": "腾讯", "客体": "美国思科公司", "发生时间": "2010年6月17日"}}], "参观访问事件": [{"trigger": "参观", "arguments": {"主体": "时任中共中央总书记、国家主席、中央军委主席胡锦涛一行", "客体": "腾讯公司", "发生时间": "2010年9月5日下午"}}], "产品发布事件": [{"trigger": "推出", "arguments": {"主体": "腾讯", "产品名称": "微信", "发生时间": "2011年1月21日"}}], "组织成立事件": [{"trigger": "成立", "arguments": {"主体": "深圳市腾讯计算机系统有限公司", "结构": "NAN", "发生时间": "1998年11月11日"}}], "业务整合事件": [], "公司重组事件": []}        
           """
        return mock_response


class AutoSchemaBuilder(ExtractOp):

    def __init__(self, params: Dict[str, str]):
        super().__init__()
        self.gpt_client = AntGPTClient(model="gpt-4-turbo")
        self.maya_client = MayaOpenClient()
        self.max_retry_times = 3
        self.namespace = params.get("namespace", "DEFAULT") if params else "DEFAULT"
        self.project_id = params.get("project_id", 1) if params else 1
        os.environ["KNEXT_PROJECT_ID"] = str(self.project_id)

    def invoke(self, record: Dict[str, str]) -> List[SPGRecord]:
        schema_prompt = SchemaPrompt()
        schema_query = schema_prompt.build_prompt(record)
        spg_types = None
        retry_times = 0
        print("---------------------Schema Prompt---------------------")
        print(schema_query)
        print("---------------------Schema Prompt---------------------")
        while retry_times < self.max_retry_times:
            print("\nAutomatic schema constructing...\n")
            try:
                content = [{"role": "user", "content": schema_query}]
                schema_response = self.gpt_client.mock_response(content)
            except Exception as e:
                retry_times += 1
                print("GPT invoke failed. Error msg: " + str(e))
                continue
            print("---------------------Schema Build Result---------------------")
            print(schema_response)
            print("---------------------Schema Build Result---------------------")
            try:
                schema_response = f"namespace {self.namespace}\n" + schema_response
                lines = schema_response.split('\n')
                filtered_response = ""
                is_filtered = False
                for line in lines:
                    if re.search(r'[ \t](name|eventTime)\(', line):
                        is_filtered = True
                        continue
                    if is_filtered and re.search(r'[ \t]desc:', line):
                        is_filtered = False
                        continue
                    filtered_response += line + '\n'
                schema_file = ".tmp.schema"
                with open(schema_file, "w", encoding="utf-8") as file:
                    file.write(filtered_response)
                ml = SPGSchemaMarkLang(schema_file)
                ml.sync_schema()
                schema = SchemaClient()
                session = schema.create_session()
                spg_types = session.spg_types
            except Exception as e:
                retry_times += 1
                print("Parse schema failed. Error msg: " + str(e))
            if spg_types:
                break

        if not spg_types:
            raise ValueError("Exceeded maximum retry count, automatic schema construction failed.")
        print("\nAutomatic schema construction finished.\n")

        entity_types, event_types = [], []
        for spg_type in spg_types.values():
            if spg_type.spg_type_enum == SpgTypeEnum.Entity:
                entity_types.append(spg_type.name)
            if spg_type.spg_type_enum == SpgTypeEnum.Event:
                event_types.append(spg_type.name)
        ae = DeepKE_KGPrompt(entity_types)
        print(ae.params)
        ae_prompt = ae.build_prompt(record)
        ee = DeepKE_EEPrompt(event_types)
        ee_prompt = ee.build_prompt(record)

        print("---------------------Entity Extract Prompt---------------------")
        print(ae_prompt)
        print("---------------------Entity Extract Prompt---------------------")

        print("---------------------Event Extract Prompt---------------------")
        print(ee_prompt)
        print("---------------------Event Extract Prompt---------------------")
        retry_times = 0
        while retry_times < self.max_retry_times:
            print("\nEntity extracting...\n")
            try:
                ae_response = self.maya_client.mock_ae_response(ae_prompt[0])
            except Exception as e:
                retry_times += 1
                print("Humming invoke failed. Error msg: " + str(e))
                continue
            print("---------------------Entity Extract Result---------------------")
            print(ae_response)
            print("---------------------Entity Extract Result---------------------")

            try:
                entity_records = ae.parse_response(ae_response)
            except Exception as e:
                retry_times += 1
                print("Parse entity extract response failed. Error msg: " + str(e))
                continue

            print("\nEntity extract finished.\n")
            print("\nEvent extracting...\n")
            try:
                ee_response = self.maya_client.mock_ee_response(ee_prompt[0])
            except Exception as e:
                retry_times += 1
                print("Humming invoke failed. Error msg: " + str(e))
                continue

            print("---------------------Event Extract Result---------------------")
            print(ee_response)
            print("---------------------Event Extract Result---------------------")
            try:
                event_records = ee.parse_response(ee_response)
            except Exception as e:
                retry_times += 1
                print("Parse event extract response failed. Error msg: " + str(e))
                continue
            print("\nEvent extract finished.\n")
            spg_records = entity_records + event_records

            print("---------------------SPGRecords---------------------")
            print(spg_records)
            print("---------------------SPGRecords---------------------")
            return SubGraph.from_spg_record(spg_types, spg_records)

        raise ValueError("Exceeded maximum retry count, entity and event extract failed.")

if __name__ == '__main__':
    a = AutoSchemaBuilder(params={"namespace": "DEFAULT"})
    record = {
        "entity_type": '公司发展事件',
        "input": """
1998年11月11日，马化腾和同学张志东在广东省深圳市正式注册成立“深圳市腾讯计算机系统有限公司”，之后许晨晔、陈一丹、曾李青相继加入。当时公司的业务是拓展无线网络寻呼系统，为寻呼台建立网上寻呼系统，这种针对企业或单位的软件开发工程是所有中小型网络服务公司的最佳选择。
1999年11月，腾讯于英属维尔京群岛注册成立，2004年2月迁册至英属开曼群岛。 [202]
2010年3月5日，19时52分58秒，腾讯QQ最高同时在线用户数突破1亿，这是人类进入互联网时代以来，全世界首次单一应用同时在线人数突破1亿。
2010年4月12日，腾讯与DST联合宣布腾讯向DST投资约3亿美元，两家公司将建立长期的战略伙伴关系，交易完成后，腾讯将持有DST约10.26%的经济权益。
2010年6月17日，腾讯与美国思科公司签署合作备忘录，双方将建立长期的战略合作伙伴关系。
2010年9月5日下午，时任中共中央总书记、国家主席、中央军委主席胡锦涛一行来到腾讯公司参观考察。
2011年1月21日，腾讯推出为智能手机提供即时通讯服务的免费应用程序：微信。
2011年5月9日，腾讯投资4.5亿元入股华谊兄弟传媒股份有限公司，投资完成后，腾讯持有华谊兄弟4.6%的股权，成为华谊兄弟第一大机构投资者。
2011年5月17日，腾讯投资8440万美元入股艺龙网，占艺龙总股份的16%，成为艺龙网第二大股东。
2011年6月21日，珂兰钻石宣布获得腾讯数千万美元级别投资。
2011年7月14日，腾讯公司党委举行了成立大会。
2012年5月，腾讯完成对电商网站易迅控股，易迅并入腾讯电商业务。
2012年5月18日，腾讯宣布进行公司组织架构调整，从原有的业务系统制升级为事业群制，划分为企业发展事业群（CDG）、互动娱乐事业群（IEG）、移动互联网事业群（MIG）、网络媒体事业群（OMG）、社交网络事业群（SNG）和技术工程事业群（TEG），并成立腾讯电商控股公司（ECC）专注运营电子商务业务。        
        """
    }
    print(a.invoke(record))
