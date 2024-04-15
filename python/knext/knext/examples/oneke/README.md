
```bash
knext project create --name OneKE --namespace OneKE --desc OneKE
```

```bash
knext schema commit
```

```bash
knext builder execute Company
```

```bash
knext reasoner query --file ./reasoner/company.dsl
```