package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.VariablePrologTerm;

public class OpInfoTest {

	@Test
	public void testConstructor1() {
		OpInfo opInfo = new OpInfo("3", "blah", "4", "5", null,"");
		assertEquals("3", opInfo.id);
		assertEquals("blah", opInfo.name);
		assertEquals("4", opInfo.src);
		assertEquals("5", opInfo.dest);
		assertTrue(opInfo.params.isEmpty());
	}

	
	@Test
	public void testGetIdFromPrologTerm() {
		IntegerPrologTerm idPT = new IntegerPrologTerm(1);
		VariablePrologTerm vPT = new VariablePrologTerm("root");
		assertEquals("1", OpInfo.getIdFromPrologTerm(idPT));
		assertEquals("root", OpInfo.getIdFromPrologTerm(vPT));
	}
}