package de.prob.model.representation;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class OperationTest {

	@Test
	public void test() {
		Operation op = new Operation("foo", Arrays.asList(new String[] { "bar",
				"4" }), null);

		Assert.assertEquals("foo(bar,4)", op.toString());

	}

}
