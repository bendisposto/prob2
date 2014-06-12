package de.prob.bmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import de.prob.ui.api.ITool;
import de.prob.ui.api.IToolListener;
import de.prob.ui.api.IllegalFormulaException;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.web.AbstractSession;

public class BMSSession extends AbstractSession implements IToolListener {

	private final ITool tool;
	private final List<String> formulasForEvaluating = new ArrayList<String>();

	public BMSSession(final ITool tool) {
		this.tool = tool;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void animationChange(final ITool tool) {
		if (this.tool.equals(tool)) {

		}

	}

	public String eval(final String formula) {
		if (tool.getErrors(tool.getCurrentState(), formula).isEmpty()) {
			try {
				return tool.evaluate(tool.getCurrentState(), formula);
			} catch (IllegalFormulaException e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public Object executeOperation(final Map<String, String[]> params) {
		String[] stateref = params.get("state");
		String[] event = params.get("name");
		String[] parameters = params.get("params");

		try {
			tool.doStep(stateref[0], event[0], parameters);
		} catch (ImpossibleStepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		// TODO Auto-generated method stub

	}

}
