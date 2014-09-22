package de.prob.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.slf4j.Logger;

import test.AbstractUnitTest;

public class ConsoleListenerTest extends AbstractUnitTest {

	@Test
	public void testMultiLineRead() throws Exception {
		ProBInstance proBInstance = mock(ProBInstance.class);
		BufferedReader reader = new BufferedReader(new StringReader("foo\nbar"));
		Logger logger = mock(Logger.class);
		ConsoleListener listener = new ConsoleListener(proBInstance, reader,
				logger);
		String line1 = listener.readAndLog();
		assertEquals("foo", line1);
		String line2 = listener.readAndLog();
		assertEquals("bar", line2);
		verify(logger).debug("foo");
		verify(logger).debug("bar");
	}

	@Test
	public void testMultiLineReadAndTerminate() throws Exception {
		ProBInstance proBInstance = mock(ProBInstance.class);
		BufferedReader reader = new BufferedReader(new StringReader(
				"foo\nbar\ngoo"));
		Logger logger = mock(Logger.class);
		ConsoleListener listener = new ConsoleListener(proBInstance, reader,
				logger);
		when(proBInstance.isShuttingDown()).thenReturn(false, false, true);
		listener.logLines();
		verify(logger).debug("foo");
		verify(logger).debug("bar");
		verifyNoMoreInteractions(logger);
	}

	@Test
	public void testSingleLineRead() throws Exception {
		ProBInstance proBInstance = mock(ProBInstance.class);
		BufferedReader reader = new BufferedReader(new StringReader("foo"));
		Logger logger = mock(Logger.class);
		ConsoleListener listener = new ConsoleListener(proBInstance, reader,
				logger);
		String line = listener.readAndLog();
		assertEquals("foo", line);
		verify(logger).debug("foo");
	}

	@Test
	public void testTerminate() throws Exception {
		ProBInstance proBInstance = mock(ProBInstance.class);
		BufferedReader reader = new BufferedReader(new StringReader("foo"));
		when(proBInstance.isShuttingDown()).thenReturn(true);
		Logger logger = mock(Logger.class);
		ConsoleListener listener = new ConsoleListener(proBInstance, reader,
				logger);
		listener.logLines();
		verify(logger, never()).debug("foo");
	}

	@Test
	public void testTerminateNull() throws Exception {
		ProBInstance proBInstance = mock(ProBInstance.class);
		BufferedReader reader = new BufferedReader(new StringReader(""));
		when(proBInstance.isShuttingDown()).thenReturn(true);
		Logger logger = mock(Logger.class);
		ConsoleListener listener = new ConsoleListener(proBInstance, reader,
				logger);
		String line = listener.readAndLog();
		assertNull(line);
		verifyNoMoreInteractions(logger);
	}
}
