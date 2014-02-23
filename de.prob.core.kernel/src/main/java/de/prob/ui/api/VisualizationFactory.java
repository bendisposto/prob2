package de.prob.ui.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.bmotion.BMotionStudioSession;
import de.prob.webconsole.WebConsole;

@Singleton
public class VisualizationFactory {

	private Provider<BMotionStudioSession> sp;

	private Map<ITool, UUID> tools = new HashMap<ITool, UUID>();
	private Map<BMotionStudioSession, UUID> sessions = new HashMap<BMotionStudioSession, UUID>();

	@Inject
	public VisualizationFactory(Provider<BMotionStudioSession> sp) {
		this.sp = sp;
	}

	private final Map<ITool, VisualizationNotifier> listeners = new HashMap<ITool, VisualizationNotifier>();

	public String createURL(ITool tool) {
		UUID id = tools.get(tool);
		if (id == null) {
			id = UUID.randomUUID();
			tools.put(tool, id);
		}

		VisualizationNotifier listener = listeners.get(tool);
		if (listener == null) {
			listener = new VisualizationNotifier();
			// tool.setNotifier(listener);
		}

		BMotionStudioSession bMotionStudioSession = sp.get();
		listener.addSession(bMotionStudioSession);
		UUID sessionId = UUID.randomUUID();
		sessions.put(bMotionStudioSession, sessionId);

		return "http://127.0.0.1:" + WebConsole.getPort()
				+ "/sessions/BMotionStudioSession/" + id + "/" + sessionId;

	}
}
