/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bmotionstudio.core.ButtonGroupHelper;
import de.bmotionstudio.core.model.VisualizationView;

public class Visualization extends BControl {

	private transient List<String> allBControlIDs;
	
	private transient VisualizationView visualizationView;
	
	private Map<String,BConnection> connections;

//	public List<String> getAllBControlIDs() {
//		if (allBControlIDs == null)
//			allBControlIDs = getAllBControlNames();
//		return allBControlIDs;
//	}

	public Visualization() {
		ButtonGroupHelper.reset();
		this.connections = new HashMap<String, BConnection>();
	}

	@Override
	protected Object readResolve() {
		super.readResolve();
		for (BConnection con : getConnections().values())
			con.setParent(this);
		ButtonGroupHelper.reset();
		init();
		return this;
	}

//	public List<String> getAllBControlNames() {
//		return getAllBControlNames(getChildren());
//	}

//	private List<String> getAllBControlNames(List<BControl> children) {
//		List<String> list = new ArrayList<String>();
//		for (BControl control : children) {
//			list.add(control.getID());
//			// Check children
//			List<BControl> subchildren = control.getChildren();
//			if (children.size() > 0)
//				list.addAll(getAllBControlNames(subchildren));
//			// Check connections
//			List<BControl> connections = new ArrayList<BControl>();
//			connections.addAll(control.getSourceConnections());
//			connections.addAll(control.getTargetConnections());
//			if (connections.size() > 0)
//				list.addAll(getAllBControlNames(connections));
//		}
//		return list;
//	}

//	public String getMaxIDString(String type) {
//		String newID = getMaxID(type, 0, getAllBControlIDs());
//		getAllBControlIDs().add(newID);
//		return newID;
//	}

	// old method
	private String getMaxID(String type, int count, List<String> allIDs) {
		String newID = "control_" + count;
		if (allIDs.contains(newID)) {
			return getMaxID(type, (count + 1), allIDs);
		} else {
			return newID;
		}
	}

//	public boolean checkIfIdExists(String ID) {
//		return getAllBControlNames().contains(ID);
//	}

	public BControl getBControl(String ID) {
		return getBControl(ID, getChildren());
	}

	private BControl getBControl(String ID, List<BControl> children) {
		for (BControl control : children) {
			if (control.getID().equals(ID)) {
				return control;
			}
			for (BConnection c : getConnections().values()) {
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
	public Visualization getVisualization() {
		return this;
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

	public Map<String,BConnection> getConnections() {
		if(connections == null)
			connections = new HashMap<String, BConnection>();
		return connections;
	}

	public void setConnections(Map<String,BConnection> connections) {
		this.connections = connections;
	}
	
	public BConnection getConnection(String id) {
		return this.connections.get(id);
	}

	public void removeConnection(BConnection connection) {
		this.connections.remove(connection.getID());
	}
	
	public void removeConnection(String id) {
		this.connections.remove(id);
	}
	
}
