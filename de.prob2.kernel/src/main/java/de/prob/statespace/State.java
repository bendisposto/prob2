package de.prob.statespace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.prob.animator.command.EvaluateFormulasCommand;
import de.prob.animator.command.EvaluateRegisteredFormulasCommand;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.GetBStateCommand;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.StateError;
import de.prob.model.representation.AbstractModel;

import groovy.lang.GroovyObjectSupport;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

/**
 * A reference to the state object in the ProB core.
 *
 * Note: This class contains a reference to the StateSpace object to which this state
 * reference belongs. In order for the garbage collector to work correctly, dereference
 * any State objects after they are no longer needed.
 *
 * @author joy
 */
public class State extends GroovyObjectSupport {
	protected String id;
	private StateSpace stateSpace;
	private boolean explored;
	private List<Transition> transitions;
	private boolean initialised;
	private boolean invariantOk;
	private boolean timeoutOccurred;
	private Set<String> transitionsWithTimeout;
	private boolean maxTransitionsCalculated;
	private Collection<StateError> stateErrors;
	private Map<IEvalElement, AbstractEvalResult> values;

	public State(String id, StateSpace space) {
		this.id = id;
		this.stateSpace = space;
		this.explored = false;
		this.transitions = new ArrayList<>();
		this.values = new HashMap<>();
	}

	/**
	 * This method is included for groovy magic in a console environment.
	 * Use the {@link State#perform} method instead.
	 *
	 * @param method String method name that was called
	 * @param params List of parameter objects that it was called with
	 * @return result of {@link State#perform}
	 * @deprecated use {@link State#perform}
	 */
	@Deprecated
	@Override
	public State invokeMethod(String method, Object params) {
		if (method.startsWith("$") && !"$setup_constants".equals(method) && !"$initialise_machine".equals(method)) {
			method = method.substring(1);
		}

		@SuppressWarnings("unchecked")
		final List<? extends CharSequence> paramsList = (List<? extends CharSequence>)DefaultGroovyMethods.asType(params, List.class);
		final String predicate = paramsList.isEmpty() ? "TRUE = TRUE" : String.join(" & ", paramsList);
		final Transition op = stateSpace.transitionFromPredicate(this, method, predicate, 1).get(0);
		transitions.add(op);
		return op.getDestination();
	}

	/**
	 * Uses {@link State#perform(String, List)} to calculate the destination state of the event
	 * with the specified event name and the conjunction of the parameters.
	 * An exception will be thrown if the specified event and params are invalid for this State.
	 * @param event String event name to execute
	 * @param predicates List of String predicates
	 * @return {@link State} that results from executing the specified event
	 */
	public State perform(String event, String... predicates) {
		return perform(event, Arrays.asList(predicates));
	}

	/**
	 * Uses {@link State#findTransition(String, List)} to calculate the destination state of the event
	 * with the specified event name and the conjunction of the parameters.
	 * An exception will be thrown if the specified event and predicates are invalid for this State.
	 * @param event String event name to execute
	 * @param predicates List of String predicates
	 * @return {@link State} that results from executing the specified event
	 */
	public State perform(String event, List<String> predicates) {
		final Transition op = findTransition(event, predicates);
		if (op == null) {
			throw new IllegalArgumentException("Could not execute " + event + " with predicates " + predicates + " on state " + this.getId());
		}
		return op.getDestination();
	}

	/**
	 * Calls {@link State#findTransition(String, List)}
	 * @param name of the operation
	 * @param predicates list of predicates specifying the parameters
	 * @return the calculated transition, or null if no transition was found.
	 */
	public Transition findTransition(String name, String... predicates) {
		return findTransition(name, Arrays.asList(predicates));
	}

	/**
	 * Calls {@link State#findTransitions(String, List, int)}
	 * @param name of the operation
	 * @param predicates list of predicates specifying the parameters
	 * @return the calculated transition, or null if no transition was found.
	 */
	public Transition findTransition(final String name, List<String> predicates) {
		if (predicates.isEmpty() && !transitions.isEmpty()) {
			final Optional<Transition> op = transitions.stream().filter(t -> t.getName().equals(name)).findAny();
			if (op.isPresent()) {
				return op.get();
			}
		}
		final List<Transition> transitions = findTransitions(name, predicates, 1);
		if (!transitions.isEmpty()) {
			return transitions.get(0);
		}
		return null;
	}

