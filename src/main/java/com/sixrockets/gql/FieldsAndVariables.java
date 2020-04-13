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

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuperBuilder(toBuilder = true)
public abstract class FieldsAndVariables<T, E extends FieldsAndVariables<T, E>> {

	T parent;
	@Getter
	@Builder.Default
	private List<Variable<?>> variables = new ArrayList<>();
	@Builder.Default
	private List<Field<?>> fields = new ArrayList<>();
	@Builder.Default
	private List<Fragment> fragments = new ArrayList<>();

	public E cleanSelection() {
		fields = new ArrayList<>();
		fragments = new ArrayList<>();
		return (E) this;
	}

	public E variable(String name, Type type) {
		variable(name, type, name);
		return (E) this;
	}

	public E variable(String name, Type type, String variableReference) {
		Variable<?> newArg = Variable.builder().name(name).type(type).variableReference(variableReference).build();
		variables.add(newArg);
		return (E) this;
	}

	public E variable(String name, String value) {
		Variable<String> newArg = Variable.<String>builder().name(name).value(value).build();
		variables.add(newArg);
		return (E) this;
	}

	public E variable(String name, Boolean value) {
		Variable<Boolean> newArg = Variable.<Boolean>builder().name(name).value(value).build();
		variables.add(newArg);
		return (E) this;
	}

	public E variable(String name, Long value) {
		Variable<Long> newArg = Variable.<Long>builder().name(name).value(value).build();
		variables.add(newArg);
		return (E) this;
	}

	public E variable(String name, Integer value) {
		Variable<Integer> newArg = Variable.<Integer>builder().name(name).value(value).build();
		variables.add(newArg);
		return (E) this;
	}

	public E variable(String name, Float value) {
		Variable<Float> newArg = Variable.<Float>builder().name(name).value(value).build();
		variables.add(newArg);
		return (E) this;
	}

	public E variable(String name, Double value) {
		Variable<Double> newArg = Variable.<Double>builder().name(name).value(value).build();
		variables.add(newArg);
		return (E) this;
	}

	public E select(String name) {
		Field<E> newField = Field.<E>builder().name(name).build();
		fields.add(newField);
		return (E) this;
	}

	public Field<E> selectWith(String name) {
		Field<E> newField = Field.<E>builder().name(name).build();
		newField.parent = (E) this;
		fields.add(newField);
		return newField;
	}

	public E fragment(Fragment fragment) {
		fragments.add(fragment);
		return (E) this;
	}

	public T end() {
		return parent;
	}

	List<Variable<?>> findAllVariables() {
		List<Variable<?>> variables = new ArrayList<>();
		variables.addAll(this.variables);
		fields.forEach(f -> {
			variables.addAll(f.findAllVariables());
		});
		fragments.forEach(f -> {
			variables.addAll(f.findAllVariables());
		});
		return variables;
	}

	List<Fragment> findAllFragments() {
		List<Fragment> fragments = new ArrayList<>();
		fragments.addAll(this.fragments);
		fields.forEach(f -> {
			fragments.addAll(f.findAllFragments());
		});
		this.fragments.forEach(f -> {
			fragments.addAll(f.findAllFragments());
		});
		return fragments;
	}

	protected void toString(StringBuilder builder) {
		if (!variables.isEmpty())
			builder.append(variables.stream().map(v -> v.toString()).collect(Collectors.joining(", ", "(", ")")));
		if (!fields.isEmpty() || !fragments.isEmpty())
			builder.append(
				Stream.concat(
					fields.stream().map(f -> f.toString()),
					fragments.stream().map(f -> "..." + f.getName())
				)
			.collect(Collectors.joining("\n", " {\n", "\n}")));
	}
}
