package de.prob.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.prob.web.data.Message;

public class ResponsesTest {

	@Test
	public void testImmediateGet() {
		Responses r = new Responses();
		Message m = new Message(2, "lala");
		assertEquals(0, r.size());
		assertTrue(r.isEmpty());
		r.add(m);
		assertEquals(1, r.size());
		assertFalse(r.isEmpty());
		Message m2 = null;
		try {
			m2 = r.get(0);
		} catch (ReloadRequiredException e) {
			fail("Value was not there anymore");
		}
		assertEquals(m, m2);
	}

	@Test
	public void testTimeOut() throws InterruptedException {
		Responses r = new Responses(1, TimeUnit.NANOSECONDS);
		Message m = new Message(2, "lala");
		assertEquals(0, r.size());
		assertTrue(r.isEmpty());
		r.add(m);
		Thread.sleep(10);
		assertEquals(1, r.size());
		assertFalse(r.isEmpty());
		try {
			r.get(0);
			fail("Value has not been discarded.");
		} catch (ReloadRequiredException e) {
		}
	}
}