	/**
	 * Calls {@link StateSpace#transitionFromPredicate(State, String, String, int)}
	 * @param name of the operation
	 * @param predicates list of predicates specifying the parameters
	 * @param nrOfSolutions to be found
	 * @return a list of solutions found, or an empty list if no solutions were found
	 */
	public List<Transition> findTransitions(String name, List<String> predicates, int nrOfSolutions) {
		final String predicate = predicates.isEmpty() ? "TRUE = TRUE" : '(' + String.join(") & (", predicates) + ')';
		try {
			final List<Transition> newOps = stateSpace.transitionFromPredicate(this, name, predicate, nrOfSolutions);
			transitions.addAll(newOps);
			return newOps;
		} catch (IllegalArgumentException e) {
			// Skip
		}
		return Collections.emptyList();
	}

	public State anyOperation(final Object filter) {
		List<Transition> ops = getOutTransitions(true, FormulaExpand.TRUNCATE);
		if (filter instanceof String) {
			final Pattern filterPattern = Pattern.compile((String)filter);
			ops = ops.stream().filter(t -> filterPattern.matcher(t.getName()).matches()).collect(Collectors.toList());
		}
		if (filter instanceof ArrayList) {
			ops = ops.stream().filter(t -> ((List<?>)filter).contains(t.getName())).collect(Collectors.toList());
		}
		if (!ops.isEmpty()) {
			Collections.shuffle(ops);
			final Transition op = ops.get(0);
			final State newState = op.getDestination();
			newState.explore();
			return newState;
		}
		return this;
	}

	public State anyEvent(Object filter) {
		return anyOperation(filter);
	}

	/**
	 * Takes a formula and evaluates it via the {@link State#eval(IEvalElement)}
	 * method. The formula is parsed via the {@link AbstractModel#parseFormula(String)} method.
	 * @param formula representation of a formula
	 * @return the {@link AbstractEvalResult} calculated from ProB
	 */
	public AbstractEvalResult eval(String formula, FormulaExpand expand) {
		return eval(stateSpace.getModel().parseFormula(formula, expand));
	}
	
	/**
	 * Takes a formula and evaluates it via the {@link State#eval(IEvalElement)}
	 * method. The formula is parsed via the {@link AbstractModel#parseFormula(String)} method.
	 * @param formula representation of a formula
	 * @return the {@link AbstractEvalResult} calculated from ProB
	 * @deprecated Use {@link #eval(String, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public AbstractEvalResult eval(String formula) {
		return this.eval(formula, FormulaExpand.TRUNCATE);
	}

	/**
	 * Takes a formula and evaluates it via the {@link State#eval(List)} method.
	 * @param formula as IEvalElement
	 * @return the {@link AbstractEvalResult} calculated by ProB
	 */
	public AbstractEvalResult eval(IEvalElement formula) {
		return eval(Collections.singletonList(formula)).get(0);
	}

	public List<AbstractEvalResult> eval(IEvalElement... formulas) {
		return eval(Arrays.asList(formulas));
	}

	public Map<IEvalElement, AbstractEvalResult> getVariableValues() {
		return evalFormulas(stateSpace.getLoadedMachine().getVariableEvalElements());
	}

	public Map<IEvalElement, AbstractEvalResult> getConstantValues() {
		return evalFormulas(stateSpace.getLoadedMachine().getConstantEvalElements());
	}

	public Map<IEvalElement, AbstractEvalResult> evalFormulas(List<IEvalElement> formulas) {
		final List<IEvalElement> notEvaluatedElements = new ArrayList<>();
		for (IEvalElement element : formulas) {
			if (!values.containsKey(element)) {
				notEvaluatedElements.add(element);
			}
		}
		if (!notEvaluatedElements.isEmpty()) {
			final EvaluateFormulasCommand cmd = new EvaluateFormulasCommand(notEvaluatedElements, this.getId());
			stateSpace.execute(cmd);
			values.putAll(cmd.getResultMap());
		}

		Map<IEvalElement, AbstractEvalResult> result = new LinkedHashMap<>();
		for (IEvalElement element : formulas) {
			result.put(element, values.get(element));
		}
		return result;
	}

	/**
	 * @param formulas to be evaluated
	 * @return list of results calculated by ProB for a given formula
	 */
	public List<AbstractEvalResult> eval(List<IEvalElement> formulas) {
		return new ArrayList<>(evalFormulas(formulas).values());
	}

