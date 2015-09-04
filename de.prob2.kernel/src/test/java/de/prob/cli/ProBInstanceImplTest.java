package de.prob.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;

import test.AbstractUnitTest;

public class ProBInstanceImplTest extends AbstractUnitTest {

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
