/**
 * 
 */
package de.prob.worksheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.prob.worksheet.api.evalStore.EvalStoreContext;

/**
 * @author Rene
 *
 */
public class ContextHistoryTest {

	@Test
	public void ContextHistory1() {
		ContextHistory test=new ContextHistory(new EvalStoreContext("bId", 1l));
	}
	@Test(expected=IllegalArgumentException.class)
	public void ContextHistory2() {
		ContextHistory test1=new ContextHistory(null);
	}
	
	
	@Test
	public void getInitialContextForId(){
		EvalStoreContext context1=new EvalStoreContext("init", null);
		ContextHistory test=new ContextHistory(context1);
		assertTrue(test.getInitialContextForId("").equals(context1));
		
		EvalStoreContext context2=new EvalStoreContext("id1", 1l);
		test.add(context2);
		assertTrue(test.getInitialContextForId("id1").equals(context1));
		assertTrue(test.getInitialContextForId("newId").equals(context2));
		
		EvalStoreContext context3=new EvalStoreContext("id2", 1l);
		test.add(context3);
		assertTrue(test.getInitialContextForId("id2").equals(context2));
		
	}
	
	@Test
	public void setContextsForId(){
		//TODO add more test cases (pre = new && post = new && post = null && pre not found)
		ContextHistory bContextHistory=new ContextHistory(new EvalStoreContext("init", null));
		bContextHistory.add(new EvalStoreContext("id1", 1l));
		bContextHistory.add(new EvalStoreContext("id3", 2l));
		bContextHistory.add(new EvalStoreContext("id3", 4l));
			
		ContextHistory bContextHistory2=new ContextHistory(new EvalStoreContext("id1", 1l));
		bContextHistory2.add(new EvalStoreContext("", 1l));
		bContextHistory2.add(new EvalStoreContext("", 2l));
		bContextHistory2.add(new EvalStoreContext("", 2l));
		bContextHistory2.add(new EvalStoreContext("", 3l));
	
		bContextHistory.setContextsForId("id2", bContextHistory2);

		ArrayList<IContext> map=new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null));
		map.add(new EvalStoreContext("id1", 1l));
		map.add(new EvalStoreContext("id2", 2l));
		map.add(new EvalStoreContext("id2", 3l));
		map.add(new EvalStoreContext("id3", 4l));		
		
		assertTrue(map.equals(bContextHistory.getHistory()));
	}



	@Test
	public void size() {
		ContextHistory bContextHistory=new ContextHistory(new EvalStoreContext("init", null));
		assertEquals(bContextHistory.size(), 1);
		
		bContextHistory.add(new EvalStoreContext("id1", 1l));
		assertEquals(bContextHistory.size(), 2);
		
		bContextHistory.add(new EvalStoreContext("id1", 2l));
		assertEquals(bContextHistory.size(), 3);
		
	}




	@Test
	public void last() {
		ContextHistory bContextHistory=new ContextHistory(new EvalStoreContext("init", null));
		bContextHistory.add(new EvalStoreContext("id1", 1l));
	
		assertTrue(new EvalStoreContext("id1",1l).equals(bContextHistory.last()));
		
		bContextHistory.add(new EvalStoreContext("id1", 2l));
		assertTrue(new EvalStoreContext("id1",2l).equals(bContextHistory.last()));
		
		
	}


	@Test
	public void add1() {

		ContextHistory bContextHistory=new ContextHistory(new EvalStoreContext("init", null));
		bContextHistory.add(new EvalStoreContext("id1", 1l));
		bContextHistory.add(new EvalStoreContext("id1", 2l));
		bContextHistory.add(new EvalStoreContext("id1", 3l));
			
		ContextHistory bContextHistory2=new ContextHistory(new EvalStoreContext("init", null));
		bContextHistory2.add(new EvalStoreContext("id1", 1l));
		bContextHistory2.add(new EvalStoreContext("id1", 2l));
		bContextHistory2.add(new EvalStoreContext("id1", 2l));
		bContextHistory2.add(new EvalStoreContext("id1", 3l));
		
		ArrayList<IContext> map=new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null));
		map.add(new EvalStoreContext("id1", 1l));
		map.add(new EvalStoreContext("id1", 2l));
		map.add(new EvalStoreContext("id1", 3l));
	
		assertTrue(bContextHistory.getHistory().equals(map));
		
	}
	
	

	@Test
	public void add2() {
		ContextHistory bContextHistory2=new ContextHistory(new EvalStoreContext("init", null));
		bContextHistory2.add(new EvalStoreContext("id1", 1l));
		bContextHistory2.add(new EvalStoreContext("id1", 2l));
		bContextHistory2.add(new EvalStoreContext("id3", 4l));
		bContextHistory2.add(new EvalStoreContext("id3", 5l));
		
		bContextHistory2.add(3,new EvalStoreContext("id2", 3l));
		bContextHistory2.add(3,new EvalStoreContext("id2", 3l));
		
		ArrayList<IContext> map=new ArrayList<IContext>();
		map.add(new EvalStoreContext("init", null));
		map.add(new EvalStoreContext("id1", 1l));
		map.add(new EvalStoreContext("id1", 2l));
		map.add(new EvalStoreContext("id2", 3l));
		map.add(new EvalStoreContext("id3", 4l));
		map.add(new EvalStoreContext("id3", 5l));
	
		assertTrue(bContextHistory2.getHistory().equals(map));
		
	}

	@Test
	public void toString1() {
		//TODO add test
		
	}


}
