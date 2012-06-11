package de.prob.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;

import org.junit.After;
import org.junit.Test;

import test.AbstractUnitTest;

public class ProBInstanceImplTest extends AbstractUnitTest {

	private Process process;
	private BufferedReader reader;
	private ProBInstance cli;
	private ProBConnection connection;

	@Test
	public void testCliShutdown() throws Exception {
		process = mock(Process.class);
		reader = mock(BufferedReader.class);
		connection = mock(ProBConnection.class);
		try {
			cli = new ProBInstance(process, reader, 12345L, connection, "", null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertFalse(cli.isShuttingDown());
		cli.shutdown();
		assertTrue(cli.isShuttingDown());
		verify(connection).disconnect();
	}

	@After
	public void killCli() {
		cli.shutdown();
		cli = null;
	}

}
