package de.prob.model.classicalb;

import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.common.base.Joiner;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.ModelElementList;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.List;


public class Operation extends BEvent {
	public Operation(final String name, final List<String> parameters, final List<String> output) {
		this(name, parameters, output, PersistentHashMap.emptyMap());
	}

	private Operation(final String name, final List<String> parameters, final List<String> output, PersistentHashMap children) {
		super(name, children);
		this.parameters = parameters;
		this.output = output;
	}

	public <T extends AbstractElement> Operation addTo(T element) {
		ModelElementList<T> kids = (ModelElementList) getChildren().get(element.getClass());
		return new Operation(getName(), parameters, output, ((AbstractElement) getChildren()).assoc(element.getClass(), kids.addElement(element)));
	}

	public Operation set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children = (PersistentHashMap) getChildren();
		return new Operation(getName(), parameters, output, (PersistentHashMap) children.assoc(clazz, elements));
	}

	public ModelElementList<ClassicalBGuard> addGuards() {
		return getChildrenOfType(ClassicalBGuard.class);
	}

	public ModelElementList<ClassicalBAction> addActions() {
		return getChildrenOfType(ClassicalBAction.class);
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

	public final List<String> getParameters() {
		return parameters;
	}

	public final List<String> getOutput() {
		return output;
	}

	private final List<String> parameters;
	private final List<String> output;
}
