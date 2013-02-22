package de.prob.scripting;

import java.util.Map;

import com.google.inject.Inject;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.StateSchema;
import de.prob.statespace.StateSpace;

public class CSPModel extends AbstractModel {

	private String content;

	@Inject
	public CSPModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	public void init(final String content) {
		this.content = content;
		statespace.setModel(this);
	}

	public String getContent() {
		return content;
	}

	@Override
	public AbstractElement getComponent(final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, AbstractElement> getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StateSchema getStateSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractElement getMainComponent() {
		// TODO Auto-generated method stub
		return null;
	}
}
