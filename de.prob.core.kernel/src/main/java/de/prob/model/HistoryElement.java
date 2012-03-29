package de.prob.model;

public class HistoryElement {
	String src;
	String dest;
	String edge;
	
	HistoryElement(String src, String dest, String edge)
	{
		this.src = src;
		this.dest = dest;
		this.edge = edge;
	}
	
	public String getSrc()
	{
		return src;
	}
	
	public String getDest()
	{
		return dest;
	}
	
	public String getOp()
	{
		return edge;
	}
}
