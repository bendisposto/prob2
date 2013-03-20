/**
 * 
 */
package de.prob.worksheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.IContext;
import de.prob.worksheet.api.evalStore.EvalStoreContext;

/**
 * @author Rene
 * 
 */
public class ContextHistoryTest {

	@Test
	public void ContextHistory1() {
		ContextHistory test = new ContextHistory(new EvalStoreContext("bId",
				1l, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void ContextHistory2() {
		ContextHistory test1 = new ContextHistory(null);
	}

	@Test
	public void getInitialContextForId() {
		EvalStoreContext context1 = new EvalStoreContext("init", null, null);
		EvalStoreContext context2 = new EvalStoreContext("id1", 1l, null);
		EvalStoreContext context3 = new EvalStoreContext("id2", 1l, null);
		EvalStoreContext context4 = new EvalStoreContext("id3", 2l, null);
		EvalStoreContext context5 = new EvalStoreContext("id3", 3l, null);
		EvalStoreContext context6 = new EvalStoreContext("id4", 4l, null);

		ContextHistory test = new ContextHistory(context1);
		test.add(context2);
		test.add(context3);
		test.add(context4);
		test.add(context5);
		test.add(context6);

		assertTrue(test.getInitialContextForId("").equals(context6));
		assertTrue(test.getInitialContextForId("newId").equals(context6));

		assertTrue(test.getInitialContextForId("id1").equals(context1));
		assertTrue(test.getInitialContextForId("id2").equals(context2));
		assertTrue(test.getInitialContextForId("id3").equals(context3));
		assertTrue(test.getInitialContextForId("id4").equals(context5));

	}

	@Test
	public void setContextsForId() {
		// TODO add more test cases (pre = new && post = new && post = null &&
		// pre not found)
		ContextHistory bContextHistory = new ContextHistory(
				new EvalStoreContext("init", null, null));
		bContextHistory.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory.add(new EvalStoreContext("id3", 2l, null));
		bContextHistory.add(new EvalStoreContext("id3", 3l, null));

		ContextHistory bContextHistory2 = new ContextHistory(
				new EvalStoreContext("id1", 1l, null));
		bContextHistory2.add(new EvalStoreContext("", 1l, null));
		bContextHistory2.add(new EvalStoreContext("", 2l, null));
		bContextHistory2.add(new EvalStoreContext("", 2l, null));
		bContextHistory2.add(new EvalStoreContext("", 3l, null));

		bContextHistory.setContexts("id2", bContextHistory2);

		ArrayList<IContext> map = new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null, null));
		map.add(new EvalStoreContext("id1", 1l, null));
		map.add(new EvalStoreContext("id2", 1l, null));
		map.add(new EvalStoreContext("id2", 2l, null));
		map.add(new EvalStoreContext("id2", 3l, null));
		map.add(new EvalStoreContext("id3", 2l, null));
		map.add(new EvalStoreContext("id3", 3l, null));
		assertTrue(map.equals(bContextHistory.getHistory()));
	}

	@Test
	public void size() {
		ContextHistory bContextHistory = new ContextHistory(
				new EvalStoreContext("init", null, null));
		assertEquals(bContextHistory.size(), 1);

		bContextHistory.add(new EvalStoreContext("id1", 1l, null));
		assertEquals(bContextHistory.size(), 2);

		bContextHistory.add(new EvalStoreContext("id1", 2l, null));
		assertEquals(bContextHistory.size(), 3);

	}

	@Test
	public void last() {
		ContextHistory bContextHistory = new ContextHistory(
				new EvalStoreContext("init", null, null));
		bContextHistory.add(new EvalStoreContext("id1", 1l, null));

		assertTrue(new EvalStoreContext("id1", 1l, null).equals(bContextHistory
				.last()));

		bContextHistory.add(new EvalStoreContext("id1", 2l, null));
		assertTrue(new EvalStoreContext("id1", 2l, null).equals(bContextHistory
				.last()));

	}

	@Test
	public void add1() {

		ContextHistory bContextHistory = new ContextHistory(
				new EvalStoreContext("init", null, null));
		bContextHistory.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory.add(new EvalStoreContext("id1", 2l, null));
		bContextHistory.add(new EvalStoreContext("id1", 3l, null));

		ContextHistory bContextHistory2 = new ContextHistory(
				new EvalStoreContext("init", null, null));
		bContextHistory2.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory2.add(new EvalStoreContext("id1", 2l, null));
		bContextHistory2.add(new EvalStoreContext("id1", 2l, null));
		bContextHistory2.add(new EvalStoreContext("id1", 3l, null));

		ArrayList<IContext> map = new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null, null));
		map.add(new EvalStoreContext("id1", 1l, null));
		map.add(new EvalStoreContext("id1", 2l, null));
		map.add(new EvalStoreContext("id1", 3l, null));

