import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Injector;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.Main;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class LoadStdLibTest {

	@Ignore
	@Test
	public void test() throws BException, URISyntaxException, IOException {

		assertNull(System.getProperty("prob.stdlib"));
		Injector injector = Main.getInjector();
		Main instance = injector.getInstance(Main.class);
		assertNotNull(System.getProperty("prob.stdlib"));
		System.out.println(System.getProperty("prob.stdlib"));

		Api api = injector.getInstance(Api.class);
		URL resource = getClass().getResource("LoadStdLibTest.mch");

		File f = new File(resource.toURI());
		System.out.println(f);
		StateSpace s = api.b_load(f.getAbsolutePath());
		System.out.println(new Trace(s));

	}

}
