package de.prob.ui.api;

import java.util.HashSet;
import java.util.Set;

import de.prob.bmotion.BMotionStudioSession;

public class VisualizationNotifier {

	Set<BMotionStudioSession> session = new HashSet<BMotionStudioSession>();

	public void addSession(BMotionStudioSession v) {
		session.add(v);
	}

	public void notifyStateChange(Object newStateRef) {
		for (BMotionStudioSession v : session) {
			// v.stateChange(newStateRef);
		}
	}

}
