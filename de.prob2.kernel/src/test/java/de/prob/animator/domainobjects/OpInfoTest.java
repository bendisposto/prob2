package de.prob.animator.domainobjects;

import org.junit.Test;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;

public class OpInfoTest {

	@Test
	public void testConstructor() {
		IntegerPrologTerm one = new IntegerPrologTerm(1);
		IntegerPrologTerm two = new IntegerPrologTerm(2);
		IntegerPrologTerm three = new IntegerPrologTerm(3);

		@SuppressWarnings("unused")
		CompoundPrologTerm cpt = new CompoundPrologTerm("op", one, two, three);

		// FIXME: Fix failing test. Need to mock StateSpace object
		/*
		 * OpInfo opInfo = OpInfo.createOpInfoFromCompoundPrologTerm(null, cpt);
		 * assertEquals("1", opInfo.getId()); assertEquals("2",
		 * opInfo.getSrcId().getId()); assertEquals("3",
		 * opInfo.getDestId().getId());
		 */
	}

}