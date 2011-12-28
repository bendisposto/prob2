package de.prob.animator.command;

public class OpInfo {
	public final String id;
	public final String name;
	public final String src;
	public final String dest;
	public final String params;

	public OpInfo(final String id, final String name, final String src,
			final String dest, final String params) {
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.params = params;
	}
}
