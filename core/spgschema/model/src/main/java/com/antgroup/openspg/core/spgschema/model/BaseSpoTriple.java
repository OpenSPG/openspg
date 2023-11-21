/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.core.spgschema.model;

/**
 * RDF triple Class definition<br>
 *
 * <p>In RDF framework, all knowledge is represented by triples in the format of <subject,
 * predicate, object>. There are the following usage scenarios of representing knowledge using
 * triples:
 *
 * <ul>
 *   <li>Representing property: subject is generally an entity, concept or event. predicate is the
 *       name of property, object is literal constant or concept. For example, a knowledge such as
 *       the age of Jack is sixty years old，it can be represented by <Jack, age, 60>;
 *   <li>Representing relation: the object is usually an entity, concept or event. For example, The
 *       founder of Alibaba is Jack Ma, it can be represented by: <Alibaba, funder, Jack>;
 * </ul>
 */
public abstract class BaseSpoTriple extends BaseOntology {

  private static final long serialVersionUID = 7245368292263515301L;
}
