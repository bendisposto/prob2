package de.prob.worksheet.api.evalStore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.prob.statespace.StateSpace;
import de.prob.worksheet.ServletContextListener;

public class EvalStoreContextTest {

	@Test
	public void testEvalStoreContext() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetId() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetBindings() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetBinding() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDestroy() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testEqualsObject() {
		StateSpace spaceA = ServletContextListener.INJECTOR
				.getInstance(StateSpace.class);
		StateSpace spaceB = ServletContextListener.INJECTOR
				.getInstance(StateSpace.class);
		EvalStoreContext context1 = new EvalStoreContext("1", 1l, spaceA);

		EvalStoreContext context2 = new EvalStoreContext("2", 1l, spaceA);
		EvalStoreContext context3 = new EvalStoreContext("1", 2l, spaceA);
		EvalStoreContext context4 = new EvalStoreContext("1", 1l, spaceB);
		EvalStoreContext context5 = new EvalStoreContext("2", 1l, spaceB);
		EvalStoreContext context6 = new EvalStoreContext("1", 2l, spaceB);
		EvalStoreContext context7 = new EvalStoreContext("2", 2l, spaceB);
		EvalStoreContext context8 = new EvalStoreContext("1", 1l, spaceA);

		assertTrue(context1.equals(context8));

		assertFalse(context1.equals(context2));
		assertFalse(context1.equals(context3));
		assertFalse(context1.equals(context4));
		assertFalse(context1.equals(context5));
		assertFalse(context1.equals(context6));
		assertFalse(context1.equals(context7));
	}

	@Test
	public void testSetId() {
		fail("Not yet implemented"); // TODO
	}

}
