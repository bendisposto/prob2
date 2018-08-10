package de.prob.cli;

import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProBInstanceImplTest {
	private ProBInstance cli;

	@Test
	public void testCliShutdown() {
		final Process process = mock(Process.class);
		final BufferedReader reader = mock(BufferedReader.class);
		Long userInterruptRef = 1234L;
		final ProBConnection connection = mock(ProBConnection.class);
		final OsSpecificInfo osinfo = mock(OsSpecificInfo.class);
		cli = new ProBInstance(process, reader, userInterruptRef,
			connection, "", osinfo, new AtomicInteger());
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
