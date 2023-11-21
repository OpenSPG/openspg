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


package com.antgroup.openspg.api.http.client

import com.antgroup.openspg.api.facade.client.SchemaFacade
import com.antgroup.openspg.api.facade.dto.schema.request.SPGTypeRequest
import com.antgroup.openspg.api.http.client.util.ConnectionInfo
import com.antgroup.openspg.api.http.client.util.HttpClientBootstrap
import spock.lang.Ignore
import spock.lang.Specification

/**
 *  */
@Ignore
class HttpSpgSchemaFacadeTest extends Specification {

	static {
		HttpClientBootstrap.init(new ConnectionInfo()
				.setScheme("http").setHost("127.0.0.1")
				.setPort("8887").setPrintLog(true)
				)
	}

	def testQuerySpgType() {
		expect:
		SchemaFacade facade = new HttpSchemaFacade();

		def request = new SPGTypeRequest()
		request.setName("FraudTest1.Cert")
		def querySpgType = facade.querySPGType(request)
		println querySpgType
	}
}
