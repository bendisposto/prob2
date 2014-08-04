package de.prob.statespace;

/**
 * Classes that implement this interface have the inherent property of being
 * able to generate a Trace through a specified {@link StateSpace} object. This
 * can be calculated either by calling the
 * {@link ITraceDescription#getTrace(StateSpace)} method or by calling
 * {@link StateSpace#getTrace(ITraceDescription)}.
 * 
 * @author joy
 * 
 */
public interface ITraceDescription {

	/**
	 * Generates a {@link Trace} through the {@link StateSpace}. May call the
	 * {@link StateSpace#getTrace(java.util.List)} or
	 * {@link StateSpace#getTrace(StateId)} methods in order to generate the
	 * trace. However, it MUST NOT call the
	 * {@link StateSpace#getTrace(ITraceDescription)} method in the
	 * {@link StateSpace}, because that method calls this method in this class
	 * and that will cause an infinite loop to take place.
	 * 
	 * @param s
	 *            {@link StateSpace} for which this trace should be generated
	 * @return {@link Trace} through the {@link StateSpace}
	 * @throws RuntimeException
	 *             when the class is not able to create the specified
	 *             {@link Trace}
	 */
	Trace getTrace(StateSpace s) throws RuntimeException;
}
