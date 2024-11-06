# Copyright 2023 OpenSPG Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

from knext.schema.marklang.schema_ml import SPGSchemaMarkLang


def test_load_schema():
    schema_file = "../../knext/examples/medicine/schema/medicine.schema"
    ml = SPGSchemaMarkLang(schema_file)
    for _, m in ml.types.items():
        for r in m.relations.values():
            print(r.object_type_name)
    print(ml.types)


test_load_schema()
