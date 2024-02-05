To create a knowledge graph based on OpenSPG,
we usually follow the steps below.

- Create a new knowledge graph project named "RiskMining".

```bash
knext project create --prj_path .
```

- First, create the schema of the project, which defines the ontology
  of the business domain of the knowledge graph. For example, in our RiskMining project,
  its ontology includes App, Cert, Person, Company, and their relations, etc.

```bash
knext schema commit
```

- Next, we can start building the knowledge graph based on the created schema and published operators.
  We can import entities such as App, Cert, Person, Company, along with their attributes and relations.
  After completing these operations, we will obtain a RiskMining knowledge graph.

```bash
knext builder execute TaxOfRiskUser,TaxOfRiskApp,Cert,Company,CompanyHasCert
knext builder execute App,Device,Person,PersonFundTrans,PersonHasDevice,PersonHoldShare
```

- Next, we can also explore some more exciting possibilities. We define some logical rules,
  which define what kind of App is a gambling App and what kind of users are
  developers of gambling Apps". These logic rules are calculated dynamically when necessary.

```bash
knext schema reg_concept_rule --file ./schema/concept.rule
```

- Finally, we execute GQL/KGDSL to perform queries and reasoning, inferring hidden knowledge
  based on existing factual data and logical rules within the knowledge graph.
  In the following example, we can output the phone number of each person, generate risk applications
  based on previously defined rules, and also output the developers and owners of these risk applications.
  It can be observed that this knowledge does not exist in the original factual data;
  rather, it is derived through inference based on the original factual data and the defined expert rules.
  Now, Let's run it!

```bash
knext reasoner execute --file ./reasoner/gambling_app.dsl
knext reasoner execute --dsl "MATCH (phone:STD.ChinaMobile)<-[:hasPhone]-(u:RiskMining.Person) RETURN u.name,phone.id"
knext reasoner execute --dsl "MATCH (s:\`RiskMining.TaxOfRiskApp\`/\`赌博应用\`) RETURN s.id"
knext reasoner execute --dsl "MATCH (s:\`RiskMining.TaxOfRiskUser\`/\`赌博App开发者\`) RETURN s.id,s.name"
```

