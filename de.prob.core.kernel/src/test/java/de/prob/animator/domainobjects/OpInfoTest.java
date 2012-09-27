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
		OpInfo opInfo = new OpInfo("3", "blah", "4", "5", null, "");
		assertEquals("3", opInfo.id);
		assertEquals("blah", opInfo.name);
		assertEquals("4", opInfo.src);
		assertEquals("5", opInfo.dest);
		assertTrue(opInfo.params.isEmpty());
	}

	@Test
	public void testConstructor2() {
		IntegerPrologTerm one = new IntegerPrologTerm(1);
		CompoundPrologTerm two = new CompoundPrologTerm("NAME");
		VariablePrologTerm three = new VariablePrologTerm("src");
		VariablePrologTerm four = new VariablePrologTerm("dest");
		VariablePrologTerm five = new VariablePrologTerm("something");
		ListPrologTerm six = new ListPrologTerm(
				new VariablePrologTerm("param"),
				new VariablePrologTerm("param"));
		VariablePrologTerm seven = new VariablePrologTerm("something else");
		VariablePrologTerm eight = new VariablePrologTerm("target state");

		CompoundPrologTerm cpt = new CompoundPrologTerm("blah", one, two,
				three, four, five, six, seven, eight);

		OpInfo opInfo = new OpInfo(cpt);
		assertEquals("1", opInfo.id);
		assertEquals("NAME", opInfo.name);
		assertEquals("src", opInfo.src);
		assertEquals("dest", opInfo.dest);

		for (String param : opInfo.params) {
			assertEquals(param, "param");
		}

		assertEquals("target state", opInfo.targetState);
	}

	@Test
	public void testGetIdFromPrologTerm() {
		IntegerPrologTerm idPT = new IntegerPrologTerm(1);
		VariablePrologTerm vPT = new VariablePrologTerm("root");
		assertEquals("1", OpInfo.getIdFromPrologTerm(idPT));
		assertEquals("root", OpInfo.getIdFromPrologTerm(vPT));
	}
}