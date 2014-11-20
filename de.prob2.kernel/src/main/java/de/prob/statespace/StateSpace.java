package de.prob.statespace;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.CheckIfStateIdValidCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.command.FindTraceBetweenNodesCommand;
import de.prob.animator.command.FindValidStateCommand;
import de.prob.animator.command.GetOperationByPredicateCommand;
import de.prob.animator.command.GetOpsFromIds;
import de.prob.animator.command.GetShortestTraceCommand;
import de.prob.animator.command.GetStatesFromPredicate;
import de.prob.animator.command.RegisterFormulaCommand;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.CSPModel;

/**
 * 
 * The StateSpace is where the animation of a given model is carried out. The
 * methods in the StateSpace allow the user to:
 * 
 * 1) Find new states and operations
 * 
 * 2) Inspect different states within the StateSpace
 * 
 * 3) Evaluate custom predicates and expressions
 * 
 * 4) Register listeners that are notified of new states and operations
 * 
 * The implementation of the StateSpace is as a {@link StateSpace} with
 * {@link State}s as vertices and {@link Transition}s as edges. Therefore, some
 * basic graph functionalities are provided.
 * 
 * @author joy
 * 
 */
public class StateSpace implements IAnimator {

	Logger logger = LoggerFactory.getLogger(StateSpace.class);
	private transient IAnimator animator;

	private AbstractCommand loadcmd;

	private final HashMap<IEvalElement, WeakHashMap<Object, Object>> formulaRegistry = new HashMap<IEvalElement, WeakHashMap<Object, Object>>();
	private final Set<IEvalElement> subscribedFormulas = new HashSet<IEvalElement>();

	LoadingCache<String, State> states = CacheBuilder.newBuilder()
			.maximumSize(100)
			// .expireAfterWrite(10, TimeUnit.MINUTES)
			// .removalListener(MY_LISTENER) this might be useful for triggering
			// removal of formulas?
			.build(new CacheLoader<String, State>() {
				@Override
				public State load(final String key) throws Exception {
					return load(key);
				}
			});

	private AbstractModel model;

	@Inject
	public StateSpace(final Provider<IAnimator> panimator) {
		animator = panimator.get();
	}

	public State getRoot() {
		return addState("root");
	}

	public State getState(final String id) {
		State sId = states.getIfPresent(id);
		if (sId != null) {
			return sId;
		}
		CheckIfStateIdValidCommand cmd = new CheckIfStateIdValidCommand(id);
		execute(cmd);
		if (cmd.isValidState()) {
			sId = new State(id, this);
			states.put(id, sId);
			return sId;
		}
		throw new IllegalArgumentException(id
				+ " does not represent a valid state in the StateSpace");
	}

	State addState(final String id) {
		State sId = states.getIfPresent(id);
		if (sId != null) {
			return sId;
		}
		sId = new State(id, this);
		states.put(id, sId);
		return sId;
	}

	public State getState(final int id) {
		if (id == -1) {
			return getRoot();
		}
		return getState(String.valueOf(id));
	}

	/**
	 * Whenever a {@link StateSpace} instance is created, it is assigned a
	 * unique identifier to help external parties differentiate between two
	 * instances. This getter method returns this id.
	 * 
	 * @return the unique {@link String} id associated with this
	 *         {@link StateSpace} instance
	 */
	@Override
	public String getId() {
		return animator.getId();
	}

	public List<State> getStatesFromPredicate(final IEvalElement predicate) {
		GetStatesFromPredicate cmd = new GetStatesFromPredicate(predicate);
		execute(cmd);
		List<String> ids = cmd.getIds();
		List<State> sIds = new ArrayList<State>();
		for (String s : ids) {
			sIds.add(addState(s));
		}
		return sIds;
	}

