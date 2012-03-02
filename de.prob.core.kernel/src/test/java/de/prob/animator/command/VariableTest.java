package de.prob.animator.command;

import org.junit.Assert;
import org.junit.Test;

import de.prob.prolog.term.CompoundPrologTerm;

public class VariableTest {

	@Test
	public void test() {

		CompoundPrologTerm term = new CompoundPrologTerm("binding",
				new CompoundPrologTerm("foo"), new CompoundPrologTerm(
						"internal", new CompoundPrologTerm("representation")),
				new CompoundPrologTerm("bar"));

		Variable variable = new Variable(term);
		Assert.assertEquals("foo", variable.name);
		Assert.assertEquals("bar", variable.value);

	}

	@Test
	public void testToString() {

		CompoundPrologTerm term = new CompoundPrologTerm("binding",
				new CompoundPrologTerm("foo"), new CompoundPrologTerm(
						"internal", new CompoundPrologTerm("representation")),
				new CompoundPrologTerm("bar"));

		Variable variable = new Variable(term);
		Assert.assertEquals("foo->bar", variable.toString());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalVariable() {

		CompoundPrologTerm term = new CompoundPrologTerm("dingdong",
				new CompoundPrologTerm("foo"), new CompoundPrologTerm(
						"internal", new CompoundPrologTerm("representation")),
				new CompoundPrologTerm("bar"));

		new Variable(term);
	}
}
