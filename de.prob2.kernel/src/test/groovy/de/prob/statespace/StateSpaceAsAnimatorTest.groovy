package de.prob.statespace


import static org.junit.Assert.*
import static org.mockito.Mockito.*
import spock.lang.Specification
import de.prob.Main
import de.prob.animator.command.CheckInitialisationStatusCommand
import de.prob.animator.command.CheckInvariantStatusCommand
import de.prob.scripting.ClassicalBFactory


class StateSpaceAsAnimatorTest extends Specification {

	static StateSpace s
	static State root
	static State firstState

	def setupSpec() {
		def path = System.getProperties().get("user.dir")+"/groovyTests/machines/scheduler.mch"
		ClassicalBFactory factory = Main.getInjector().getInstance(ClassicalBFactory.class)
		s = factory.load(path) as StateSpace
		root = s.getRoot()
		firstState = root.$initialise_machine()
	}

	def "it is possible to interrupt a command that is running"() {
		// TODO: once the interrupt mechanism is set in stone
	}

	def "it is possible to execute a single command"() {
		when:
		CheckInitialisationStatusCommand cmd = new CheckInitialisationStatusCommand(root.getId())
		s.execute(cmd)

		then:
		cmd.getResult() == false
	}

	def "it is possible to execute multiple commmands"() {
		when:
		CheckInitialisationStatusCommand cmd = new CheckInitialisationStatusCommand(firstState.getId())
		CheckInvariantStatusCommand cmd2 = new CheckInvariantStatusCommand(firstState.getId())
		s.execute(cmd, cmd2)

		then:
		cmd.getResult() == true
		cmd2.isInvariantViolated() == false
	}

	def "it is possible to start and end transactions"() {
		when:
		def a = s.isBusy()
		s.startTransaction()
		def b = s.isBusy()
		s.endTransaction()
		def c = s.isBusy()

		then:
		!a
		b
		!c
	}

	class MyListener implements IAnimationChangeListener {

		boolean i_am_busy = false
		List<String> notifications = []

		@Override
		public void traceChange(Trace currentTrace,
				boolean currentAnimationChanged) {
		}

		@Override
		public void animatorStatus(boolean busy) {
			notifications << "Status: "+ busy
		}
	}

	def "it is possible to notify animation listeners that the animator is busy"() {
		when:
		AnimationSelector animations = Main.getInjector().getInstance(AnimationSelector.class)
		animations.addNewAnimation(new Trace(s))
		def mylistener = new MyListener()
		animations.registerAnimationChangeListener(mylistener)
		s.startTransaction()
		s.endTransaction()

		then:
		mylistener.notifications == [
			"Status: false",
			"Status: true",
			"Status: false"
		]
	}
}
