package de.prob.cli.integration.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.prob.Main;
import de.prob.animator.command.GetMachineOperationInfos;
import de.prob.animator.command.GetMachineOperationInfos.OperationInfo;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.StateSpace;

public class GetMachineOperationInfosCommandTest {

	private Api api;
	private StateSpace s;

	@Before
	public void setupClass() {
		api = Main.getInjector().getInstance(Api.class);
	}

	@Test
	public void testGetMachineOperationInfosCommand() throws IOException, ModelTranslationError {
		System.out.println(Main.getProBDirectory());
		System.out.println(api.getVersion());
		s = api.b_load("src" + File.separator + "test" + File.separator + "resources" + File.separator + "b"
				+ File.separator + "ExampleMachine.mch");
		assertNotNull(s);
		GetMachineOperationInfos command = new GetMachineOperationInfos();
		s.execute(command);
		System.out.println(command.getOperationInfos());
		OperationInfo operationInfo = command.getOperationInfos().get(0);
		assertEquals("Foo", operationInfo.getOperationName());
		assertEquals("p1", operationInfo.getParameterNames().get(0));
		assertEquals("p2", operationInfo.getParameterNames().get(1));
		assertEquals("out1", operationInfo.getOutputParameterNames().get(0));
		assertEquals("out2", operationInfo.getOutputParameterNames().get(1));
	}

	@After
	public void tearDown() {
		s.kill();
	}
}
