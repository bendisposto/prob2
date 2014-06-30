package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;
import de.prob.statespace.TraceElement;
import de.prob.sync.UIState;

@Singleton
public class DataServlet extends HttpServlet implements
IAnimationChangeListener {

	private static final long serialVersionUID = -6568158351553781071L;
	private final AnimationSelector animations;

	@Inject
	public DataServlet(AnimationSelector animations) {
		this.animations = animations;
		animations.registerAnimationChangeListener(this);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		String state = req.getParameter("state");
		String delta = UIState.delta(state);
		resp.setContentType("text/edn");
		PrintWriter writer = resp.getWriter();
		writer.write(delta);
		writer.flush();
		writer.close();
	}

	@Override
	public void traceChange(Trace currentTrace, boolean currentAnimationChanged) {
		String traceId = currentTrace.getUUID().toString();
		String component = currentTrace.getModel().getMainComponent()
				.toString();

		def tt = []

		tt << [
			[
				"current-animation",
				"component"
			],
			component
		]
		tt << [
			["current-animation", "uuid"],
			traceId
		]

		TraceElement element = currentTrace.getHead();
		TraceElement current = currentTrace.getCurrent();

		boolean past = false;

		while (element.getPrevious() != null) {
			OpInfo op = element.getOp();
			String rep = op.getRep(currentTrace.getModel());
			if (element == current) {
				tt << [
					[
						traceId,
						element.index.toString(),
						"type"
					],
					"current"
				]
				past = true;
			}
			else {
				tt << [
					[
						traceId,
						element.index.toString(),
						"type"
					],
					past?"past":"future"
				]
			}
			tt << [
				[
					traceId,
					element.index.toString(),
					"name"
				],
				rep
			]
			element = element.getPrevious();
		}

		UIState.transact(tt);
	}

	@Override
	public void animatorStatus(boolean busy) {
	}
}
