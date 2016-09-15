import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.Main;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class LoadStdLibTest {

	private static final String PROB_STDLIB = "prob.stdlib";
	Logger logger = LoggerFactory.getLogger(LoadStdLibTest.class);

	@Test
	public void test() throws BException, URISyntaxException, IOException {

		assertNull(System.getProperty(PROB_STDLIB));
		Injector injector = Main.getInjector();
		injector.getInstance(Main.class);
		assertNotNull(System.getProperty(PROB_STDLIB));
		logger.debug("Value of property prob.stdlib: {}", System.getProperty(PROB_STDLIB));

		Api api = injector.getInstance(Api.class);
		URL resource = getClass().getResource("LoadStdLibTest.mch");

		File f = new File(resource.toURI());
		logger.debug("Testmachine File: {}", f);
		StateSpace s = api.b_load(f.getAbsolutePath());
		Trace x = new Trace(s);
		logger.debug("Trace: {}", x);

	}

}
