package de.prob.statespace;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.prob.animator.command.GetOpFromId;
import de.prob.animator.domainobjects.ValueTranslator;
import de.prob.model.representation.AbstractModel;
import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.scripting.CSPModel;

/**
 * Stores the information for a given Operation. This includes operation id
 * (id), operation name (name), the source state (src), and the destination
 * state (dest), as well as a list of parameters.
 * 
 * @author joy
 * 
 */
public class OpInfo {
	public final String id;
	public String name;
	public final String src;
	public final String dest;
	public List<String> params = new ArrayList<String>();
	public String targetState;
	public String rep = null;
	public boolean evaluated;

	Logger logger = LoggerFactory.getLogger(OpInfo.class);

	public OpInfo(final String id, final String src, final String dest) {
		this(id, null, src, dest, new ArrayList<String>(), null);
		evaluated = false;
	}

	/**
	 * The user can specify all of the fields necessary to identify a particular
	 * operation
	 * 
	 * @param id
	 * @param name
	 * @param src
	 * @param dest
	 * @param params
	 * @param targetState
	 */
	public OpInfo(final String id, final String name, final String src,
			final String dest, final List<String> params,
			final String targetState) {
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.targetState = targetState;
		if (params != null) {
			for (String string : params) {
				this.params.add(string);
			}
		}
		evaluated = true;
	}

	/**
	 * The {@link CompoundPrologTerm} that this constructor takes as an argument
	 * should have an arity of 8. The following information should be contained
	 * in the {@link CompoundPrologTerm}:
	 * 
	 * 
	 * ( id , name , src , dest , {@link ListPrologTerm} represenation of
	 * parameters that can be translated to groovy values ,
	 * {@link ListPrologTerm} representation of names of parameters , _ , target
	 * state )
	 * 
	 * @param opTerm
	 *            - a {@link CompoundPrologTerm} which contains all of the
	 *            information about the operation.
	 * 
	 * 
	 */
	public OpInfo(final CompoundPrologTerm opTerm) {
		String id = null, src = null, dest = null;
		id = getIdFromPrologTerm(opTerm.getArgument(1));
		src = getIdFromPrologTerm(opTerm.getArgument(3));
		dest = getIdFromPrologTerm(opTerm.getArgument(4));
		ListPrologTerm parameters = BindingGenerator.getList(opTerm
				.getArgument(5));
		ValueTranslator valueTranslator = new ValueTranslator();
		for (PrologTerm prologTerm : parameters) {
			try {
				Object translated = valueTranslator.toGroovy(prologTerm);
				String retranslated = valueTranslator.asString(translated);
				// System.out.println("T: " + translated.getClass() + " "
				// + translated.toString() + " " + retranslated);
			} catch (IllegalArgumentException e) {
				// Ignore exception for now. Translation is not implemented for
				// CSP
			}
		}

		ListPrologTerm lpt = BindingGenerator.getList(opTerm.getArgument(6));
		for (PrologTerm prologTerm : lpt) {
			params.add(prologTerm.getFunctor());
		}
		targetState = getIdFromPrologTerm(opTerm.getArgument(8));

		this.id = id;
		name = PrologTerm.atomicString(opTerm.getArgument(2));
		this.src = src;
		this.dest = dest;
		evaluated = true;
	}

	public static String getIdFromPrologTerm(final PrologTerm destTerm) {
		if (destTerm instanceof IntegerPrologTerm) {
			return BindingGenerator.getInteger(destTerm).getValue().toString();
		}
		return destTerm.getFunctor();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	public List<String> getParams() {
		return params;
	}

	public String getTargetState() {
		return targetState;
	}

	@Override
	public String toString() {
		return getId() + "=[" + getSrc() + "," + getDest() + "]";
	}

	public String getRep(final AbstractModel m) {
		if (rep == null) {
			rep = generateRep(m);
		}
		return rep;
	}

	private String generateRep(final AbstractModel m) {
		if (!evaluated) {
			ensureEvaluated(m.getStatespace());
		}

		if (m instanceof CSPModel) {
			if (params.isEmpty()) {
				return name;
			}
			return name + "." + Joiner.on(".").join(getParams());
		}
		return name + "(" + Joiner.on(",").join(getParams()) + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof OpInfo) {
			OpInfo that = (OpInfo) obj;
			boolean b = that.getId().equals(id);
			return b;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public boolean isSame(final OpInfo that) {
		if (!that.isEvaluated()) {
			return false;
		}
		return that.getName().equals(name) && that.getParams().equals(params);
	}

	public OpInfo ensureEvaluated(final StateSpace s) {
		if (evaluated) {
			return this;
		}
		GetOpFromId command = new GetOpFromId(getId());
		s.execute(command);
		name = command.getName();
		params = command.getParams();
		targetState = command.getTargetState();
		evaluated = true;
		return this;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public String sha() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(targetState.getBytes());
		return new BigInteger(1, md.digest()).toString(16);
	}

	/**
	 * @return the saved String representation. Can be null if rep has not yet
	 *         been generated. To generate the String rep for a given model use
	 *         {{@link #getRep(AbstractModel)};
	 */
	public String getRep() {
		return rep;
	}

	public void setInfo(final String name, final List<String> params,
			final String targetState) {
		this.name = name;
		this.params = params;
		this.targetState = targetState;
		evaluated = true;
	}
}
