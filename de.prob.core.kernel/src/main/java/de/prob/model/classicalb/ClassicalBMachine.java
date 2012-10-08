package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.AbstractDomTreeElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Label;
import de.prob.model.representation.Operation;

public class ClassicalBMachine extends AbstractDomTreeElement implements
		AbstractElement {

	private enum ESection {
		SETS, PARAMETERS, CONSTRAINTS, CONSTANTS, PROPERTIES, VARIABLES, INVARIANT, ASSERTIONS, USER_FORMULAS
	}

	private final NodeIdAssignment astMapping;

	public ClassicalBMachine(final NodeIdAssignment nodeIdAssignment) {
		this.astMapping = nodeIdAssignment;
	}

	public Node getNode(final int i) {
		return astMapping.lookupById(i);
	}

	private String name;
	private boolean locked = false;
	private final List<ClassicalBEntity> sets = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> parameters = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> constraints = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> constants = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> properties = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> variables = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> invariant = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> assertions = new ArrayList<ClassicalBEntity>();
	private final List<Operation> operations = new ArrayList<Operation>();

	private final Map<ESection, Label> labels = new HashMap<ESection, Label>();

	public List<ClassicalBEntity> constants() {
		return lock(constants);
	}

	public List<ClassicalBEntity> variables() {
		return lock(variables);
	}

	public List<ClassicalBEntity> parameters() {
		return lock(parameters);
	}

	public List<ClassicalBEntity> invariant() {
		return lock(invariant);
	}

	public List<ClassicalBEntity> assertions() {
		return lock(assertions);
	}

	public List<ClassicalBEntity> constraints() {
		return lock(constraints);
	}

	public List<ClassicalBEntity> properties() {
		return lock(properties);
	}

	public List<Operation> operations() {
		if (locked)
			return Collections.unmodifiableList(operations);
		return operations;
	}

	public List<ClassicalBEntity> sets() {
		return lock(sets);
	}

	public String name() {
		return name;
	}

	public void setName(final String name) {
		if (locked)
			throw new UnsupportedOperationException(
					"Must not modify Machine after it has been locked");
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void close() {
		locked = true;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ClassicalBMachine) {
			final ClassicalBMachine that = (ClassicalBMachine) obj;
			return that.name.equals(name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	private List<ClassicalBEntity> lock(final List<ClassicalBEntity> p) {
		if (locked)
			return Collections.unmodifiableList(p);
		return p;
	}

	public String print() {
		final StringBuilder sb = new StringBuilder();
		if (!sets.isEmpty()) {
			sb.append("Sets:\n");
			for (final ClassicalBEntity set : sets) {
				sb.append("  " + set.toString() + "\n");
			}
		}
		if (!parameters.isEmpty()) {
			sb.append("Parameters:\n");
			for (final ClassicalBEntity parameter : parameters) {
				sb.append("  " + parameter.toString() + "\n");
			}
		}
		if (!constraints.isEmpty()) {
			sb.append("Constraints:\n");
			for (final ClassicalBEntity constraint : constraints) {
				sb.append("  " + constraint.toString() + "\n");
			}
		}
		if (!constants.isEmpty()) {
			sb.append("Constants:\n");
			for (final ClassicalBEntity constant : constants) {
				sb.append("  " + constant.toString() + "\n");
			}
		}
		if (!properties.isEmpty()) {
			sb.append("Properties:\n");
			for (final ClassicalBEntity property : properties) {
				sb.append("  " + property.toString() + "\n");
			}
		}
		if (!variables.isEmpty()) {
			sb.append("Variables:\n");
			for (final ClassicalBEntity variable : variables) {
				sb.append("  " + variable.toString() + "\n");
			}
		}
		if (!invariant.isEmpty()) {
			sb.append("Invariant:\n");
			for (final ClassicalBEntity inv : invariant) {
				sb.append("  " + inv.toString() + "\n");
			}
		}
		if (!assertions.isEmpty()) {
			sb.append("Assertions:\n");
			for (final ClassicalBEntity assertion : assertions) {
				sb.append("  " + assertion.toString() + "\n");
			}
		}
		if (!operations.isEmpty()) {
			sb.append("Operations:\n");
			for (final Operation operation : operations) {
				sb.append("  " + operation.toString() + "\n");
			}
		}
		return sb.toString();
	}

	@Override
	public List<String> getVariableNames() {
		final List<String> vars = new ArrayList<String>();
		for (final ClassicalBEntity var : variables) {
			vars.add(var.getIdentifier());
		}
		return vars;
	}

	@Override
	public List<String> getConstantNames() {
		final List<String> cons = new ArrayList<String>();
		for (final ClassicalBEntity con : constants) {
			cons.add(con.getIdentifier());
		}
		return cons;
	}

	@Override
	public List<String> getOperationNames() {
		final List<String> ops = new ArrayList<String>();
		for (final Operation op : operations) {
			ops.add(op.toString());
		}
		return ops;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public List<AbstractDomTreeElement> getSubcomponents() {
		return new ArrayList<AbstractDomTreeElement>(labels.values());
	}

	public void createLabels() {
		if (!sets.isEmpty()) {
			final Label setLabel = new Label("Sets");
			for (final ClassicalBEntity set : sets) {
				setLabel.addFormula(set);
			}
			labels.put(ESection.SETS, setLabel);
		}
		if (!parameters.isEmpty()) {
			final Label label = new Label("Parameters");
			for (final ClassicalBEntity param : parameters) {
				label.addFormula(param);
			}
			labels.put(ESection.PARAMETERS, label);
		}
		if (!constraints.isEmpty()) {
			final Label label = new Label("Constraints");
			for (final ClassicalBEntity constraint : constraints) {
				label.addFormula(constraint);
			}
			labels.put(ESection.CONSTRAINTS, label);
		}
		if (!constants.isEmpty()) {
			final Label label = new Label("Constants");
			for (final ClassicalBEntity constant : constants) {
				label.addFormula(constant);
			}
			labels.put(ESection.CONSTANTS, label);
		}
		if (!properties.isEmpty()) {
			final Label label = new Label("Properties");
			for (final ClassicalBEntity prop : properties) {
				label.addFormula(prop);
			}
			labels.put(ESection.PROPERTIES, label);
		}
		if (!variables.isEmpty()) {
			final Label label = new Label("Variables");
			for (final ClassicalBEntity var : variables) {
				label.addFormula(var);
			}
			labels.put(ESection.VARIABLES, label);
		}
		if (!invariant.isEmpty()) {
			final Label label = new Label("Invariant");
			for (final ClassicalBEntity inv : invariant) {
				label.addFormula(inv);
			}
			labels.put(ESection.INVARIANT, label);
		}
		if (!assertions.isEmpty()) {
			final Label label = new Label("Assertions");
			for (final ClassicalBEntity assertion : assertions) {
				label.addFormula(assertion);
			}
			labels.put(ESection.ASSERTIONS, label);
		}
		labels.put(ESection.USER_FORMULAS, new Label("Formulas"));
	}

	@Override
	public boolean toEvaluate() {
		return false;
	}
}
