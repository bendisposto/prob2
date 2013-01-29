package de.prob.worksheet;

import java.util.HashMap;

public interface IContext {
	public String getId();
	public HashMap<String, Object> getBindings();
	public Object getBinding(String name);
	public void destroy();
	@Override
	public String toString();
	public void setId(String id);
}
