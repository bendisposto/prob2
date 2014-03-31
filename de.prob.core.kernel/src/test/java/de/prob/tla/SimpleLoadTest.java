package de.prob.tla;


import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.scripting.Api;
import de.prob.webconsole.ServletContextListener;

public class SimpleLoadTest {

	private Api api;

	@Before
	public void setup() {
		api = ServletContextListener.INJECTOR.getInstance(Api.class);
	}

	@Test
	public void testLoadTLAFile() throws IOException, BException {
		ClassicalBModel model = api
				.tla_load("src/test/resources/tla/Foo.tla");
		assertNotNull(model);

	}

}
