package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.BSet;
import de.prob.model.representation.Constant;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.StateSchema;
import de.prob.model.representation.Variable;

public class BStateSchema implements StateSchema {

	public Object[] getElements(final AbstractModel model) {
		ArrayList<Object> result = new ArrayList<Object>();
		result.addAll(model.getChildrenOfType(Context.class));
		result.addAll(model.getChildrenOfType(Machine.class));
		return result.toArray();
	};

	public Object[] getElements(final Context context) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (Entry<Class<? extends AbstractElement>, Set<? extends AbstractElement>> entry : context
				.getChildren().entrySet()) {
			Class<? extends AbstractElement> key = entry.getKey();
			if (key.equals(BSet.class) || key.equals(Constant.class)
					|| key.equals(Axiom.class)) {
				if (!entry.getValue().isEmpty()) {
					result.add(entry);
				}
			}
		}
		return result.toArray();
	};

	public Object[] getElements(final Machine machine) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (Entry<Class<? extends AbstractElement>, Set<? extends AbstractElement>> entry : machine
				.getChildren().entrySet()) {
			Class<? extends AbstractElement> key = entry.getKey();
			if (key.equals(Invariant.class) || key.equals(Variable.class)) {
				if (!entry.getValue().isEmpty()) {
					result.add(entry);
				}
			}
		}
		return result.toArray();
	};

	@Override
	public Object[] getElements(final Object o) {
		if (o instanceof AbstractModel) {
			return getElements((AbstractModel) o);
		}
		if (o instanceof Context) {
			return getElements((Context) o);
		}
		if (o instanceof Machine) {
			return getElements((Machine) o);
		}
		if (o instanceof Entry<?, ?>) {
			if (((Entry<?, ?>) o).getValue() instanceof Set<?>) {
				return ((Set<?>) ((Entry<?, ?>) o).getValue()).toArray();
			}
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(final Object o) {
		if (o instanceof Entry<?, ?>) {
			return true;
		}
		return getElements(o).length > 0;
	}

}
