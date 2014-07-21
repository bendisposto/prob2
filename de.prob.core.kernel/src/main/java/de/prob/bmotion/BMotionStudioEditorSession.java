package de.prob.bmotion;

import de.prob.model.representation.AbstractModel;

public class BMotionStudioEditorSession extends AbstractBMotionStudioSession {

	public BMotionStudioEditorSession(String templatePath, AbstractModel model,
			final String host, final int port) {
		super(templatePath, model, host, port);
	}

	@Override
	public void initSession() {
	}

}
