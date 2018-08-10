package de.prob.model.classicalb;

import java.io.IOException;
import java.nio.file.Paths;

import de.prob.Main;
import de.prob.exception.ProBError;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;

import org.junit.Before;
import org.junit.Test;


public class ParseErrorTest {

	private Api api;

	@Before
	public void setup() {
		api = Main.getInjector().getInstance(Api.class);
	}

	@Test(expected = ProBError.class)
	public void testLoadBMachineWithParseError() throws IOException, ModelTranslationError {
		api.b_load(Paths.get("src", "test", "resources", "b", "ParseError.mch").toString());
	}

	@Test(expected = IOException.class)
	public void testLoadBMachineButFileDoesNotExists() throws IOException, ModelTranslationError {
		api.b_load(Paths.get("src", "test", "resources", "b", "FileDoesNotExists.mch").toString());
	}
}
