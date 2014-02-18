package de.prob.check;

import de.prob.animator.command.ModelCheckingJob;
import de.prob.statespace.StateSpace;
import de.prob.web.views.ModelCheckingUI;

/**
 * This {@link IModelCheckJob} performs consistency checking on a given
 * {@link StateSpace} based on the specified {@link ModelCheckingOptions}
 * specified by the user or by the default options. This class should be used
 * with the {@link ModelChecker} wrapper class in order to perform model
 * checking. Communications with the ProB kernel take place via the
 * {@link ModelCheckingJob} command.
 * 
 * @author joy
 * 
 */
public class ConsistencyChecker implements IModelCheckJob {

	private final StateSpace s;
	private final String jobId;
	private final ModelCheckingUI ui;
	private final ModelCheckingJob job;

	/**
	 * calls {@link #ConsistencyChecker(StateSpace, ModelCheckingOptions)} with
	 * default model checking options ({@link ModelCheckingOptions#DEFAULT})
	 * 
	 * @param s
	 *            {@link StateSpace} in which to perform the consistency
	 *            checking
	 */
	public ConsistencyChecker(final StateSpace s) {
		this(s, ModelCheckingOptions.DEFAULT);
	}

	/**
	 * calls
	 * {@link #ConsistencyChecker(StateSpace, ModelCheckingOptions, ModelCheckingUI)}
	 * with null for UI
	 * 
	 * @param s
	 *            {@link StateSpace} in which to perform the consistency
	 *            checking
	 * @param options
	 *            {@link ModelCheckingOptions} specified by user
	 */
	public ConsistencyChecker(final StateSpace s,
			final ModelCheckingOptions options) {
		this(s, options, null);
	}

	/**
	 * @param s
	 *            {@link StateSpace} in which to perform the consistency
	 *            checking
	 * @param options
	 *            {@link ModelCheckingOptions} specified by the user
	 * @param ui
	 *            {@link ModelCheckingUI} if the UI should be informed of
	 *            updates. Otherwise, null.
	 */
	public ConsistencyChecker(final StateSpace s,
			final ModelCheckingOptions options, final ModelCheckingUI ui) {
		this.s = s;
		this.ui = ui;
		jobId = ModelChecker.generateJobId();
		job = new ModelCheckingJob(s, options, jobId, ui);
	}

	@Override
	public IModelCheckingResult call() throws Exception {
		long time = System.currentTimeMillis();
		s.execute(job);
		IModelCheckingResult result = job.getResult();
		if (ui != null) {
			ui.isFinished(jobId, System.currentTimeMillis() - time, result,
					job.getStats());
		}
		return result;
	}

	@Override
	public IModelCheckingResult getResult() {
		return job.getResult();
	}

	@Override
	public String getJobId() {
		return jobId;
	}

	@Override
	public StateSpace getStateSpace() {
		return s;
	}

	/**
	 * Provides a way to generate a {@link ModelChecker} with consistency
	 * checking capabilities given a {@link StateSpace}. Default options will be
	 * used.
	 * 
	 * @param s
	 *            {@link StateSpace} for which the consistency checking should
	 *            take place
	 * @return {@link ModelChecker} with consistency checking capabilities.
	 */
	public static ModelChecker create(final StateSpace s) {
		return new ModelChecker(new ConsistencyChecker(s));
	}

	/**
	 * Provides a way to generate a {@link ModelChecker} with consistency
	 * checking capabilities given a {@link StateSpace} and user defined
	 * {@link ModelCheckingOptions}.
	 * 
	 * @param s
	 *            {@link StateSpace} for which the consistency checking should
	 *            take place
	 * @param options
	 *            {@link ModelCheckingOptions} specified by the user
	 * @return {@link ModelChecker} with consistency checking capabilities
	 */
	public static ModelChecker create(final StateSpace s,
			final ModelCheckingOptions options) {
		return new ModelChecker(new ConsistencyChecker(s, options));
	}

}
