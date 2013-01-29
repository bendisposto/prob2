package de.prob.worksheet.api.evalStore;

import static org.junit.Assert.*;

import org.junit.Test;

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
		EvalStoreContext context1=new EvalStoreContext("1", 1l);
		EvalStoreContext context2=new EvalStoreContext("1", 1l);
		EvalStoreContext context3=new EvalStoreContext("1", null);
		EvalStoreContext context4=new EvalStoreContext("1", null);
		EvalStoreContext context5=new EvalStoreContext("2", 2l);
		EvalStoreContext context6=new EvalStoreContext("2", null);
		EvalStoreContext context7=new EvalStoreContext("2", 1l);
	
		assertTrue(context1.equals(context2));
		assertTrue(context3.equals(context4));
		assertTrue(context1.equals(context7));
		assertTrue(context3.equals(context6));
		
		assertFalse(context1.equals(context3));
		assertFalse(context1.equals(context5));
		assertFalse(context1.equals(context6));
		assertFalse(context2.equals(context5));
		
	}

	@Test
	public void testSetId() {
		fail("Not yet implemented"); // TODO
	}

}
