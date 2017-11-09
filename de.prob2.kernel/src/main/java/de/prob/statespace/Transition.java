package de.prob.statespace;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import de.be4.classicalb.core.parser.exceptions.BCompoundException;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventParameter;
import de.prob.model.representation.AbstractElement;
import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.translator.Translator;
import de.prob.translator.types.BObject;
import de.prob.model.classicalb.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Stores the information for a given Operation. This includes operation id
 * (id), operation name (name), the source state (src), and the destination
 * state (dest), as well as a list of parameters.</p>
 * 
 * <p>Note: This class retains a reference to the {@link StateSpace} object to
 * which it belongs. In order to ensure that the garbage collector works
 * correctly when cleaning up a {@link StateSpace} object make sure that all
 * {@link Transition} objects are correctly dereferenced.</p>
 * 
 * @author joy 
 */
public class Transition {
	public final StateSpace stateSpace;

	private final String id;
	private final String name;
	private final State src;
	private final State dest;
	private List<String> params;
	private List<String> returnValues;
	private List<BObject> translatedParams;
	private List<BObject> translatedRetV;
	private String rep;
	private boolean evaluated;
	private FormulaExpand formulaExpansion;
	private final FormalismType formalismType;
	private String predicateString;

	Logger logger = LoggerFactory.getLogger(Transition.class);

	private Transition(final StateSpace stateSpace, final String id,
			final String name, final State src, final State dest) {
		this.stateSpace = stateSpace;
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.evaluated = false;
		this.rep = name;
		formalismType = stateSpace.getModel().getFormalismType();
	}

	/**
	 * @return String identifier associated with this Operation
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return name of this operation
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the {@link State} reference to the source node of this transition
	 */
	public State getSource() {
		return src;
	}

	/**
	 * @return the {@link State} reference to the destination node of this
	 *         operation
	 */
	public State getDestination() {
		return dest;
	}

	/**
	 * The list of parameters is not filled by default. If the parameter list
	 * has not yet been filled, ProB will be contacted to lazily fill the
	 * parameter list via the {@link #evaluate()} method.
	 * 
	 * @return list of values for the parameters represented as strings
	 */
	public List<String> getParams() {
		if (!evaluated) {
			evaluate();
		}
		return params;
	}

	public List<BObject> getTranslatedParams() throws BCompoundException {
		if (translatedParams != null) {
			return translatedParams;
		}
		translateParamsAndRetVals();
		return translatedParams;
	}

	private void translateParamsAndRetVals() throws BCompoundException {
		if (!evaluated || formulaExpansion != FormulaExpand.EXPAND) {
			evaluate(FormulaExpand.EXPAND);
		}
		translatedParams = new ArrayList<BObject>();
		for (String str : params) {
			translatedParams.add(Translator.translate(str));
		}
		translatedRetV = new ArrayList<BObject>();
		for (String str : returnValues) {
			translatedRetV.add(Translator.translate(str));
		}
	}

	/**
	 * The list of return values is not filled by default. If the return value
	 * list has not yet been filled, ProB will be contacted to lazily fill the
	 * list via the {@link #evaluate()} method.
	 * 
	 * @return list of return values of the operation represented as strings.
	 */
	public List<String> getReturnValues() {
		if (!evaluated) {
			evaluate();
		}
		return returnValues;
	}

	public List<BObject> getTranslatedReturnValues() throws BCompoundException {
		if (translatedRetV != null) {
			return translatedRetV;
		}
		translateParamsAndRetVals();
		return translatedRetV;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the String representation of the operation.
	 */
	public String getRep() {
		return rep;
	}

	/**
	 * Uses {@link #getParameterPredicates()} to calculate a string predicate
	 * representing the value of the parameters for this transition.
	 * 
	 * @return the {@link String} predicate that represents the value of the
	 *         parameters for this transition.
	 */
	public String getParameterPredicate() {
		if (predicateString != null) {
			return predicateString;
		}
		List<String> params = getParameterPredicates();
		predicateString = params.isEmpty() ? "TRUE=TRUE" : Joiner.on(" & ")
				.join(params);
		return predicateString;
	}

	/**
	 * @return a list of string predicates representing the value of the
	 *         parameters for this transition
	 */
	public List<String> getParameterPredicates() {
		if (isArtificialTransition()) {
			return Collections.emptyList();
		}
		evaluate();
		List<String> predicates = new ArrayList<String>();
		AbstractElement mainComponent = stateSpace.getMainComponent();
		List<String> params = new ArrayList<String>();
		if (mainComponent instanceof ClassicalBMachine) {
			Operation op = ((ClassicalBMachine) mainComponent)
					.getOperation(getName());
			params = op.getParameters();
		} else if (mainComponent instanceof EventBMachine) {
			Event event = ((EventBMachine) mainComponent).getEvent(getName());
			for (EventParameter eventParameter : event.getParameters()) {
				params.add(eventParameter.getName());
			}
		}
		if (params.size() == this.params.size()) {
			for (int i = 0; i < params.size(); i++) {
				predicates.add(params.get(i) + " = " + this.params.get(i));
			}
		}

		return predicates;
	}

	private String createRep(final String name, final List<String> params,
			final List<String> returnVals) {
		if (formalismType.equals(FormalismType.CSP)) {
			if (params.isEmpty()) {
				return name;
			}
			return name + "." + Joiner.on(".").join(params);
		}
		String retVals = returnVals.isEmpty() ? "" : Joiner.on(",").join(
				returnVals)
				+ " <-- ";
		return retVals + name + "(" + Joiner.on(",").join(params) + ")";
	}

	public String getPrettyRep() {
		String rep = getRep();
		if (name.equals("$initialise_machine")) {
			rep = rep.replaceAll("\\$initialise_machine", "INITIALISATION");
		}
		if (name.equals("$setup_constants")) {
			rep = rep.replaceAll("\\$setup_constants", "SETUP_CONSTANTS");
		}
		return rep;
	}

	public boolean isArtificialTransition() {
		return name.equals("$initialise_machine")
				|| name.equals("$setup_constants");
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Transition) {
			Transition that = (Transition) obj;
			return getId().equals(that.getId())
					&& getSource().equals(that.getSource())
					&& getDestination().equals(that.getDestination());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId(), getSource().getId(), getDestination()
				.getId());
	}

