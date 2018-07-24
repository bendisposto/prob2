package de.prob.statespace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.krukow.clj_lang.PersistentVector;

import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractModel;
import de.prob.util.Tuple2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import groovy.lang.GroovyObjectSupport;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

/**
 * @author joy
 */
public class Trace extends GroovyObjectSupport {
	private boolean exploreStateByDefault = true;
	private final TraceElement current;
	private final TraceElement head;
	private final StateSpace stateSpace;
	private final UUID uuid;
	private final List<Transition> transitionList;

	@SuppressFBWarnings(value = "RANGE_ARRAY_INDEX", justification = "False positive, Findbugs vs. Groovy")
	public Trace(final StateSpace s) {
		this(s.getRoot());
	}

	public Trace(final State startState) {
		this(startState.getStateSpace(), new TraceElement(startState), PersistentVector.emptyVector(), UUID.randomUUID());
	}

	public Trace(final StateSpace s, final TraceElement head, List<Transition> transitionList, UUID uuid) {
		this(s, head, head, transitionList, uuid);
	}

	private Trace(final StateSpace s, final TraceElement head, final TraceElement current, List<Transition> transitionList, UUID uuid) {
		this.stateSpace = s;
		this.head = head;
		this.current = current;
		this.transitionList = transitionList;
		this.uuid = uuid;
	}

	public boolean isExploreStateByDefault() {
		return exploreStateByDefault;
	}

	public void setExploreStateByDefault(boolean exploreStateByDefault) {
		this.exploreStateByDefault = exploreStateByDefault;
	}

	public final TraceElement getCurrent() {
		return current;
	}

	public final TraceElement getHead() {
		return head;
	}

	public final UUID getUUID() {
		return uuid;
	}

	public AbstractEvalResult evalCurrent(String formula, FormulaExpand expand) {
		return getCurrentState().eval(formula, expand);
	}
	
	/**
	 * @deprecated Use {@link #evalCurrent(String, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public AbstractEvalResult evalCurrent(String formula) {
		return getCurrentState().eval(formula, FormulaExpand.TRUNCATE);
	}

	public AbstractEvalResult evalCurrent(IEvalElement formula) {
		return getCurrentState().eval(formula);
	}

	public int size() {
		return transitionList.size();
	}

	public List<Tuple2<String, AbstractEvalResult>> eval(IEvalElement formula) {
		final List<EvaluationCommand> cmds = new ArrayList<>();
		for (Transition t : transitionList) {
			if (getStateSpace().canBeEvaluated(t.getDestination())) {
				cmds.add(formula.getCommand(t.getDestination()));
			}
		}

		stateSpace.execute(new ComposedCommand(cmds));

		final List<Tuple2<String, AbstractEvalResult>> res = new ArrayList<>();
		for (EvaluationCommand cmd : cmds) {
			res.add(new Tuple2<>(cmd.getStateId(), cmd.getValue()));
		}
		return res;
	}

	public List<Tuple2<String, AbstractEvalResult>> eval(String formula, FormulaExpand expand) {
		return this.eval(stateSpace.getModel().parseFormula(formula, expand));
	}
	
	/**
	 * @deprecated Use {@link #eval(String, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public List<Tuple2<String, AbstractEvalResult>> eval(String formula) {
		return this.eval(formula, FormulaExpand.TRUNCATE);
	}

	public Trace add(final Transition op) {
		// TODO: Should we check to ensure that current.getCurrentState() == op.getSrcId()
		final TraceElement newHE = new TraceElement(op, current);
		final List<Transition> transitionList = branchTransitionListIfNecessary(op);
		final Trace newTrace = new Trace(stateSpace, newHE, transitionList, this.uuid);
		newTrace.setExploreStateByDefault(this.exploreStateByDefault);
		if (exploreStateByDefault && !op.getDestination().isExplored()) {
			op.getDestination().explore();
		}
		return newTrace;
	}

	public Trace add(final String transitionId) {
		final Transition op = getCurrentState().getOutTransitions().stream()
			.filter(t -> t.getId().equals(transitionId))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException(transitionId + " is not a valid operation on this state"));
		return add(op);
	}

	public Trace add(final int i) {
		return add(String.valueOf(i));
	}

	/**
	 * Tries to find an operation with the specified name and parameters in the
	 * list of transitions calculated by ProB.
	 * @param name of the event to be executed
	 * @param parameters values of the parameters for the event
	 * @return a new trace with the operation added.
	 */
	public Trace addTransitionWith(final String name, final List<String> parameters) {
		Transition op = getCurrentState().getOutTransitions(true, FormulaExpand.EXPAND).stream()
			.filter(it -> it.getName().equals(name) && it.getParameterValues().equals(parameters))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("Could not find operation " + name + " with parameters " + parameters));
		/* TODO: call GetOperationByPredicateCommand when MAX_OPERATIONS reached */
		return add(op);
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	public Trace back() {
		if (canGoBack()) {
			return new Trace(stateSpace, head, current.getPrevious(), transitionList, this.uuid);
		}
		return this;
	}

