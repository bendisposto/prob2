package de.prob.check;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import de.prob.animator.command.AbstractCommand;
import de.prob.statespace.StateSpace;

/**
 * Classes implementing {@link IModelCheckJob} are responsible for taking user
 * input and performing some sort of model checking thereby producing an
 * IModelCheckingResult. This is encapsulated using the {@link Callable}
 * interface in order to be able to use the executor framework and
 * {@link Future}s and in order to be able to cancel the calculation. However,
 * even if the calculation is cancelled, the calculation that takes place
 * (probably in an {@link AbstractCommand} of some sort) should still produce a
 * result (likely a {@link NotYetFinished} result). The user should still be
 * able to get the result from the {@link IModelCheckJob} object, so the
 * additional method {@link IModelCheckJob#getResult()} is provided to allow the
 * {@link ModelChecker} to access the result even in the case of cancellation.
 * 
 * @author joy
 * 
 */
public interface IModelCheckJob extends Callable<IModelCheckingResult> {

	/**
	 * This method should return a result even in the case of cancellation. In
	 * the case of cancellation, the result is most likely
	 * {@link NotYetFinished}.
	 * 
	 * @return result of the calculation
	 */
	IModelCheckingResult getResult();

	/**
	 * The job id here should be unique. In implementations, a unique job id can
	 * be generated via the static method {@link ModelChecker#generateJobId()}.
	 * 
	 * @return job id associated with this job.
	 */
	String getJobId();

	/**
	 * In the creation of an {@link IModelCheckJob}, the corresponding
	 * {@link StateSpace} for the job should be specified. This method allows
	 * the {@link ModelChecker} to access the correct {@link StateSpace} so that
	 * an interrupt can be sent in the case of a cancellation.
	 * 
	 * @return the {@link StateSpace} associated with this job.
	 */
	StateSpace getStateSpace();
}
