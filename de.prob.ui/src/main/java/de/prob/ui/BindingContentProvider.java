package de.prob.ui;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import groovy.lang.Binding;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.webconsole.GroovyExecution;

public class BindingContentProvider implements IStructuredContentProvider {

	private GroovyExecution executor;

	
	
	public BindingContentProvider() {
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
		Binding bindings = executor.getBindings();
		ArrayList<BindingTableEntry> res = new ArrayList<BindingTableEntry>();
		Set entrySet = bindings.getVariables().entrySet();
		for (Object object : entrySet) {
			Entry e = (Entry) object;
			String key = (String) e.getKey();
			Object value =  e.getValue();
			if (!(key.startsWith("__") || key.startsWith("this$"))) 
				res.add(new BindingTableEntry(key, value.getClass().getSimpleName(), value.toString()));
		}
		return res.toArray(new Object[res.size()]);
	}

}
