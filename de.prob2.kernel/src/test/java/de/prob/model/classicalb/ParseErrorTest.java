package de.prob.model.classicalb;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.prob.Main;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;

public class ParseErrorTest {

	private Api api;

	@Before
	public void setup() {
		api = Main.getInjector().getInstance(Api.class);
	}

	@Test(expected = ModelTranslationError.class)
	public void testLoadBMachineWithParseError() throws IOException, ModelTranslationError {
		api.b_load("src" + File.separator + "test" + File.separator + "resources" + File.separator + "b"
				+ File.separator + "ParseError.mch");
	}

	@Test(expected = IOException.class)
	public void testLoadBMachineButFileDoesNotExists() throws IOException, ModelTranslationError {
		api.b_load("src" + File.separator + "test" + File.separator + "resources" + File.separator + "b"
				+ File.separator + "FileDoesNotExists.mch");
	}
}