	/**
	 * @param that
	 *            {@link Transition} with which this {@link Transition} should
	 *            be compared
	 * @return if the name and parameters of the {@link Transition}s are
	 *         equivalent
	 */
	public boolean isSame(final Transition that) {
		evaluate();
		that.evaluate();
		return that.getName().equals(name) && that.getParams().equals(params);
	}

	/**
	 * The {@link Transition} is checked to see if the name, parameters, and
	 * return values have been retrieved from ProB yet. If not, the retrieval
	 * takes place via the {@link GetOpFromId} command and the missing values
	 * are set.
	 * 
	 * @return {@code this}
	 */
	public Transition evaluate() {
		return evaluate(FormulaExpand.TRUNCATE);
	}

	public boolean canBeEvaluated(final FormulaExpand expansion) {
		if (!evaluated) {
			return true;
		}
		if (this.formulaExpansion == FormulaExpand.TRUNCATE
				&& expansion == FormulaExpand.EXPAND) {
			return true;
		}
		return false;
	}

	public Transition evaluate(final FormulaExpand expansion) {
		if (canBeEvaluated(expansion)) {
			GetOpFromId command = new GetOpFromId(this, expansion);
			stateSpace.execute(command);
			return this;
		}
		return this;
	}

	/**
	 * Check whether this transition has been evaluated.
	 * @return whether or not the name, parameters, and return values have yet
	 *         been retrieved from ProB
	 */
	public boolean isEvaluated() {
		return evaluated;
	}

	public boolean isTruncated() {
		return formulaExpansion == FormulaExpand.TRUNCATE;
	}

	/**
	 * Calls {@link State#getStateRep} to calculate the SHA-1 of the destination
	 * state.
	 * 
	 * @return A SHA-1 hash of the target state in String format.
	 * @throws NoSuchAlgorithmException if no SHA-1 provider is found
	 */
	public String sha() throws NoSuchAlgorithmException {
		evaluate();
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(getDestination().getStateRep().getBytes());
		return new BigInteger(1, md.digest()).toString(Character.MAX_RADIX);
	}

	/**
	 * Sets the values for the fields in this class. This should ONLY be called
	 * by the {@link GetOpFromId} command during retrieval of the values from
	 * Prolog. For this reason, it is package private
	 * 
	 * @param params
	 *            - {@link List} of {@link String} parameters
	 * @param returnValues
	 *            - {@link List} of {@link String} return values
	 */
	void setInfo(final FormulaExpand expansion, final List<String> params,
			final List<String> returnValues) {
		this.formulaExpansion = expansion;
		this.params = params;
		this.returnValues = returnValues;
		this.rep = createRep(name, params, returnValues);
		evaluated = true;
	}

	/**
	 * Creates an artificial transition that is to be added to the
	 * {@link StateSpace}.
	 * 
	 * @param s
	 *            {@link StateSpace} object to which this operation belongs
	 * @param transId
	 *            String operation id
	 * @param description
	 *            String description of the operation
	 * @param srcId
	 *            String id of source node
	 * @param destId
	 *            String id of destination node
	 * @return {@link Transition} representation of given information
	 */
	public static Transition generateArtificialTransition(final StateSpace s,
			final String transId, final String description, final String srcId,
			final String destId) {
		return new Transition(s, transId, description, s.addState(srcId),
				s.addState(destId));
	}

	/**
	 * @param s
	 *            StateSpace object to which this operation belongs
	 * @param cpt
	 *            {@link CompoundPrologTerm} representation of the operation
	 *            which contains the transition id, source id, and destination
	 *            id
	 * @return {@link Transition} object representing the information about the
	 *         operation
	 */
	public static Transition createTransitionFromCompoundPrologTerm(
			final StateSpace s, final CompoundPrologTerm cpt) {
		String opId = Transition.getIdFromPrologTerm(cpt.getArgument(1));
		String name = BindingGenerator.getCompoundTerm(cpt.getArgument(2), 0).getFunctor().intern();
		String srcId = Transition.getIdFromPrologTerm(cpt.getArgument(3));
		String destId = Transition.getIdFromPrologTerm(cpt.getArgument(4));
		return new Transition(s, opId, name, s.addState(srcId),
				s.addState(destId));
	}

	/**
	 * Takes a {@link PrologTerm} representation of a transition id and
	 * translates it to a string value.
	 * 
	 * @param destTerm
	 *            {@link PrologTerm} representing the Transition Id
	 * @return String representation of the Transition Id
	 */
	public static String getIdFromPrologTerm(final PrologTerm destTerm) {
		if (destTerm instanceof IntegerPrologTerm) {
			return BindingGenerator.getInteger(destTerm).getValue().toString();
		}
		return destTerm.getFunctor();
	}
}
