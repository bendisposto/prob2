package test;

import org.junit.Assume;
import org.junit.Before;

public class AbstractIntegrationTest extends AbstractUnitTest {

	private static final String MSG = "Test skipped. To enable set system property integrationtest=true";

	@Before
	public void setUp() {
		if (!integrationTestRun) {
			System.err.println(MSG);
		}
		Assume.assumeTrue(integrationTestRun);
	}

}