	/**
	 * Takes the name of an operation and a predicate and finds Operations that
	 * satisfy the name and predicate at the given stateId. New Operations are
	 * added to the graph. This is only valid for ClassicalB predicates.
	 * 
	 * @param stateId
	 *            {@link State} from which the operation should be found
	 * @param name
	 *            name of the operation that should be executed
	 * @param predicate
	 *            an additional guard for the operation. This usually describes
	 *            the parameters
	 * @param nrOfSolutions
	 *            int number of solutions that should be found for the given
	 *            predicate
	 * @return list of operations calculated by ProB
	 * @throws BException
	 */
	public List<Transition> transitionFromPredicate(final State stateId,
			final String name, final String predicate, final int nrOfSolutions)
			throws IllegalArgumentException {
		final IEvalElement pred = model.parseFormula(predicate);
		final GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				this, stateId.getId(), name, pred, nrOfSolutions);
		execute(command);
		if (command.hasErrors()) {
			throw new IllegalArgumentException("Executing operation " + name
					+ " with predicate " + predicate + " produced errors: "
					+ Joiner.on(", ").join(command.getErrors()));
		}
		return command.getNewTransitions();
	}

	/**
	 * Tests to see if a combination of an operation name and a predicate is
	 * valid from a given state.
	 * 
	 * @param stateId
	 *            {@link State} id for state to test
	 * @param name
	 *            {@link String} name of operation
	 * @param predicate
	 *            {@link String} predicate to test
	 * @return true, if the operation is valid from the given state. False
	 *         otherwise.
	 */
	public boolean isValidOperation(final State stateId, final String name,
			final String predicate) {
		final ClassicalB pred = new ClassicalB(predicate);
		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				this, stateId.getId(), name, pred, 1);
		execute(command);
		return !command.hasErrors()
				&& (command.getNewTransitions().size() == 1);
	}

	public List<IEvalResult> eval(final State state,
			final List<IEvalElement> formulas) {
		return state.eval(formulas);
	}

	/**
	 * Calculates the registered formulas at the given state and returns the
	 * cached values
	 * 
	 * @param stateId
	 * @return map from {@link IEvalElement} object to {@link IEvalResult}
	 *         objects
	 */
	public Map<IEvalElement, IEvalResult> valuesAt(final State stateId) {
		stateId.explore();
		return stateId.getValues();
	}

	public boolean canBeEvaluated(final State stateId) {
		return stateId.isInitialised();
	}

	public void subscribe(final Object subscriber,
			final List<IEvalElement> formulasOfInterest) {
		List<AbstractCommand> subscribeCmds = new ArrayList<AbstractCommand>();
		for (IEvalElement formulaOfInterest : formulasOfInterest) {
			if (formulaOfInterest instanceof CSP) {
				logger.info(
						"CSP formula {} not subscribed because CSP evaluation is not state based. Use eval method instead",
						formulaOfInterest.getCode());
			} else {
				if (formulaRegistry.containsKey(formulaOfInterest)) {
					formulaRegistry.get(formulaOfInterest).put(subscriber,
							new WeakReference<Object>(formulaOfInterest));
					subscribedFormulas.add(formulaOfInterest);
				} else {
					WeakHashMap<Object, Object> subscribers = new WeakHashMap<Object, Object>();
					subscribers.put(subscriber, new WeakReference<Object>(
							subscriber));
					formulaRegistry.put(formulaOfInterest, subscribers);
					subscribeCmds.add(new RegisterFormulaCommand(
							formulaOfInterest));
					subscribedFormulas.add(formulaOfInterest);
				}
			}
		}
		execute(new ComposedCommand(subscribeCmds));
	}

	/**
	 * If a class is interested in having a particular formula calculated and
	 * cached whenever a new state is explored, then they "subscribe" to that
	 * formula with a reference to themselves. This should only be used for
	 * B-Type formulas ({@code EventB} or {@code ClassicalB}). {@code CSP}
	 * formulas will not be subscribed, because CSP evaluation is not state
	 * based.
	 * 
	 * @param subscriber
	 * @param formulaOfInterest
	 */
	public void subscribe(final Object subscriber,
			final IEvalElement formulaOfInterest) {
		if (formulaOfInterest instanceof CSP) {
			logger.info(
					"CSP formula {} not subscribed because CSP evaluation is not state based. Use eval method instead",
					formulaOfInterest.getCode());
			return;
		}

		if (formulaRegistry.containsKey(formulaOfInterest)) {
			formulaRegistry.get(formulaOfInterest).put(subscriber,
					new WeakReference<Object>(subscriber));
		} else {
			execute(new RegisterFormulaCommand(formulaOfInterest));
			WeakHashMap<Object, Object> subscribers = new WeakHashMap<Object, Object>();
			subscribers.put(subscriber, new WeakReference<Object>(subscriber));
			formulaRegistry.put(formulaOfInterest, subscribers);
		}
		if (!subscribedFormulas.contains(formulaOfInterest)) {
			subscribedFormulas.add(formulaOfInterest);
		}
	}

	public boolean isSubscribed(final IEvalElement formula) {
		return formulaRegistry.containsKey(formula)
				&& !formulaRegistry.get(formula).isEmpty();
	}

	/**
	 * If a subscribed class is no longer interested in the value of a
	 * particular formula, then they can unsubscribe to that formula
	 * 
	 * @param subscriber
	 * @param formulaOfInterest
	 */
	public void unsubscribe(final Object subscriber,
			final IEvalElement formulaOfInterest) {
		if (formulaRegistry.containsKey(formulaOfInterest)) {
			final WeakHashMap<Object, Object> subscribers = formulaRegistry
					.get(formulaOfInterest);
			subscribers.remove(subscriber);
			if (subscribers.isEmpty()) {
				subscribedFormulas.remove(formulaOfInterest);
			}
		}
	}

	public Set<IEvalElement> getSubscribedFormulas() {
		List<IEvalElement> toRemove = new ArrayList<IEvalElement>();
		for (IEvalElement e : subscribedFormulas) {
			WeakHashMap<Object, Object> subscribers = formulaRegistry.get(e);
			if (subscribers == null || subscribers.isEmpty()) {
				toRemove.add(e);
			}
		}
		subscribedFormulas.removeAll(toRemove);
		return subscribedFormulas;
	}

	// ANIMATOR
	@Override
	public void sendInterrupt() {
		animator.sendInterrupt();
	}

	@Override
	public void execute(final AbstractCommand command) {
		animator.execute(command);
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		animator.execute(commands);
	}

	@Override
	public String toString() {
		return animator.getId();
	}

	/**
	 * @param state
	 * @return Returns a String representation of the operations available from
	 *         the specified {@link State}. This is mainly useful for console
	 *         output.
	 */
	public String printOps(final State state) {
		final StringBuilder sb = new StringBuilder();
		final Collection<Transition> opIds = state.getOutTransitions();

		sb.append("Operations: \n");
		for (final Transition opId : opIds) {
			sb.append("  " + opId.getId() + ": " + opId.getRep());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * @param state
	 * @return Returns a String representation of the information about the
	 *         state with the specified {@link State}. This includes the id for
	 *         the state, the cached calculated values, and if an invariant
	 *         violation or a timeout has occured for the given state. This is
	 *         mainly useful for console output.
	 */
	public String printState(final State state) {
		final StringBuilder sb = new StringBuilder();

		state.explore();

		sb.append("STATE: " + state + "\n\n");
		sb.append("VALUES:\n");
		Map<IEvalElement, IEvalResult> currentState = state.getValues();
		if (currentState != null) {
			final Set<Entry<IEvalElement, IEvalResult>> entrySet = currentState
					.entrySet();
			for (final Entry<IEvalElement, IEvalResult> entry : entrySet) {
				sb.append("  " + entry.getKey().getCode() + " -> "
						+ entry.getValue().toString() + "\n");
			}
		}
		sb.append("\nINVARIANT: ");
		return sb.toString();
	}

	/**
	 * This calculated the shortest path from root to the specified state. This
	 * contacts the ProB kernel via the {@link GetShortestTraceCommand} and then
	 * uses the generated of operations to generate a Trace via the
	 * {@link StateSpace#getTrace(ITraceDescription)} method.
	 * 
	 * @param stateId
	 *            state id for which the trace through the state space should be
	 *            found.
	 * @return trace in the form of a {@link Trace} object
	 */
	public Trace getTrace(final String stateId) {
		GetShortestTraceCommand cmd = new GetShortestTraceCommand(this, stateId);
		execute(cmd);
		Trace t = getTrace(cmd);
		return t;
	}

	/**
	 * @param sourceId
	 *            of source node
	 * @param destId
	 *            of destination node
	 * @return shortest Trace between the two specified ids (in form of
	 *         {@link Trace} object)
	 */
	public Trace getTrace(final String sourceId, final String destId) {
		FindTraceBetweenNodesCommand cmd = new FindTraceBetweenNodesCommand(
				this, sourceId, destId);
		execute(cmd);
		Trace t = getTrace(cmd);
		return t;
	}

	/**
	 * Takes a list of {@link String} operation id names and generates a
	 * {@link Trace} by executing each one in order. This calls the
	 * {@link Trace#add(String)} method which can throw an
	 * {@link IllegalArgumentException} if executing the operations in the
	 * specified order is not possible.
	 * 
	 * @param transitionIds
	 *            List of transition ids in the order that they should be
	 *            executed.
	 * @return {@link Trace} generated by executing the ids.
	 */
	public Trace getTrace(final List<String> transitionIds) {
		Trace t = new Trace(this);
		for (String id : transitionIds) {
			t = t.add(id);
		}
		return t;
	}

	public Trace getTrace(final ITraceDescription description) {
		return description.getTrace(this);
	}

	/**
	 * Takes an {@link IEvalElement} containing a predicate and returns a
	 * {@link Trace} containing only a magic operation that leads to valid state
	 * where the predicate holds.
	 * 
	 * @param predicate
	 *            predicate that should hold in the valid state
	 * @return {@link Trace} containing a magic operation leading to the state.
	 */
	public Trace getTraceToState(final IEvalElement predicate) {
		FindValidStateCommand cmd = new FindValidStateCommand(this, predicate);
		execute(cmd);
		return getTrace(cmd);
	}

	public void setAnimator(final IAnimator animator) {
		this.animator = animator;
	}

	public AbstractCommand getLoadcmd() {
		return loadcmd;
	}

	public void setLoadcmd(final AbstractCommand loadcmd) {
		this.loadcmd = loadcmd;
	}

	/**
	 * Set the model that is being animated. This should only be set at the
	 * beginning of an animation. The currently supported model types are
	 * {@link ClassicalBModel}, {@link EventBModel}, or {@link CSPModel}. A
	 * StateSpace object always corresponds with exactly one model.
	 * 
	 * @param model
	 */
	public void setModel(final AbstractModel model) {
		this.model = model;
	}

	/**
	 * Returns the specified model for the given StateSpace
	 * 
	 * @return the {@link AbstractModel} that represents the model for the given
	 *         StateSpace instance
	 */
	public AbstractModel getModel() {
		return model;
	}

	/**
	 * This method allows the conversion of the StateSpace to a Model or a
	 * Trace. This corresponds to the Groovy operator "as". The user convert a
	 * StateSpace to an {@link AbstractModel}, {@link EventBModel},
	 * {@link ClassicalBModel}, or {@link CSPModel}. If they specify the class
	 * {@link Trace}, a new Trace object will be created and returned.
	 * 
	 * @param className
	 * @return the Model or Trace corresponding to the StateSpace instance
	 */
	public Object asType(final Class<?> className) {
		if (className.getSimpleName().equals("AbstractModel")) {
			return model;
		}
		if (className.getSimpleName().equals("EventBModel")) {
			if (model instanceof EventBModel) {
				return model;
			}
		}
		if (className.getSimpleName().equals("ClassicalBModel")) {
			if (model instanceof ClassicalBModel) {
				return model;
			}
		}
		if (className.getSimpleName().equals("CSPModel")) {
			if (model instanceof CSPModel) {
				return model;
			}
		}
		if (className.getSimpleName().equals("Trace")) {
			return new Trace(this);
		}
		throw new ClassCastException("An element of class " + className
				+ " was not found");
	}

	/**
	 * This method is implemented to provide access to the {@link State} objects
	 * specified by an integer identifier. This maps to a groovy operator so
	 * that in the console users can type variableOfTypeStateSpace[stateId] and
	 * receive the corresponding StateId back. An IllegalArgumentException is
	 * thrown if the specified id is unknown.
	 * 
	 * @throws IllegalArgumentException
	 * @param that
	 * @return {@link State} for the specified id
	 */
	public Object getAt(final int sId) {
		return getState(sId);
	}

	/**
	 * @param transitions
	 *            to be evaluated
	 * @return a set containing all of the evaluated ops
	 */
	public Set<Transition> evaluateTransitions(
			final Collection<Transition> transitions) {
		GetOpsFromIds cmd = new GetOpsFromIds(transitions);
		execute(cmd);
		return new LinkedHashSet<Transition>(transitions);
	}

	/**
	 * Evaluates all of the formulas for every specified state (if they can be
	 * evaluated). Internally calls {@link #canBeEvaluated(State)}. If the
	 * formulas are of interest to a class (i.e. the an object has subscribed to
	 * the formula) the formula is cached.
	 * 
	 * @param states
	 * @param formulas
	 * @return a map of the formulas and their results for all of the specified
	 *         states
	 */
	public Map<State, Map<IEvalElement, IEvalResult>> evaluateForGivenStates(
			final Collection<State> states, final List<IEvalElement> formulas) {
		Map<State, Map<IEvalElement, IEvalResult>> result = new HashMap<State, Map<IEvalElement, IEvalResult>>();
		List<EvaluationCommand> cmds = new ArrayList<EvaluationCommand>();

		for (State stateId : states) {
			if (stateId.isInitialised()) {
				Map<IEvalElement, IEvalResult> res = new HashMap<IEvalElement, IEvalResult>();
				result.put(stateId, res);

				// Check for cached values
				Map<IEvalElement, IEvalResult> map = stateId.getValues();
				if (map == null) {
					for (IEvalElement f : formulas) {
						cmds.add(f.getCommand(stateId));
					}
				} else {
					for (IEvalElement f : formulas) {
						if (map.containsKey(f)) {
							res.put(f, map.get(f));
						} else {
							cmds.add(f.getCommand(stateId));
						}
					}
				}
			}
		}

		execute(new ComposedCommand(cmds));

		for (EvaluationCommand efCmd : cmds) {
			IEvalElement formula = efCmd.getEvalElement();
			IEvalResult value = efCmd.getValue();
			State id = getState(efCmd.getStateId());
			Map<IEvalElement, IEvalResult> values = id.getValues();

			if (formulaRegistry.containsKey(formula)
					&& !formulaRegistry.get(formula).isEmpty()) {
				values.put(formula, value);
			}
			result.get(id).put(formula, value);
		}

		return result;
	}

	@Override
	public void startTransaction() {
		animator.startTransaction();
	}

	@Override
	public void endTransaction() {
		animator.endTransaction();
	}

	@Override
	public boolean isBusy() {
		return animator.isBusy();
	}

}
