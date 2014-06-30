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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void traceChange(Trace currentTrace, boolean currentAnimationChanged) {
		String traceId = currentTrace.getUUID().toString();
		String component = currentTrace.getModel().getMainComponent()
				.toString();

		List<String> cpath = Arrays.asList(new String[] { "current-animation",
				"component" });
		List<String> upath = Arrays.asList(new String[] { "current-animation",
				"uuid" });

		ArrayList tc = new ArrayList();
		tc.add(cpath);
		tc.add(component);

		ArrayList tu = new ArrayList();
		tu.add(upath);
		tu.add(traceId);

		List<String> path1 = Arrays.asList(new String[] { traceId, "past" });

		TraceElement element = currentTrace.getHead();
		TraceElement current = currentTrace.getCurrent();

		ArrayList past = new ArrayList();
		ArrayList future = new ArrayList();

		List selected = future;

		while (element.getPrevious() != null) {
			OpInfo op = element.getOp();
			String rep = op.getRep(currentTrace.getModel());
			if (element == current) {
				selected = past;
			}
			selected.add(rep);
			element = element.getPrevious();
		}

		List<String> path2 = Arrays.asList(new String[] { traceId, "future" });

		ArrayList t1 = new ArrayList();
		t1.add(path1);
		t1.add(past);
		ArrayList t2 = new ArrayList();
		t2.add(path2);
		t2.add(future);

		List tt = new ArrayList();
		tt.add(t1);
		tt.add(t2);
		tt.add(tc);
		tt.add(tu);

		UIState.transact(tt);

	}

	@Override
	public void animatorStatus(boolean busy) {

	}
}
