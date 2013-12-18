package de.prob.web.views;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelChecker;
import de.prob.check.ModelCheckingOptions;
import de.prob.check.ModelCheckingOptions.Options;
import de.prob.model.eventb.Context;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Machine;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class ModelCheckingUI extends AbstractSession {

	private ModelCheckingOptions options;

	private ModelChecker checker;

	private final AnimationSelector animations;

	Map<StateSpace, List<ModelChecker>> queued = new HashMap<StateSpace, List<ModelChecker>>();

	@Inject
	public ModelCheckingUI(final AnimationSelector animations) {
		this.animations = animations;
		options = ModelCheckingOptions.DEFAULT;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/modelchecking/index.html");
	}

	public void updateStats(final String id, final IModelCheckingResult result) {

	}

	public void isFinished(final String id, final IModelCheckingResult res) {

	}

	public Object startJob(final Map<String, String[]> params) {
		String stateSpaceId = get(params, "ssId");
		StateSpace space = findStateSpace(stateSpaceId);

		if (space != null) {

		} else {
			// FIXME handle error
		}

		if (checker != null) {
			checker.start();
		}
		return null;
	}

	private StateSpace findStateSpace(final String id) {
		List<Trace> traces = animations.getTraces();
		for (Trace trace : traces) {
			StateSpace stateSpace = trace.getStateSpace();
			if (stateSpace.getId().equals(id)) {
				return stateSpace;
			}
		}
		return null;
	}

	public Object requestNewJob(final Map<String, String[]> params) {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("cmd", "ModelChecking.updateOptions");

		// extract options
		EnumSet<Options> allOf = EnumSet.allOf(Options.class);
		for (Options o : allOf) {
			res.put(o.name(), options.getPrologOptions().contains(o));
		}

		// extract state spaces
		List<StateSpace> spaces = new ArrayList<StateSpace>();
		List<Trace> traces = animations.getTraces();
		for (Trace trace : traces) {
			if (!spaces.contains(trace.getStateSpace())) {
				spaces.add(trace.getStateSpace());
			}
		}
		List<Object> sss = new ArrayList<Object>();
		for (StateSpace space : spaces) {
			String rep = "";
			AbstractElement comp = space.getModel().getMainComponent();
			if (comp instanceof Machine || comp instanceof Context) {
				rep = comp instanceof Machine ? ((Machine) comp).getName()
						: ((Context) comp).getName();
			}
			rep += " " + space.toString();
			sss.add(WebUtils.wrap("rep", rep, "id", space.getId()));
		}
		res.put("spaces", sss);
		return res;
	}

	// SET MODEL CHECKING OPTIONS

	public Object breadthFirst(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(get(params, "set"));
		options = options.breadthFirst(isSet);
		return null;
	}

	public Object checkDeadlocks(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(get(params, "set"));
		options = options.checkDeadlocks(isSet);
		return null;
	}

	public Object checkInvariantViolations(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(get(params, "set"));
		options = options.checkInvariantViolations(isSet);
		return null;
	}

	public Object checkAssertions(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(get(params, "set"));
		options = options.checkAssertions(isSet);
		return null;
	}

	public Object recheckExisting(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(get(params, "set"));
		options = options.recheckExisting(isSet);
		return null;
	}

	public Object stopAtFullCoverage(final Map<String, String[]> params) {
		boolean isSet = Boolean.valueOf(get(params, "set"));
		options = options.stopAtFullCoverage(isSet);
		return null;
	}

}
