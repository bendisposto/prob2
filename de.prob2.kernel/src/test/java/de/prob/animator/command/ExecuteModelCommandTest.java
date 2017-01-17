package de.prob.animator.command;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.prob.Main;
import de.prob.animator.command.ExecuteModelCommand.ExecuteModelResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class ExecuteModelCommandTest {

	private Api api;
	
	
	@BeforeClass 
	public static void prepare(){
		if ("true".equals(System.getenv("TRAVIS"))) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
	}

	@Before
	public void setup() {
		api = Main.getInjector().getInstance(Api.class);
		String probHome = System.getProperty("prob.home");
		if (probHome != null) {
			System.out.println("using prob.home: " + System.getProperty("prob.home"));
		}
		System.out.println("Started " + api.getVersion());
		
	}

	@Test
	public void testExecuteModelCommand() throws IOException, ModelTranslationError {
		StateSpace s = api.b_load("src" + File.separator + "test" + File.separator + "resources" + File.separator + "b"
				+ File.separator + "Counter.mch");
		Trace t = new Trace(s);
		ExecuteModelCommand cmd = new ExecuteModelCommand(s, t.getCurrentState(), 33);
		s.execute(cmd);
		int numberofStatesExecuted = cmd.getNumberofStatesExecuted();
		assertEquals(33, numberofStatesExecuted);
		ExecuteModelResult result = cmd.getResult();
		assertEquals(ExecuteModelCommand.ExecuteModelResult.MAXIMUM_NR_OF_STEPS_REACHED, result);
		List<Transition> newTransitions = cmd.getNewTransitions();
		t = t.add(newTransitions.get(0));
		State currentState = t.getCurrentState();
		ClassicalB evalElement = new ClassicalB("x");
		EvalResult evalResult = (EvalResult) currentState.eval(evalElement);
		assertEquals("33", evalResult.getValue());
	}

}
