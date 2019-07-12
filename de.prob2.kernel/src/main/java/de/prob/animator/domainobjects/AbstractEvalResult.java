package de.prob.animator.domainobjects;

import groovy.lang.GroovyObjectSupport;

public class AbstractEvalResult extends GroovyObjectSupport {

	private static long idcounter = 0;

	public static synchronized long nextID() {
		return idcounter++;
	}

	public final long id;

	public long getId() {
		return id;
	}

	public AbstractEvalResult() {
		this.id = nextID();
	}

}
