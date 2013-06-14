package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.statespace.Trace;
import de.prob.statespace.OpInfo;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
class OperationsContentProvider implements IStructuredContentProvider {
	
	Map<String,Object> allOperations = new HashMap<String,Object>();

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(final Object inputElement) {
		List<Object> ops= new ArrayList<Object>();
		Map<String,Object> enabledOps = new HashMap<String, Object>();
		
		if( inputElement instanceof Trace) {
			Trace trace = (Trace) inputElement;
			Set<OpInfo> nextTransitions = trace.getNextTransitions();
			for (OpInfo opInfo : nextTransitions) {
				if(enabledOps.containsKey(opInfo.name)) {
					List<OpInfo> opList = (ArrayList<OpInfo>) enabledOps.get(opInfo.name);
					opList.add(opInfo);
				} else {
					List<OpInfo> opList = new ArrayList<OpInfo>();
					opList.add(opInfo);
					enabledOps.put(opInfo.name,opList);
				}
			}
			ops.addAll(enabledOps.values());
		}
		
		//add the operations that are not enabled
		for (String name : allOperations.keySet()) {
			if(!enabledOps.containsKey(name) && !name.equals("INITIALISATION"))
				ops.add(allOperations.get(name));
		}
		
		return ops.toArray();
	}
	
	/*
	 * The operations for the given model are saved as a map of the operation name
	 * to the actual operation object itself. In the case of an EventB Model, this
	 * object will be of type org.eventb.emf.core.machine.Event
	 */
	public void setAllOperations(Map<String, Object> allOperations) {
		this.allOperations = allOperations;
	}
}