		assertTrue(bContextHistory.getHistory().equals(map));

	}

	@Test
	public void add2() {
		ContextHistory bContextHistory2 = new ContextHistory(
				new EvalStoreContext("init", null, null));
		bContextHistory2.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory2.add(new EvalStoreContext("id1", 2l, null));
		bContextHistory2.add(new EvalStoreContext("id3", 4l, null));
		bContextHistory2.add(new EvalStoreContext("id3", 5l, null));

		bContextHistory2.add(3, new EvalStoreContext("id2", 3l, null));
		bContextHistory2.add(3, new EvalStoreContext("id2", 3l, null));

		ArrayList<IContext> map = new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null, null));
		map.add(new EvalStoreContext("id1", 1l, null));
		map.add(new EvalStoreContext("id1", 2l, null));
		map.add(new EvalStoreContext("id2", 3l, null));
		map.add(new EvalStoreContext("id3", 4l, null));
		map.add(new EvalStoreContext("id3", 5l, null));

		assertTrue(bContextHistory2.getHistory().equals(map));

	}

	@Test
	public void removeAfterInitial() {
		ContextHistory bContextHistory = new ContextHistory(
				new EvalStoreContext("init", null, null));
		bContextHistory.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory.add(new EvalStoreContext("id2", 2l, null));
		bContextHistory.add(new EvalStoreContext("id2", 3l, null));
		bContextHistory.add(new EvalStoreContext("id2", 4l, null));
		bContextHistory.add(new EvalStoreContext("id2", 5l, null));
		bContextHistory.add(new EvalStoreContext("id3", 6l, null));
		bContextHistory.add(new EvalStoreContext("id3", 7l, null));

		bContextHistory.reset("id2");
		ArrayList<IContext> map = new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null, null));
		map.add(new EvalStoreContext("id1", 1l, null));

		assertTrue(map.equals(bContextHistory.getHistory()));

		bContextHistory = new ContextHistory(new EvalStoreContext("init", null,
				null));
		bContextHistory.add(new EvalStoreContext("id1", 1l, null));
		bContextHistory.add(new EvalStoreContext("id2", 2l, null));
		bContextHistory.add(new EvalStoreContext("id2", 3l, null));
		bContextHistory.add(new EvalStoreContext("id2", 4l, null));
		bContextHistory.add(new EvalStoreContext("id2", 5l, null));
		bContextHistory.add(new EvalStoreContext("id3", 6l, null));
		bContextHistory.add(new EvalStoreContext("id3", 7l, null));

		bContextHistory.reset("id3");
		map = new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null, null));
		map.add(new EvalStoreContext("id1", 1l, null));
		map.add(new EvalStoreContext("id2", 2l, null));
		map.add(new EvalStoreContext("id2", 3l, null));
		map.add(new EvalStoreContext("id2", 4l, null));
		map.add(new EvalStoreContext("id2", 5l, null));

		assertTrue(map.equals(bContextHistory.getHistory()));

	}

	@Test
	public void toString1() {
		// TODO add test

	}

	@Test
	public void insertEmptyContext() {
		ContextHistory test = new ContextHistory(new EvalStoreContext("root",
				null, null));
		test.insertEmptyContext("root", "block-1");
		assertEquals(2, test.size());
	}
}
