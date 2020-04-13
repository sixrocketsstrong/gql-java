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
package com.sixrockets.gql;

import static com.sixrockets.gql.FileAsserts.assertContentsEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class GQLTest {

	private static final Fragment bipBeepBoop = GQL.fragment("bipBeepBoop", "Baz")
		.select("bip")
		.select("beep")
		.select("boop");

	@Test
	void emptyQuery() {
		assertEquals("query foo", GQL.query("foo").toString());
	}

	@Test
	void queryWithNoArgNoSelect() {
		assertContentsEquals(
			"queryWithNoArgNoSelect.graphql",
			GQL.query("foo")
				.op("bar")
				.toString()
		);
	}

	@Test
	void queryWithOneArgNoSelect() {
		assertEquals(
			"query foo($baz: String) {\nbar(baz: $baz)\n}",
			GQL.query("foo")
				.opWith("bar")
					.variable("baz", Type.nullable("String"))
				.end()
			.toString()
		);
	}

	@Test
	void queryWithSelection() {
		assertEquals(
			"query foo {\nbar {\nbaz\n}\n}",
			GQL.query("foo")
				.opWith("bar")
					.select("baz")
				.end()
			.toString()
		);
	}

	@Test
	void queryWithFragment() {
		assertEquals(
			"query foo {\nbar {\n...bipBeepBoop\n}\n}\nfragment bipBeepBoop on Baz {\nbip\nbeep\nboop\n}",
			GQL.query("foo")
				.opWith("bar")
					.fragment(bipBeepBoop)
				.end()
			.toString()
		);
	}
}
