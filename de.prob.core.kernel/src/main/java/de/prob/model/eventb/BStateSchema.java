package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.Set;

import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.BSet;
import de.prob.model.representation.Constant;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.StateSchema;
import de.prob.model.representation.Variable;

public class BStateSchema implements StateSchema {

	public Object[] getElements(AbstractModel model) {
		ArrayList<Object> result = new ArrayList<Object>();
		result.addAll(model.getChildrenOfType(Context.class));
		result.addAll(model.getChildrenOfType(Machine.class));
		return result.toArray();
	};

	public Object[] getElements(Context context) {
		ArrayList<Object> result = new ArrayList<Object>();
		Set<BSet> sets = context.getChildrenOfType(BSet.class);
		result.addAll(sets);
		Set<Constant> constants = context.getChildrenOfType(Constant.class);
		result.addAll(constants);
		Set<Axiom> axioms = context.getChildrenOfType(Axiom.class);
		result.addAll(axioms);
		return result.toArray();
	};

	public Object[] getElements(Machine machine) {
		ArrayList<Object> result = new ArrayList<Object>();
		result.addAll(machine.getChildrenOfType(Variable.class));
		result.addAll(machine.getChildrenOfType(Invariant.class));
		return result.toArray();
	};

	public Object[] getElements(Object o) {
		if (o instanceof AbstractModel)
			return getElements((AbstractModel) o);
		if (o instanceof Context)
			return getElements((Context) o);
		if (o instanceof Machine)
			return getElements((Machine) o);
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object o) {
		return getElements(o).length > 0;
	}

}
