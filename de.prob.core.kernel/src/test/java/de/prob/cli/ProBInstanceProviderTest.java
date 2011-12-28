package de.prob.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

import org.junit.Test;

import test.AbstractUnitTest;

public class ProBInstanceProviderTest extends AbstractUnitTest {

	@Test
	public void testExtractCliInformation() throws Exception {
		PrologProcessProvider ppp = mock(PrologProcessProvider.class);
		ProBInstanceProvider factory = new ProBInstanceProvider(ppp);

		String text = "No file to process\nStarting Socket Server\n"
				+ "Application Path: /Users/bendisposto/.prob\nPort: 61013\n"
				+ "probcli revision: $Rev$\nuser interrupt reference id: 57124\n"
				+ "-- starting command loop --";

		BufferedReader reader = new BufferedReader(new StringReader(text));

		Map<Class<? extends AbstractCliPattern<?>>, AbstractCliPattern<?>> info = factory
				.extractCliInformation(reader);

		AbstractCliPattern<?> p1 = info.get(InterruptRefPattern.class);
		assertNotNull(p1);

		AbstractCliPattern<?> p2 = info.get(PortPattern.class);
		assertNotNull(p2);

	}
}