	/**
	 * Moves one step forward in the animation if this is possible
	 */
	public Trace forward() {
		if (canGoForward()) {
			TraceElement p = head;
			while (!p.getPrevious().equals(current)) {
				p = p.getPrevious();
			}
			return new Trace(stateSpace, head, p, transitionList, this.uuid);
		}
		return this;
	}

	public Trace gotoPosition(int pos) {
		Trace trace = this;
		int currentIndex = trace.getCurrent().getIndex();
		if (pos == currentIndex) {
			return trace;
		} else if (pos > currentIndex && pos < size()) {
			while (pos != trace.getCurrent().getIndex()) {
				trace = trace.forward();
			}
		} else if (pos < currentIndex && pos >= -1) {
			while (pos != trace.getCurrent().getIndex()) {
				trace = trace.back();
			}
		}
		return trace;
	}

	public boolean canGoForward() {
		return !current.equals(head);
	}

	public boolean canGoBack() {
		return current.getPrevious() != null;
	}

	private PersistentVector<Transition> branchTransitionListIfNecessary(Transition newOp) {
		if (head.equals(current)) {
			return ((PersistentVector<Transition>)transitionList).assocN(transitionList.size(), newOp);
		} else {
			final PersistentVector<Transition> tList = PersistentVector.create(transitionList.subList(0, current.getIndex() + 1));
			return tList.assocN(tList.size(), newOp);
		}
	}

	@Override
	public String toString() {
		return stateSpace.printOps(current.getCurrentState()) + getRep();
	}

	public String getRep() {
		if (current.getTransition() == null) {
			return "";
		}
		return getCurrent().getIndex() + " previous transitions. Last executed transition: " + getCurrent().getTransition().evaluate(FormulaExpand.TRUNCATE).getRep();
	}

	public Trace randomAnimation(final int numOfSteps) {
		if (numOfSteps <= 0) {
			return this;
		}

		State currentState = this.current.getCurrentState();
		TraceElement current = this.current;
		PersistentVector<Transition> transitionList = (PersistentVector<Transition>)this.transitionList;
		for (int i = 0; i < numOfSteps; i++) {
			final List<Transition> ops = currentState.getOutTransitions();
			if (ops.isEmpty()) {
				break;
			}
			Collections.shuffle(ops);
			final Transition op = ops.get(0);
			current = new TraceElement(op, current);
			if (i == 0) {
				transitionList = branchTransitionListIfNecessary(op);
			} else {
				transitionList = transitionList.assocN(transitionList.size(), op);
			}
			currentState = op.getDestination();
			if(Thread.currentThread().isInterrupted()) {
				return this;
			}
		}

		return new Trace(stateSpace, current, transitionList, this.uuid);
	}

	/**
	 * This method is included because it translates to groovy magic in a console environment
	 * (allows the execution of an event by calling it as a method in the Trace class).
	 * For executing an event from within Java, use {@link Trace#execute} instead.
	 *
	 * @param method String method name called
	 * @param params List of parameters
	 * @return {@link Trace#execute(String, List)}
	 * @deprecated use {@link Trace#execute}
	 */
	@Deprecated
	@Override
	public Trace invokeMethod(String method, Object params) {
		if (method.startsWith("$") && !"$setup_constants".equals(method) && !"$initialise_machine".equals(method)) {
			method = method.substring(1);
		}

		@SuppressWarnings("unchecked")
		final List<String> paramsList = (List<String>)DefaultGroovyMethods.asType(params, List.class);
		final Transition transition = getCurrentState().findTransition(method, paramsList);
		if (transition == null) {
			throw new IllegalArgumentException("Could not execute event with name " + method + " and parameters " + params);
		}
		return add(transition);
	}

	/**
	 * Takes an event name and a list of String predicates and uses {@link State#findTransition(String, List)}
	 * with the {@link Trace#getCurrentState()}, the specified event name, and the conjunction of the parameters.
	 * If the specified operation is invalid, a runtime exception will be thrown.
	 *
	 * @param event String event name
	 * @param predicates List of String predicates to be conjoined
	 * @return {@link Trace} which is a result of executing the specified operation
	 */
	public Trace execute(String event, List<String> predicates) {
		final Transition transition = getCurrentState().findTransition(event, predicates);
		if (transition == null) {
			throw new IllegalArgumentException("Could not execute event with name " + event + " and parameters " + predicates);
		}
		return add(transition);
	}

	public Trace execute(String event, String... predicates) {
		return execute(event, Arrays.asList(predicates));
	}

	/**
	 * Tests to see if the event name plus the conjunction of the parameter strings produce a valid
	 * operation on this state. Uses implementation in {@link Trace#canExecuteEvent(String, List)}
	 *
	 * @param event Name of the event to be executed
	 * @param predicates to be conjoined
	 * @return {@code true}, if the operation can be executed. {@code false}, otherwise
	 */
	public boolean canExecuteEvent(String event, String... predicates) {
		return canExecuteEvent(event, Arrays.asList(predicates));
	}

