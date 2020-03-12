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

import com.sixrockets.GQL.Field;

import java.util.List;
import java.util.stream.Collectors;

public interface FieldContainer<T extends FieldContainer<?>> {

	public GQL getRoot();

	public List<Field> getFields();

	public default T simpleField(String name) {
		return (T) startField(name).end();
	}

	public default Field startField(String name) {
		Field newField = new Field().setRoot(getRoot()).setName(name).setParent(this);
		getFields().add(newField);
		return newField;
	}

	public default void addFields(StringBuilder builder) {
		if (!getFields().isEmpty()) {
			builder.append(getFields().stream()
					.map(f -> f.toString())
					.collect(Collectors.joining("\n", " {\n", "\n}\n"))
			);
		}
	}
}
