package de.prob.cli.integration;

import java.io.File;
import java.io.IOException;

import de.prob.Main;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleLoadTest {

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
		s.kill();
	}

	@Test
	public void testLoadTLAFile2() throws IOException, ModelTranslationError {
		StateSpace s = api.tla_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "Definitions.tla");
		assertNotNull(s);
		Trace t = new Trace(s);
		assertEquals(1, t.getNextTransitions(FormulaExpand.EXPAND).size());
		s.kill();
	}

	@Test
	public void testClub() throws IOException, ModelTranslationError {
		StateSpace s = api.tla_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "ForDistribution" + File.separator + "Club.tla");
		assertNotNull(s);
		Trace t = new Trace(s);
		assertEquals(1, t.getNextTransitions(FormulaExpand.EXPAND).size());
		s.kill();
	}

	@Test
	public void testLoadBFile() throws IOException, ModelTranslationError {
		StateSpace s = api.b_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "Foo.mch");
		assertNotNull(s);
		s.kill();
	}

	@Test
	public void testLoadTLAFileChoose() throws IOException, ModelTranslationError {
		StateSpace s = api.tla_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tla" + File.separator + "Choose.tla");
		assertNotNull(s);
		s.kill();
	}

	@Test
	public void testLoadBRulesFile() throws IOException, ModelTranslationError {
		StateSpace s = api.brules_load("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "brules" + File.separator + "SimpleRulesMachine.rmch");
		assertNotNull(s);
		s.kill();
	}

}