	@Override
	public String toString() {
		return id;
	}

	public String getId() {
		return id;
	}

	public long numericalId() {
		return "root".equals(id) ? -1 : Long.parseLong(id);
	}

	public String getStateRep() {
		if (stateSpace.getModel().getFormalismType().equals(FormalismType.B)) {
			final GetBStateCommand cmd = new GetBStateCommand(this);
			stateSpace.execute(cmd);
			return cmd.getState();
		}
		return "unknown";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof State && this.id.equals(((State)obj).getId()) && this.getStateSpace().equals(((State)obj).getStateSpace());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, stateSpace);
	}

	public StateSpace getStateSpace() {
		return stateSpace;
	}

	public boolean isExplored() {
		return explored;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public boolean isInitialised() {
		if (!explored) {
			explore();
		}
		return initialised;
	}

	public boolean isInvariantOk() {
		if (!explored) {
			explore();
		}
		return invariantOk;
	}

	public boolean isMaxTransitionsCalculated() {
		if (!explored) {
			explore();
		}
		return maxTransitionsCalculated;
	}

	public boolean isTimeoutOccurred() {
		if (!explored) {
			explore();
		}
		return timeoutOccurred;
	}

	public Set<String> getTransitionsWithTimeout() {
		if (!explored) {
			explore();
		}
		return transitionsWithTimeout;
	}

	public Collection<StateError> getStateErrors() {
		if (!explored) {
			explore();
		}
		return stateErrors;
	}

	public List<Transition> getOutTransitions() {
		// The FormulaExpand argument is ignored if evaluate is false
		return getOutTransitions(false, FormulaExpand.TRUNCATE);
	}

	/**
	 * @deprecated Use {@link #getOutTransitions(boolean, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public List<Transition> getOutTransitions(boolean evaluate) {
		return this.getOutTransitions(evaluate, FormulaExpand.TRUNCATE);
	}

	/**
	 * If the state has not yet been explored (i.e. the default number
	 * of outgoing transitions has not yet been calculated by ProB), this
	 * is done via the {@link State#explore()} method. By default, the list of
	 * {@link Transition} objects created will not be evaluated (i.e. certain
	 * information about the transition will be lazily retrieved from ProB
	 * at a later time). However, if an optional parameter is supplied and
	 * set to true, the evaluation of all of the {@link Transition} objects will
	 * occur before the list is returned via the {@link StateSpace#evaluateTransitions(Collection, FormulaExpand)}
	 * method.
	 * @param evaluate whether or not the list of transitions should be evaluated. By default this is set to false.
	 * @return the outgoing transitions from this state
	 */
	public List<Transition> getOutTransitions(boolean evaluate, FormulaExpand expansion) {
		if (!explored) {
			explore();
		}
		if (evaluate) {
			stateSpace.evaluateTransitions(transitions, expansion);
		}
		return transitions;
	}

	public State explore() {
		final ExploreStateCommand cmd = new ExploreStateCommand(stateSpace, id, stateSpace.getSubscribedFormulas());
		stateSpace.execute(cmd);
		transitions = cmd.getNewTransitions();
		values.putAll(cmd.getFormulaResults());
		initialised = cmd.isInitialised();
		invariantOk = cmd.isInvariantOk();
		timeoutOccurred = cmd.isTimeoutOccured();
		maxTransitionsCalculated = cmd.isMaxOperationsReached();
		stateErrors = cmd.getStateErrors();
		transitionsWithTimeout = cmd.getOperationsWithTimeout();
		explored = true;
		return this;
	}

	public Map<IEvalElement, AbstractEvalResult> getValues() {
		Set<IEvalElement> formulas = stateSpace.getSubscribedFormulas();
		List<IEvalElement> toEvaluate = new ArrayList<>();
		for (IEvalElement f : formulas) {
			if (!values.containsKey(f)) {
				toEvaluate.add(f);
			}
		}
		if (!toEvaluate.isEmpty()) {
			final EvaluateRegisteredFormulasCommand cmd = new EvaluateRegisteredFormulasCommand(this.getId(), toEvaluate);
			stateSpace.execute(cmd);
			values.putAll(cmd.getResults());
		}
		return new HashMap<>(values);
	}
}
