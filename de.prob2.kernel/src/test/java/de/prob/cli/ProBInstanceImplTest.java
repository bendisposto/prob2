package de.prob.cli;

import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProBInstanceImplTest {

	private Process process;
	private BufferedReader reader;
	private ProBInstance cli;
	private ProBConnection connection;
	private OsSpecificInfo osinfo;

	@Test
	public void testCliShutdown() throws Exception {
		process = mock(Process.class);
		reader = mock(BufferedReader.class);
		connection = mock(ProBConnection.class);
		osinfo = mock(OsSpecificInfo.class);
		Long userInterruptRef = 1234L;
		try {
			cli = new ProBInstance(process, reader, userInterruptRef,
					connection, "", osinfo, new AtomicInteger());
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
