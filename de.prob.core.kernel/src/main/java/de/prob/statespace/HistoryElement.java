package de.prob.statespace;

public class HistoryElement {
	private String src;
	private String dest;
	private String edge;
	
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
