package de.prob.animator.domainobjects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.statespace.OpInfo;

public class OpInfoTest {

	@Test
	public void testConstructor() {
		IntegerPrologTerm one = new IntegerPrologTerm(1);
		IntegerPrologTerm two = new IntegerPrologTerm(2);
		IntegerPrologTerm three = new IntegerPrologTerm(3);

		CompoundPrologTerm cpt = new CompoundPrologTerm("op", one, two, three);

		OpInfo opInfo = OpInfo.createOpInfoFromCompoundPrologTerm(null, cpt);
		assertEquals("1", opInfo.getId());
		assertEquals("2", opInfo.getSrcId().getId());
		assertEquals("3", opInfo.getDestId().getId());

	}

}