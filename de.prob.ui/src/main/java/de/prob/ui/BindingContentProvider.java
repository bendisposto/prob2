package de.prob.ui;

import groovy.lang.Binding;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ServletContextListener;

public class BindingContentProvider implements IStructuredContentProvider {

	private GroovyExecution executor;
	private String filter;

	public BindingContentProvider(String filter) {
		this.filter = filter;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.executor = (GroovyExecution) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (executor == null) {
			executor = ServletContextListener.INJECTOR
					.getInstance(GroovyExecution.class);
		}
		Binding bindings = executor.getBindings();
		ArrayList<BindingTableEntry> res = new ArrayList<BindingTableEntry>();
		Set entrySet = bindings.getVariables().entrySet();
		for (Object object : entrySet) {
			Entry e = (Entry) object;
			String key = (String) e.getKey();
			Object value = e.getValue();
			if (key.startsWith(filter) && !(key.startsWith("__") || key.startsWith("this$")))
				res.add(new BindingTableEntry(key, value.getClass()
						.getSimpleName(), value.toString()));
		}
		return res.toArray(new Object[res.size()]);
	}

}
