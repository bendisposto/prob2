package de.prob.model.classicalb;

import com.github.krukow.clj_lang.PersistentHashMap
import com.google.common.base.Joiner

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Action
import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class Operation extends BEvent {

	def final List<String> parameters;
	def final List<String> output;

	public Operation(final String name, final List<String> parameters,
	final List<String> output) {
		this(name, parameters, output, PersistentHashMap.emptyMap())
	}

	private Operation(final String name, final List<String> parameters,
	final List<String> output, children) {
		super(name, children)
		this.parameters = parameters;
		this.output = output;
	}

	def <T extends AbstractElement> Operation addTo(T element) {
		def kids = children.get(T)
		new Operation(name, parameters, output, children.assoc(T, kids.addElement(element)))
	}

	def Operation set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		new Operation(name, parameters, output, children.assoc(clazz, elements))
	}

	def ModelElementList<ClassicalBGuard> addGuards() {
		getChildrenOfType(Guard.class)
	}

	def ModelElementList<ClassicalBAction> addActions() {
		getChildrenOfType(Action.class)
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (!output.isEmpty()) {
			for (String string : output) {
				sb.append(string + " <-- ");
			}
		}
		sb.append(getName());
		if (!parameters.isEmpty()) {
			sb.append("(");
			sb.append(Joiner.on(",").join(parameters.iterator()));
			sb.append(")");
		}
		return sb.toString();
	}
}
