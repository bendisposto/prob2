package de.prob.statespace;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.animator.command.GetOpFromId;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.Operation;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventParameter;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Stores the information for a given Operation. This includes operation id
 * (id), operation name (name), the source state (src), and the destination
 * state (dest), as well as a list of parameters. </br></br> Note: This class
 * retains a reference to the StateSpace object to which it belongs. In order to
 * ensure that the garbage collector works correctly when cleaning up a
 * StateSpace object make sure that all OpInfo objects are correctly
 * dereferenced.
 * 
 * @author joy
 * 
 */
public class OpInfo {
	public StateSpace stateSpace;

	private final String id;
	private String name;
	private final StateId src;
	private final StateId dest;
	private List<String> params = new ArrayList<String>();
	private List<String> returnValues = new ArrayList<String>();
	private String targetState;
	private String rep = null;
	private boolean evaluated;
	private final FormalismType formalismType;
	private String predicateString;

	Logger logger = LoggerFactory.getLogger(OpInfo.class);

	private OpInfo(final StateSpace stateSpace, final String id,
			final StateId src, final StateId dest) {
		this.stateSpace = stateSpace;
		this.id = id;
		this.src = src;
		this.dest = dest;
		formalismType = stateSpace.getModel().getFormalismType();
		evaluated = false;
	}

	private OpInfo(final StateSpace stateSpace, final String id,
			final String name, final StateId src, final StateId dest) {
		this.stateSpace = stateSpace;
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		formalismType = stateSpace.getModel().getFormalismType();
		params = Collections.emptyList();
		targetState = "";
		evaluated = true;
		rep = name;
	}

	/**
	 * @return String identifier associated with this Operation
	 */
	public String getId() {
		return id;
	}

	/**
	 * The name field is not by default filled. To ensure that the name,
	 * parameter, and return value information have been retrieved from ProB,
	 * use the {@link #evaluate()} method.
	 * 
	 * @return name of this operation
	 */
	public String getName() {
		if (!evaluated) {
			evaluate();
		}
		return name;
	}

	/**
	 * {@link Deprecated}. Use {@link #getSrcId()} and access the String id via
	 * {@link StateId#getId()} instead.
	 * 
	 * @return String id of source node
	 */
	@Deprecated
	public String getSrc() {
		return src.getId();
	}

	/**
	 * {@link Deprecated}. Use {@link #getDestId()} and access the String id via
	 * {@link StateId#getId()}.
	 * 
	 * @return String id of destination node.
	 */
	@Deprecated
	public String getDest() {
		return dest.getId();
	}

	/**
	 * @return the {@link StateId} reference to the source node of this
	 *         operation
	 */
	public StateId getSrcId() {
		return src;
	}

	/**
	 * @return the {@link StateId} reference to the destination node of this
	 *         operation
	 */
	public StateId getDestId() {
		return dest;
	}

	/**
	 * The list of parameters is not filled by default. To ensure that the name,
	 * parameter, and return value information have been retrieved from ProB,
	 * use the {@link #evaluate()} method.
	 * 
	 * @return list of values for the parameters represented as strings
	 */
	public List<String> getParams() {
		if (!evaluated) {
			evaluate();
		}
		return params;
	}

	/**
	 * The list of return values is not filled by default. To ensure that the
	 * name, parameter, and return value information have been retrieved from
	 * ProB, use the {@link #evaluate()} method.
	 * 
	 * @return list of return values of the operation represented as strings.
	 */
	public List<String> getReturnValues() {
		if (!evaluated) {
			evaluate();
		}
		return returnValues;
	}

	/**
	 * @return a String representation of the target state
	 */
	public String getTargetState() {
		if (!evaluated) {
			evaluate();
		}
		return targetState;
	}

	@Override
	public String toString() {
		return getId() + "=[" + getSrcId().getId() + "," + getDestId().getId()
				+ "]";
	}

	/**
	 * This is {@link Deprecated}. Use {@link #getRep()} instead.
	 * 
	 * @param m
	 *            Abstract Model
	 * @return string representation
	 */
	@Deprecated
	public String getRep(final AbstractModel m) {
		if (rep == null) {
			rep = generateRep();
		}
		return rep;
	}

	/**
	 * @return the String representation of the operation.
	 */
	public String getRep() {
		if (rep == null) {
			rep = generateRep();
		}
		return rep;
	}

	public String getParameterPredicate() {
		if (predicateString != null) {
			return predicateString;
		}
		predicateString = Joiner.on(" & ").join(getParameterPredicates());
		return predicateString;
	}

