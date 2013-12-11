package de.prob.web.views;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.check.StateSpaceStats;
import de.prob.statespace.AnimationSelector;
import de.prob.web.AbstractSession;

@Singleton
public class ModelCheckingUI extends AbstractSession {

	@Inject
	public ModelCheckingUI(final AnimationSelector selector) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateStats(final StateSpaceStats stats) {

	}

	public void isFinished() {

	}

}
