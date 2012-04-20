package de.prob.animator.domainobjects;

import static org.junit.Assert.*;

import org.junit.Test;

import de.prob.ProBException;
import de.prob.parser.ResultParserException;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.VariablePrologTerm;

public class OpInfoTest {

	@Test
	public void testConstructor1() {
		OpInfo opInfo = new OpInfo("3", "blah", "4", "5", null);
		assertEquals("3", opInfo.id);
		assertEquals("blah", opInfo.name);
		assertEquals("4", opInfo.src);
		assertEquals("5", opInfo.dest);
		assertTrue(opInfo.params.isEmpty());
	}

	@Test
	public void testConstructor2() throws ProBException {
		IntegerPrologTerm idPT = new IntegerPrologTerm(1);
		CompoundPrologTerm namePT = new CompoundPrologTerm("blah");
		IntegerPrologTerm srcPT = new IntegerPrologTerm(2);
		IntegerPrologTerm destPT = new IntegerPrologTerm(3);
		ListPrologTerm paramsPT = new ListPrologTerm(new CompoundPrologTerm(
				"=D"));
		CompoundPrologTerm opTerm = new CompoundPrologTerm("cpt", idPT, namePT,
				srcPT, destPT, null, paramsPT);
		assertEquals(idPT, opTerm.getArgument(1));
		assertEquals(namePT, opTerm.getArgument(2));
		assertEquals(srcPT, opTerm.getArgument(3));
		assertEquals(destPT, opTerm.getArgument(4));
		assertEquals(paramsPT, opTerm.getArgument(6));
		assertEquals(6, opTerm.getArity());

		OpInfo info = new OpInfo(opTerm);
		assertEquals("1", info.id);
		assertEquals("blah", info.name);
		assertEquals("2", info.src);
		assertEquals("3", info.dest);
		assertTrue(info.params.contains("=D"));
	}

	@Test
	public void testGetIdFromPrologTerm() throws ResultParserException {
		IntegerPrologTerm idPT = new IntegerPrologTerm(1);
		VariablePrologTerm vPT = new VariablePrologTerm("root");
		assertEquals("1", OpInfo.getIdFromPrologTerm(idPT));
		assertEquals("root", OpInfo.getIdFromPrologTerm(vPT));
	}
}