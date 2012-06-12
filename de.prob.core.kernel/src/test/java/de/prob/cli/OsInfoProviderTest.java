package de.prob.cli;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Test;

import test.AbstractUnitTest;

public class OsInfoProviderTest extends AbstractUnitTest {

	static final HashMap<String, String> supported = new HashMap<String, String>();
	static final String[] unsupported = { "OS/2", "Solaris	", "SunOS",
			"MPE/iX", "HP-UX", "AIX	", "OS/390", "FreeBSD", "Irix",
			"Digital Unix", "NetWare 4.11", "OSF1", "OpenVMS" };
	private static final String LINUX = "Linux";
	private static final String MAC = "MacOs";
	private static final String WIN = "Windows";

	static {
		supported.put("Linux", LINUX);
		supported.put("Mac OS", MAC);
		supported.put("Mac OS X", MAC);
		supported.put("Windows 95", WIN);
		supported.put("Windows 98", WIN);
		supported.put("Windows Me", WIN);
		supported.put("Windows NT", WIN);
		supported.put("Windows 2000", WIN);
		supported.put("Windows 2003", WIN);
		supported.put("Windows XP", WIN);
		supported.put("Windows CE", WIN);
	}

	@Test
	public void testSupportedOS() {
		for (Entry<String, String> entry : supported.entrySet()) {
			assertEquals(entry.getValue(), new OsInfoProvider(entry.getKey(),
					"i386").get().name);
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnsupportedOS() {
		String[] unsupported2 = unsupported;
		for (String string : unsupported2) {
			new OsInfoProvider(string, "i386").get();
		}
	}

}
