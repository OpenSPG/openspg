# sample转换工具

对于chatglm2模型，只需要准备相对结构化的sample数据，可以通过convert_util.py，拉取spg schema信息，自动生成模型微调可接收的训练样本。

RE/sample.json

```json
{
    "input": "甲状腺结节是指在甲状腺内的肿块，可随吞咽动作随甲状腺而上下移动，是临床常见的病症，可由多种病因引起。临床上有多种甲状腺疾病，如甲状腺退行性变、炎症、自身免疫以及新生物等都可以表现为结节。甲状腺结节可以单发，也可以多发，多发结节比单发结节的发病率高，但单发结节甲状腺癌的发生率较高。患者通常可以选择在普外科，甲状腺外科，内分泌科，头颈外科挂号就诊。有些患者可以触摸到自己颈部前方的结节。在大多情况下，甲状腺结节没有任何症状，甲状腺功能也是正常的。甲状腺结节进展为其它甲状腺疾病的概率只有1%。有些人会感觉到颈部疼痛、咽喉部异物感，或者存在压迫感。当甲状腺结节发生囊内自发性出血时，疼痛感会更加强烈。治疗方面，一般情况下可以用放射性碘治疗，复方碘口服液(Lugol液)等，或者服用抗甲状腺药物来抑制甲状腺激素的分泌。目前常用的抗甲状腺药物是硫脲类化合物，包括硫氧嘧啶类的丙基硫氧嘧啶(PTU)和甲基硫氧嘧啶(MTU)及咪唑类的甲硫咪唑和卡比马唑。",
    "output": [
        {
            "subject": "甲状腺结节",
            "predicate": "名称",
            "object": "甲状腺结节"
        },
        {
            "subject": "甲状腺结节",
            "predicate": "就诊科室",
            "object": "普外科,甲状腺外科,内分泌科,头颈外科"
        },
        {
            "subject": "甲状腺结节",
            "predicate": "常见症状",
            "object": "颈部疼痛,咽喉部异物感,压迫感"
        },
        {
            "subject": "甲状腺结节",
            "predicate": "并发症",
            "object": "甲状腺癌"
        },
        {
            "subject": "甲状腺结节",
            "predicate": "适用药品",
            "object": "放射性碘,复方碘口服液(Lugol液),抗甲状腺药物(硫脲类化合物)"
        },
        {
            "subject": "甲状腺结节",
            "predicate": "发病部位",
            "object": "甲状腺"
        }
    ]
}
```

1、执行样本转换（sample.json->processed.json）：

```python
python convert.py \
    --entity_type Medical.Disease \
    --task_type RE \
    --src_path dataset/RE/sample.json \
    --tgt_path dataset/RE/processed.json \
    --template_path ../../../schema/prompt.json
```

2、执行p-tuning：

```shell
sh train.sh
```

3、将p-tuning的结果参数文件覆盖原模型参数文件，并执行以下命令部署模型服务：

```python
python deployer.py -m ../chatglm2-6b -H localhost -p 8888
```

4、算子内调用模型推理服务

```python
import requests
question = "Your Question"
data = {
    "prompt": question,
    "history": None,
}
response = requests.post(
    'http://localhost:8888',
    data=question.encode('utf-8'),
)
answer = response.text

```

