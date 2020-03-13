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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GQL {

	private String declaration;
	private String builtQuery;
	private List<Operation> operations = new ArrayList<>();

	private GQL(String declaration) {
		this.declaration = declaration;
	}

	public GQL op(String operation) {
		return op(operation, null);
	}

	public GQL op(String operation, String alias) {
		Operation op = operation(operation);
		op.parent = this;
		operations.add(op);
		return this;
	}

	public GQL op(Operation operation) {
		Operation op = operation.toBuilder().build();
		op.parent = this;
		operations.add(operation);
		return this;
	}

	public Operation opWith(String operation) {
		return opWith(operation, null);
	}

	public Operation opWith(String operation, String alias) {
		Operation op = operation(operation);
		op.parent = this;
		operations.add(op);
		return op;
	}

	public Operation opWith(Operation operation) {
		Operation op = operation.toBuilder().build().cleanSelection();
		op.parent = this;
		operations.add(operation);
		return op;
	}

	public String toString() {
		if (null != builtQuery) return builtQuery;

		StringBuilder builder = new StringBuilder();
		builder.append(declaration);
		Map<String, Type> variables = findAllVariables();
		if (!variables.isEmpty()) {
			builder.append(
				variables.entrySet().stream()
				.map(entry -> "$" + entry.getKey() + ": " + entry.getValue().toString())
				.collect(Collectors.joining(", ", "(", ")"))
				);
		}
		if (!operations.isEmpty())
				builder.append(operations.stream().map(o -> o.toString()).collect(Collectors.joining("\n", " {\n", "\n}")));

		List<Fragment> fragments = findAllFragments();
		if (!fragments.isEmpty())
				builder.append(fragments.stream().map(o -> o.toString()).collect(Collectors.joining("\n", "\n", "")));

		builtQuery = builder.toString();

		return builtQuery;
	}

	public static GQL query(String opName) {
		return new GQL("query " + opName);
	}

	public static GQL mutation(String opName) {
		return new GQL("mutation " + opName);
	}

	public static Operation operation(String operation) {
		return operation(operation, null);
	}

	public static Operation operation(String operation, String alias) {
		return Operation.builder().operation(operation).alias(alias).build();
	}

	public static Fragment fragment(String name, String onType) {
		return Fragment.builder().name(name).onType(onType).build();
	}

	Map<String, Type> findAllVariables() {
		Map<String, Type> variables = new HashMap<>();
		List<Variable<?>> arguments = new ArrayList<>();
		operations.forEach(o -> {
			arguments.addAll(o.findAllVariables());
		});
		arguments.forEach(arg -> {
			if (variables.containsKey(arg.getVariableReference())) {
				if (!variables.get(arg.getVariableReference()).equals(arg.getType())) {
					throw new IllegalArgumentException(String.format(
						"Same variable reference %s with differing types: %s vs %s",
						arg.getVariableReference(),
						arg.getType(),
						variables.get(arg.getVariableReference())
					));
				}
			}
			variables.put(arg.getVariableReference(), arg.getType());
		});

		return variables;
	}

	List<Fragment> findAllFragments() {
		List<Fragment> fragments = new ArrayList<>();
		operations.forEach(o -> {
			fragments.addAll(o.findAllFragments());
		});

		return fragments;
	}
}
