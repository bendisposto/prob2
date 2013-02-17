package de.prob.worksheet;

import java.util.HashMap;

public class RootContext implements IContext {

	@Override
	public String getId() {
		return "init";
	}

	@Override
	public void setId(String contextId) {
		return;
	}

	@Override
	public HashMap<String, Object> getBindings() {
		return null;
	}

	@Override
	public void setBindings(HashMap<String, Object> bindings) {
		return;
	}

	@Override
	public Object getBinding(String name) {
		return null;
	}

	@Override
	public void setBinding(String name, Object binding) {
		return;
	}

	@Override
	public boolean equalsBindings(Object obj) {
		return ((IContext) obj).getBindings() == null;
	}

	@Override
	public void destroy() {
		return;
	}

	@Override
	public IContext getDuplicate() {
		return new RootContext();
	}

	@Override
	public boolean equals(Object obj) {
		return equalsBindings(obj) && ((IContext) obj).getId().equals("init");
	}
}
