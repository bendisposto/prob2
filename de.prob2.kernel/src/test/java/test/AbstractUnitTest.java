package test;

import org.junit.BeforeClass;

public abstract class AbstractUnitTest {
	@BeforeClass
	public static void setup() {
		System.setProperty("PROB_LOG_CONFIG", "fulltrace.xml");
	}
}
