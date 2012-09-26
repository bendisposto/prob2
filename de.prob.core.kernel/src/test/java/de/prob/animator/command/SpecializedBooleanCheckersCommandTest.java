package de.prob.animator.command;

import static org.junit.Assert.*;

import org.junit.Test;

import test.TestHelper;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.PrologTerm;

public class SpecializedBooleanCheckersCommandTest {

	@Test
	public void testCheckInitialisationStatusCommandTrue()  {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInitialisationStatusCommand command = new CheckInitialisationStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInitialized());
	}

	@Test
	public void testCheckInitialisationStatusCommandFalse()
			 {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInitialisationStatusCommand command = new CheckInitialisationStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInitialized());
	}

	@Test
	public void testCheckInvariantStatusCommandTrue()  {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInvariantStatusCommand command = new CheckInvariantStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInvariantViolated());
	}

	@Test
	public void testCheckInvariantStatusCommandFalse()  {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckInvariantStatusCommand command = new CheckInvariantStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isInvariantViolated());
	}

	@Test
	public void testCheckMaxOperationReachedStatusCommandTrue()
			 {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckMaxOperationReachedStatusCommand command = new CheckMaxOperationReachedStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.maxOperationReached());
	}

	@Test
	public void testCheckMaxOperationReachedStatusCommandFalse()
			 {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckMaxOperationReachedStatusCommand command = new CheckMaxOperationReachedStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.maxOperationReached());
	}

	@Test
	public void testCheckTimeoutStatusCommandTrue()  {
		boolean b = true;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckTimeoutStatusCommand command = new CheckTimeoutStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isTimeout());
	}

	@Test
	public void testCheckTimeoutStatusCommandFalse()  {
		boolean b = false;
		ISimplifiedROMap<String, PrologTerm> map = TestHelper.mkAtomMock(
				"PropResult", "" + b);
		CheckTimeoutStatusCommand command = new CheckTimeoutStatusCommand(
				"root");
		command.processResult(map);
		assertEquals(b, command.isTimeout());
	}
}
