package de.prob.tla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.prob.Main;
import de.prob.model.AbstratUnitTestRunModelWithCli;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class SimpleLoadTest extends AbstratUnitTestRunModelWithCli {

	private Api api;

	@Before
	public void setupClass() {
		api = Main.getInjector().getInstance(Api.class);
	}
	
	@Test
	public void testLoadTLAFile() throws IOException, ModelTranslationError {
		StateSpace s = api.tla_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "Foo.tla");
		assertNotNull(s);
	}

	@Test
	public void testLoadTLAFile2() throws IOException, ModelTranslationError {
		StateSpace s = api.tla_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "Definitions.tla");
		assertNotNull(s);
		Trace t = new Trace(s);
		assertEquals(1, t.getNextTransitions().size());
	}

	@Test
	public void testClub() throws IOException, ModelTranslationError {
		StateSpace s = api.tla_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "ForDistribution" + File.separator + "Club.tla");
		assertNotNull(s);
		Trace t = new Trace(s);
		assertEquals(1, t.getNextTransitions().size());
	}

	@Test
	public void testLoadBFile() throws IOException, ModelTranslationError {
		StateSpace s = api.b_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "Foo.mch");
		assertNotNull(s);
	}

	@Test
	public void testLoadTLAFileChoose() throws IOException, ModelTranslationError {
		StateSpace s = api.tla_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "Choose.tla");
		assertNotNull(s);
	}

}
