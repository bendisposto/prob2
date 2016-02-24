package de.prob.check;

public interface IModelCheckListener {

	void updateStats(String jobId, long timeElapsed, IModelCheckingResult result,
			StateSpaceStats stats);

	void isFinished(String jobId, long timeElapsed, IModelCheckingResult result,
			StateSpaceStats stats);

}
