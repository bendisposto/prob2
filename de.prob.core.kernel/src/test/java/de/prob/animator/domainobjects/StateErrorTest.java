package de.prob.animator.domainobjects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.prob.prolog.term.CompoundPrologTerm;

public class StateErrorTest {

	@Test
	public void test() {
		StateError a = new StateError("I", " am", " Awesome.");
		assertEquals("I", a.getEvent());
		assertEquals(" am", a.getShortDescription());
		assertEquals(" Awesome.", a.getLongDescription());
		StateError b = new StateError(new CompoundPrologTerm("cpt",
				new CompoundPrologTerm("My"), new CompoundPrologTerm(" Life"),
				new CompoundPrologTerm(" ROCKS!!!")));
		assertEquals("My", b.getEvent());
		assertEquals(" Life", b.getShortDescription());
		assertEquals(" ROCKS!!!", b.getLongDescription());

	}

}
