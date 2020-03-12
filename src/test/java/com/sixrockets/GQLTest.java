/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.sixrockets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GQLTest {

	@Test
	void emptyQuery() {
		assertEquals("query foo", GQL.query("foo").toString());
	}

	@Test
	void queryWithNoArgNoSelect() {
		assertEquals(
			"query foo {\nbar\n}\n",
			GQL.query("foo")
				.simpleField("bar")
				.toString()
		);
	}

	@Test
	void queryWithOneArgNoSelect() {
		assertEquals(
			"query foo($baz: String) {\nbar(baz: $baz)\n}\n",
			GQL.query("foo")
				.startField("bar")
						.argument("baz")
						.end()
				.end()
				.toString()
		);
	}
}
