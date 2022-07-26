package com.sixrockets.gql;

import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.ListType;
import graphql.language.NamedNode;
import graphql.language.NonNullType;
import graphql.language.OperationDefinition;
import graphql.language.SelectionSetContainer;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.language.VariableDefinition;
import graphql.language.VariableReference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GraphQLJavaUtils {

	public static GQL queryFromField(
		Field field,
		OperationDefinition def,
		List<FragmentDefinition> fragments
	) {
		GQL val = (def.getOperation().equals(OperationDefinition.Operation.QUERY))
			? GQL.query()
			: GQL.mutation();
		Operation op = val.opWith(field.getName(), field.getAlias());

		addArguments(op, field, def);
		addSelections(op, field, def, fragments);
		return val;
	}

	public static GQL queryFromField(
		String operationName,
		Field field,
		OperationDefinition def,
		List<FragmentDefinition> fragments
	) {
		GQL val = (def.getOperation().equals(OperationDefinition.Operation.QUERY))
			? GQL.query(operationName)
			: GQL.mutation(operationName);
		Operation op = val.opWith(field.getName(), field.getAlias());

		addArguments(op, field, def);
		addSelections(op, field, def, fragments);
		return val;
	}

	public static void addSelections(
		FieldsAndVariables<?, ?> dest,
		SelectionSetContainer<?> selSet,
		OperationDefinition def,
		List<FragmentDefinition> fragments
	) {
		if (selSet.getSelectionSet() == null) return;
		selSet
			.getSelectionSet()
			.getSelections()
			.stream()
			.forEach(
				selection -> {
					if (selection instanceof NamedNode) {
						if (selection instanceof FragmentSpread) {
							FragmentSpread spread = (FragmentSpread) selection;
							fragments
								.stream()
								.filter(fd -> fd.getName().equals(spread.getName()))
								.findFirst()
								.ifPresent(
									fDef -> {
										Fragment fragment = GQL.fragment(
											"f" + UUID.randomUUID().toString().replace("-", ""),
											typeString(fDef.getTypeCondition())
										);
										addSelections(fragment, fDef, def, fragments);
										dest.fragment(fragment);
									}
								);
						} else {
							com.sixrockets.gql.Field<?> parentSel = dest.selectWith(
								((NamedNode<?>) selection).getName()
							);
							if (selection instanceof Field) {
								addArguments(parentSel, (Field) selection, def);
							}
							if (selection instanceof SelectionSetContainer) {
								SelectionSetContainer<?> container = (SelectionSetContainer<?>) selection;
								if (
									container.getSelectionSet() != null &&
									!container.getSelectionSet().getSelections().isEmpty()
								) {
									addSelections(parentSel, container, def, fragments);
								}
							}
						}
					} else if (selection instanceof InlineFragment) {
						InlineFragment inlineFragment = (InlineFragment) selection;
						Fragment fragment = GQL.fragment(
							"f" + UUID.randomUUID().toString().replace("-", ""),
							typeString(inlineFragment.getTypeCondition())
						);
						addSelections(fragment, inlineFragment, def, fragments);
						dest.fragment(fragment);
					}
				}
			);
	}

	public static void addArguments(
		FieldsAndVariables dest,
		Field field,
		OperationDefinition def
	) {
		field
			.getArguments()
			.stream()
			.forEach(
				arg -> {
					if (arg.getValue() instanceof VariableReference) {
						VariableReference value = (VariableReference) arg.getValue();
						Optional<VariableDefinition> varDef = def
							.getVariableDefinitions()
							.stream()
							.filter(d -> d.getName().equals(value.getName()))
							.findFirst();
						varDef.ifPresent(
							d -> dest.variable(arg.getName(), fromType(d.getType()))
						);
					}
				}
			);
	}

	public static com.sixrockets.gql.Type fromType(Type type) {
		if (type instanceof NonNullType) {
			return com.sixrockets.gql.Type.notNull(
				typeString(((NonNullType) type).getType())
			);
		} else {
			return com.sixrockets.gql.Type.nullable(typeString(type));
		}
	}

	private static String typeString(Type type) {
		if (type instanceof ListType) {
			return "[" + typeString(((ListType) type).getType()) + "]";
		} else {
			if (type instanceof NonNullType) {
				return typeString(((NonNullType) type).getType()) + "!";
			} else {
				return ((TypeName) type).getName();
			}
		}
	}
}