	public List<String> getParameterPredicates() {
		evaluate();
		List<String> predicates = new ArrayList<String>();
		AbstractElement mainComponent = stateSpace.getModel()
				.getMainComponent();
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

	/**
	 * The string representation of the operation is calculated based on the
	 * name, parameters, return values, and the formalism type in question
	 * {@link FormalismType#CSP} or {@link FormalismType#B}. If the operation is
	 * not yet evaluated (the values for name, parameters, and return values
	 * have not yet been retrieved), this is done via {@link #evaluate()}.
	 * 
	 * @return a String represenation of the operation
	 */
	private String generateRep() {
		evaluate();

		if (formalismType.equals(FormalismType.CSP)) {
			if (params.isEmpty()) {
				return name;
			}
			return name + "." + Joiner.on(".").join(getParams());
		}
		String retVals = getReturnValues().isEmpty() ? "" : Joiner.on(",")
				.join(getReturnValues()) + " <-- ";
		return retVals + name + "(" + Joiner.on(",").join(getParams()) + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof OpInfo) {
			OpInfo that = (OpInfo) obj;
			return this.getId().equals(that.getId())
					&& this.getSrcId().equals(that.getSrcId())
					&& this.getDestId().equals(that.getDestId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 7).append(getId()).append(getSrc())
				.append(getDest()).toHashCode();
	}

	/**
	 * @param that
	 *            {@link OpInfo} with which this {@link OpInfo} should be
	 *            compared
	 * @return if the name and parameters of the {@link OpInfo}s are equivalent
	 */
	public boolean isSame(final OpInfo that) {
		if (!that.isEvaluated()) {
			return false;
		}
		return that.getName().equals(name) && that.getParams().equals(params);
	}

	/**
	 * {@link Deprecated}. Use {@link #evaluate()} instead.
	 * 
	 * @param s
	 *            - StateSpace
	 * @return OpInfo that has been evaluated.
	 */
	@Deprecated
	public OpInfo ensureEvaluated(final StateSpace s) {
		return evaluate();
	}

	/**
	 * The {@link OpInfo} is checked to see if the name, parameters, and return
	 * values have been retrieved from ProB yet. If not, the retrieval takes
	 * place via the {@link GetOpFromId} command and the missing values are set.
	 * 
	 * @return
	 */
	public OpInfo evaluate() {
		if (evaluated) {
			return this;
		}
		GetOpFromId command = new GetOpFromId(this);
		stateSpace.execute(command);
		return this;
	}

	/**
	 * @return whether or not the name, parameters, and return values have yet
	 *         been retrieved from ProB
	 */
	public boolean isEvaluated() {
		return evaluated;
	}

	/**
	 * @return A SHA-1 hash of the target state in String format
	 * @throws NoSuchAlgorithmException
	 */
	public String sha() throws NoSuchAlgorithmException {
		evaluate();
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(targetState.getBytes());
		return new BigInteger(1, md.digest()).toString(Character.MAX_RADIX);
	}

	/**
	 * Sets the values for the fields in this class. This should ONLY be called
	 * by the {@link GetOpFromId} command during retrieval of the values from
	 * Prolog. For this reason it has been marked as deprecated.
	 * 
	 * @param name
	 *            - Name of the operation
	 * @param params
	 *            - {@link List} of {@link String} parameters
	 * @param returnValues
	 *            - {@link List} of {@link String} return values
	 * @param paramsSource
	 *            - {@link List} of {@link EvalResult} parameters
	 * @param retValSource
	 *            - {@link List} of {@link EvalResult} return values
	 * @param targetState
	 *            - String representation of target state
	 */
	@Deprecated
	public void setInfo(final String name, final List<String> params,
			final List<String> returnValues, final String targetState) {
		this.name = name;
		this.params = params;
		this.targetState = targetState;
		this.returnValues = returnValues;
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
	 * @return OpInfo representation of given information
	 */
	public static OpInfo generateArtificialTransition(final StateSpace s,
			final String transId, final String description, final String srcId,
			final String destId) {
		return new OpInfo(s, transId, description, s.addState(srcId),
				s.addState(destId));
	}

	/**
	 * @param s
	 *            StateSpace object to which this operation belongs
	 * @param cpt
	 *            {@link CompoundPrologTerm} representation of the operation
	 *            which contains the transition id, source id, and destination
	 *            id
	 * @return {@link OpInfo} object representing the information about the
	 *         operation
	 */
	public static OpInfo createOpInfoFromCompoundPrologTerm(final StateSpace s,
			final CompoundPrologTerm cpt) {
		String opId = OpInfo.getIdFromPrologTerm(cpt.getArgument(1));
		String srcId = OpInfo.getIdFromPrologTerm(cpt.getArgument(2));
		String destId = OpInfo.getIdFromPrologTerm(cpt.getArgument(3));
		return new OpInfo(s, opId, s.addState(srcId), s.addState(destId));
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
