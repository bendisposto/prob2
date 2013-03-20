package de.prob.worksheet.api.evalStore;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JNode {
	private static int counter = 0;
	private String name;
	private String id;
	private ArrayList<JNode> children;
	private JNodeData color;

	public JNode() {
		JNode.counter++;
		id = "node" + JNode.counter;
		children = new ArrayList<JNode>();
	}

	public JNode(String name) {
		JNode.counter++;
		id = "node" + JNode.counter;
		children = new ArrayList<JNode>();
		this.name = name;
	}

	public List<JNode> getChildren() {
		return children;
	}

	@JsonProperty
	public void setChildren(List<JNode> children) {
		this.children = new ArrayList(children);
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addChildren(JNode node) {
		if (children == null) {
			children = new ArrayList<JNode>();
		}
		children.add(node);
	}

	@JsonIgnore
	public String getColor() {
		return color.getColor();
	}

	public void setColor(String color) {
		JNodeData data = new JNodeData();
		data.setColor(color);
		this.color = data;
	}

	@JsonProperty(value = "data")
	public JNodeData getData() {
		return color;
	}

	public JNode find(String name) {
		if (this.name.equals(name))
			return this;
		if (children != null) {
			for (JNode node : children) {
				JNode ret = node.find(name);
				if (ret != null)
					return ret;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		String child = "";
		if (children != null)
			child = " children:{" + children + "}";
		return "Node:" + name + child;
	}

	@JsonProperty
	public String getId() {
		return id;
	}

	public void setId(String label) {
		id = label;
	}
}