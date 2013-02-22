/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import java.util.ArrayList;
import java.util.List;

import de.bmotionstudio.core.ButtonGroupHelper;
import de.bmotionstudio.core.model.VisualizationView;

public class Visualization extends BControl {

	private transient List<String> allBControlIDs;
	
	private transient VisualizationView visualizationView;

	public List<String> getAllBControlIDs() {
		if (allBControlIDs == null)
			allBControlIDs = getAllBControlNames();
		return allBControlIDs;
	}

	private transient Boolean isRunning;

	public Visualization() {
		ButtonGroupHelper.reset();
	}

	@Override
	protected Object readResolve() {
		super.readResolve();
		this.isRunning = false;
		ButtonGroupHelper.reset();
		init();
		return this;
	}

	public void setIsRunning(Boolean bol) {
		this.isRunning = bol;
	}

	public Boolean isRunning() {
		return isRunning;
	}

	public List<String> getAllBControlNames() {
		return getAllBControlNames(getChildren());
	}

	private List<String> getAllBControlNames(List<BControl> children) {
		List<String> list = new ArrayList<String>();
		for (BControl control : children) {
			list.add(control.getID());
			// Check children
			List<BControl> subchildren = control.getChildren();
			if (children.size() > 0)
				list.addAll(getAllBControlNames(subchildren));
			// Check connections
			List<BControl> connections = new ArrayList<BControl>();
			connections.addAll(control.getSourceConnections());
			connections.addAll(control.getTargetConnections());
			if (connections.size() > 0)
				list.addAll(getAllBControlNames(connections));
		}
		return list;
	}

	public String getMaxIDString(String type) {
		String newID = getMaxID(type, 0, getAllBControlIDs());
		getAllBControlIDs().add(newID);
		return newID;
	}

	// old method
	private String getMaxID(String type, int count, List<String> allIDs) {
		String newID = "control_" + count;
		if (allIDs.contains(newID)) {
			return getMaxID(type, (count + 1), allIDs);
		} else {
			return newID;
		}
	}

	public boolean checkIfIdExists(String ID) {
		return getAllBControlNames().contains(ID);
	}

	public BControl getBControl(String ID) {
		return getBControl(ID, getChildren());
	}

	private BControl getBControl(String ID, List<BControl> children) {
		for (BControl control : children) {
			if (control.getID().equals(ID)) {
				return control;
			}
			for (BConnection c : control.getSourceConnections()) {
				if (c.getID().equals(ID))
					return c;
			}
			for (BConnection c : control.getTargetConnections()) {
				if (c.getID().equals(ID))
					return c;
			}
			if (control.getChildren().size() > 0) {
				BControl childControl = getBControl(ID,
						control.getChildren());
				if (childControl != null)
					return childControl;
			}
		}
		return null;
	}

	@Override
	protected void initAttributes() {
	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

	public VisualizationView getVisualizationView() {
		return visualizationView;
	}

	public void setVisualizationView(VisualizationView visualizationView) {
		this.visualizationView = visualizationView;
	}

}
