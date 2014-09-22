package test;

import java.io.IOException;

import org.junit.BeforeClass;

public abstract class AbstractUnitTest {

	protected final boolean integrationTestRun = isIntegrationTestRun();

	@BeforeClass
	public static void setup() throws IOException {
		System.setProperty("PROB_LOG_CONFIG", "fulltrace.xml");
	}

	private boolean isIntegrationTestRun() {
		String itp = System.getProperty("integrationtest");
		if (itp != null)
			return "true".equals(itp.toLowerCase());
		return false;
	}

}
