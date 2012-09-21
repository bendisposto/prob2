package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.History;

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

	public Object[] getElements(final Object inputElement) {
		List<Object> ops= new ArrayList<Object>();
		Map<String,Object> enabledOps = new HashMap<String, Object>();
		
		if( inputElement instanceof History) {
			History history = (History) inputElement;
			Set<OpInfo> nextTransitions = history.getNextTransitions();
			ops.addAll(nextTransitions);
			for (OpInfo opInfo : nextTransitions) {
				enabledOps.put(opInfo.name, opInfo);
			}
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