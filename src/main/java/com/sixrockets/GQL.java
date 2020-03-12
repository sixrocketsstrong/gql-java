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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GQL implements FieldContainer<GQL>{

	private String declaration;
	private String builtQuery;
	@Getter
	private List<Field> fields = new ArrayList<>();
	private Map<String, Type> variables = new HashMap<>();

	private GQL(String declaration) {
		this.declaration = declaration;
	}

	@Override
	public GQL getRoot() {
		return this;
	}

	public String toString() {
		if (null != builtQuery) return builtQuery;

		StringBuilder builder = new StringBuilder();
		builder.append(declaration);
		if (!variables.isEmpty()) {
			builder.append(
				variables.entrySet().stream()
						.map(entry -> "$" + entry.getKey() + ": " + entry.getValue().toString())
						.collect(Collectors.joining(", ", "(", ")"))
			);
		}
		addFields(builder);
		builtQuery = builder.toString();

		return builtQuery;
	}

	public static GQL query(String opName) {
		return new GQL("query " + opName);
	}

	public static GQL mutation(String opName) {
		return new GQL("mutation " + opName);
	}

	public static GQL fragment(String name, String onType) {
		return new GQL("fragment " + name + " on " + onType);
	}

	void addVariables(List<Argument> arguments) {
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
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@EqualsAndHashCode
	public static class Type {
		private String name;
		private Boolean notNull;

		public String toString() {
			return (notNull) ? name + "!" : name;
		}

		public static Type nullable(String name) {
			return new Type(name, false);
		}

		public static Type notNull(String name) {
			return new Type(name, true);
		}
	}

	@NoArgsConstructor
	@Accessors(chain = true)
	@Data
	public static class Field implements FieldContainer<Field> {
		private GQL root;
		private List<Field> fields = new ArrayList<>();
		private List<Argument> arguments = new ArrayList<>();
		private FieldContainer<?> parent;
		private String name;

		public Argument argument(String name) {
			return argument(name, Type.nullable("String"));
		}
		public Argument argument(String name, Type type) {
			return argument(name, type, name);
		}
		public Argument argument(String name, Type type, String variableReference) {
			Argument newArg = new Argument().setParent(this).setName(name).setType(type).setVariableReference(variableReference);
			arguments.add(newArg);
			return newArg;
		}
		public FieldContainer<?> end() { root.addVariables(arguments); return parent; }

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(name);
			if (!arguments.isEmpty()) {
				builder.append(
						arguments.stream()
								.map(arg -> arg.getName() + ": $" + arg.getVariableReference())
								.collect(Collectors.joining(", ", "(", ")")));

			}
			addFields(builder);
			return builder.toString();
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@Accessors(chain = true)
	@Data
	public static class Argument {
		private Field parent;
		private String name;
		private Type type;
		private String variableReference;

		public Field end() { return parent; }
	}
}
