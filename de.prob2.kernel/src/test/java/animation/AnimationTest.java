package animation;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.Main;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class AnimationTest {

	@Test
	public void test() throws IOException, BException {
		Api api = Main.getInjector().getInstance(Api.class);

		System.out.println(api.getVersion());

		StateSpace s = api.b_load("groovyTests" + File.separator + "machines" + File.separator + "scheduler.mch");

		Trace trace = new Trace(s);
		trace.randomAnimation(10);
	}

}
