package de.bmotionstudio.core.model.attribute;

import java.util.ArrayList;
import java.util.List;

public class ConnectionList {

	private List<String> connections = new ArrayList<String>();

	public List<String> getConnections() {
		return connections;
	}

	public void setConnections(List<String> connections) {
		this.connections = connections;
	}
	
	public void add(String con) {
		this.connections.add(con);
	}
	
	public void remove(String con) {
		this.connections.remove(con);
	}
		
}
