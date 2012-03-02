package de.prob.animator.command;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import test.TestHelper;
import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.PrologTerm;

public class SpecializedBooleanCheckersCommandTest {
	Random r = new Random();

	@Test
	public void testCheckInitialisationStatusCommand() throws ProBException {
		boolean b = r.nextBoolean();
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInitialisationStatusCommand command = new CheckInitialisationStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInitialized());
	}

	@Test
	public void testCheckInvariantStatusCommand() throws ProBException {
		boolean b = r.nextBoolean();
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInvariantStatusCommand command = new CheckInvariantStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInvariantViolated());
	}

	@Test
	public void testCheckMaxOperationReachedStatusCommand()
			throws ProBException {
		boolean b = r.nextBoolean();
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckMaxOperationReachedStatusCommand command = new CheckMaxOperationReachedStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.maxOperationReached());
	}

	@Test
	public void testCheckTimeoutStatusCommand() throws ProBException {
		boolean b = r.nextBoolean();
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckTimeoutStatusCommand command = new CheckTimeoutStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isTimeout());
	}

}
