package de.prob.bmotion;

import java.util.UUID;

import de.prob.ui.api.ITool;


public class BMotionStudioEditorSession extends AbstractBMotionStudioSession {

	public BMotionStudioEditorSession(UUID id, ITool tool, String templatePath,
			final String host, final int port) {
		super(id, tool, templatePath, host, port);
	}

	@Override
	public void initSession() {
	}

}
