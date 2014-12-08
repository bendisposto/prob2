package de.prob.tla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.Main;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class SimpleLoadTest {

	private Api api;

	@Before
	public void setup() {
		api = Main.getInjector().getInstance(Api.class);
	}

	@Test
	public void testLoadTLAFile() throws IOException, BException {
		ClassicalBModel model = api.tla_load("src"+File.separator+"test"+File.separator+"resources"+File.separator+"tla"+File.separator+"Foo.tla");
		assertNotNull(model);
	}

	@Test
	public void testLoadTLAFile2() throws IOException, BException {
		ClassicalBModel model = api
				.tla_load("src"+File.separator+"test"+File.separator+"resources"+File.separator+"tla"+File.separator+"Definitions.tla");
		assertNotNull(model);
		StateSpace s = model.getStateSpace();
		Trace t = new Trace(s);
		assertEquals(1, t.getNextTransitions().size());
	}

	@Test
	public void testClub() throws IOException, BException {
		ClassicalBModel model = api
				.tla_load("src"+File.separator+"test"+File.separator+"resources"+File.separator+"tla"+File.separator+"ForDistribution"+File.separator+"Club.tla");
		assertNotNull(model);
		StateSpace s = model.getStateSpace();
		Trace t = new Trace(s);
		assertEquals(1, t.getNextTransitions().size());
	}

	@Test
	public void testLoadBFile() throws IOException, BException {
		ClassicalBModel model = api.b_load("src"+File.separator+"test"+File.separator+"resources"+File.separator+"tla"+File.separator+"Foo.mch");
		assertNotNull(model);
	}

	@Test
	public void testLoadTLAFileChoose() throws IOException, BException {
		ClassicalBModel model = api
				.tla_load("src"+File.separator+"test"+File.separator+"resources"+File.separator+"tla"+File.separator+"Choose.tla");
		assertNotNull(model);
	}

}
