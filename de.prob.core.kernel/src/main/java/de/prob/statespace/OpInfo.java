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
import de.prob.model.representation.AbstractModel;
import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Stores the information for a given Operation. This includes operation id
 * (id), operation name (name), the source state (src), and the destination
 * state (dest), as well as a list of parameters.
 * 
 * @author joy
 * 
 */
public class OpInfo {
	private final String id;
	private String name;
	private final String src;
	private final String dest;
	private List<String> params = new ArrayList<String>();
	private String targetState;
	private String rep = null;
	private boolean evaluated;

	Logger logger = LoggerFactory.getLogger(OpInfo.class);

	private OpInfo(final String id, final String src, final String dest) {
		this.id = id;
		this.src = src;
		this.dest = dest;
		evaluated = false;
	}

	private OpInfo(final String id, final String name, final String src,
			final String dest) {
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.params = Collections.emptyList();
		this.targetState = "";
		this.evaluated = true;
		this.rep = name;
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

		if (m.getFormalismType().equals(FormalismType.CSP)) {
			if (params.isEmpty()) {
				return name;
			}
			return name + "." + Joiner.on(".").join(getParams());
		}
		return name + "(" + Joiner.on(",").join(getParams()) + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof OpInfo) {
			OpInfo that = (OpInfo) obj;
			return this.getId().equals(that.getId())
					&& this.getSrc().equals(that.getSrc())
					&& this.getDest().equals(that.getDest());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 7).append(getId()).append(getSrc())
				.append(getDest()).toHashCode();
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
		GetOpFromId command = new GetOpFromId(this);
		s.execute(command);
		return this;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public String sha() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(targetState.getBytes());
		return new BigInteger(1, md.digest()).toString(Character.MAX_RADIX);
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

	public static OpInfo generateArtificialTransition(final String transId,
			final String description, final String srcId, final String destId) {
		return new OpInfo(transId, description, srcId, destId);
	}

	public static OpInfo createOpInfoFromCompoundPrologTerm(
			final CompoundPrologTerm cpt) {
		return new OpInfo(OpInfo.getIdFromPrologTerm(cpt.getArgument(1)),
				OpInfo.getIdFromPrologTerm(cpt.getArgument(2)),
				OpInfo.getIdFromPrologTerm(cpt.getArgument(3)));
	}
}
