package de.prob.animator.command;

import java.util.List;

import de.prob.statespace.Transition;

/**
 * This interface is intended to be implemented by {@link AbstractCommand}s in
 * the case that the execution of the execution of the command results in the
 * modification of the state space on the Prolog side. If this is the case, the
 * (possibly) new transitions must be added to the state space so that it
 * functions correctly on the Java side.
 * 
 * @author joy
 * 
 */
public interface IStateSpaceModifier {

	/**
	 * If a command results in the expansion of the state space on the Prolog
	 * side it should provide access to the (possibly) new transitions via this
	 * method. The developer does not need to add these transitions explicitly
	 * to the state space. The state space will automatically add them after the
	 * execution of the command.
	 * 
	 * @return List of the (possibly) new transitions that have been added to
	 *         the state space.
	 */
	List<Transition> getNewTransitions();
}