	/**
	 * Tests to see if the event name plus the conjunction of the parameter strings produce a valid
	 * operation on this state. Uses implementation in {@link StateSpace#isValidOperation(State, String, String)}
	 *
	 * @param event Name of the event to be executed
	 * @param predicates List of String predicates to be conjoined
	 * @return {@code true}, if the operation can be executed. {@code false}, otherwise
	 */
	public boolean canExecuteEvent(String event, List<String> predicates) {
		return getCurrentState().findTransition(event, predicates) != null;
	}

	public Trace anyOperation(final Object filter) {
		List<Transition> ops = current.getCurrentState().getOutTransitions(true, FormulaExpand.EXPAND);
		if (filter instanceof String) {
			final Pattern filterPattern = Pattern.compile((String)filter);
			ops = ops.stream().filter(t -> filterPattern.matcher(t.getName()).matches()).collect(Collectors.toList());
		}
		if (filter instanceof ArrayList) {
			ops = ops.stream().filter(t -> ((List<?>)filter).contains(t.getName())).collect(Collectors.toList());
		}
		Collections.shuffle(ops);
		if (!ops.isEmpty()) {
			Transition op = ops.get(0);
			return add(op.getId());
		}
		return this;
	}

	public Trace anyEvent(Object filter) {
		return anyOperation(filter);
	}

	public StateSpace getStateSpace() {
		return stateSpace;
	}

	public Set<Transition> getNextTransitions() {
		return getNextTransitions(false, FormulaExpand.TRUNCATE);
	}

	/**
	 * @deprecated Use {@link #getNextTransitions(boolean, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public Set<Transition> getNextTransitions(boolean evaluate) {
		return this.getNextTransitions(evaluate, FormulaExpand.TRUNCATE);
	}

	public Set<Transition> getNextTransitions(boolean evaluate, FormulaExpand expansion) {
		return new HashSet<>(getCurrentState().getOutTransitions(evaluate, expansion));
	}

	public State getCurrentState() {
		return current.getCurrentState();
	}

	public State getPreviousState() {
		return current.getPrevious().getCurrentState();
	}

	public Transition getCurrentTransition() {
		return current.getTransition();
	}

	public AbstractModel getModel() {
		return stateSpace.getModel();
	}

	public Object asType(Class<?> clazz) {
		if (clazz == StateSpace.class) {
			return stateSpace;
		}
		if (clazz == AbstractModel.class) {
			return stateSpace.getModel();
		}
		if (clazz == stateSpace.getModel().getClass()) {
			return stateSpace.getModel();
		}
		throw new ClassCastException("Not able to convert Trace object to " + clazz);
	}

	public List<Transition> getTransitionList() {
		return getTransitionList(false, FormulaExpand.TRUNCATE);
	}

	/**
	 * @deprecated Use {@link #getTransitionList(boolean, FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public List<Transition> getTransitionList(boolean evaluate) {
		return this.getTransitionList(evaluate, FormulaExpand.TRUNCATE);
	}

	public List<Transition> getTransitionList(boolean evaluate, FormulaExpand expansion) {
		final List<Transition> ops = transitionList;
		if (evaluate) {
			stateSpace.evaluateTransitions(ops, expansion);
		}
		return ops;
	}

	/**
	 * Takes a {@link StateSpace} and a list of {@link Transition} operations through the {@link StateSpace}
	 * and generates a {@link Trace} object from the information. The list of operations must be a valid
	 * list of operations starting from the root state, and for which the information has already been
	 * cached in the {@link StateSpace}. Otherwise, an assertion error will be thrown. Calls {@link Trace#addTransitions(List)}
	 *
	 * @param s {@link StateSpace} through which the Trace should be generated
	 * @param ops List of {@link Transition} operations that should be executed in the Trace
	 * @return {@link Trace} specified by list of operations
	 */
	public static Trace getTraceFromTransitions(StateSpace s, List<Transition> ops) {
		if (!ops.isEmpty()) {
			Trace t = new Trace(ops.get(0).getSource());
			return t.addTransitions(ops);
		}
		return new Trace(s);
	}

	/**
	 * Adds a list of operations to an existing trace.
	 *
	 * @param ops List of {@link Transition} objects that should be added to the current trace
	 * @return Trace with the ops added
	 */
	public Trace addTransitions(List<Transition> ops) {
		if (ops.isEmpty()) {
			return this;
		}

		final Transition first = ops.get(0);
		TraceElement h = new TraceElement(first, current);
		PersistentVector<Transition> transitionList = branchTransitionListIfNecessary(first);
		for (Transition op : ops.subList(1, ops.size())) {
			h = new TraceElement(op, h);
			transitionList = transitionList.assocN(transitionList.size(), op);
		}
		return new Trace(stateSpace, h, transitionList, this.uuid);
	}

	/**
	 * @return an identical Trace object with a different UUID
	 */
	public Trace copy() {
		return new Trace(stateSpace, head, current, transitionList, UUID.randomUUID());
	}
}
