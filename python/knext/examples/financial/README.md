```bash
knext project create --name 全风 --namespace Financial --desc 全风财政指标抽取
```

```bash
knext schema commit
```

```bash
knext operator publish DemoExtractOp
```

```bash
knext builder submit Demo
```

```bash
knext reasoner query --file ./reasoner/demo.dsl
```

