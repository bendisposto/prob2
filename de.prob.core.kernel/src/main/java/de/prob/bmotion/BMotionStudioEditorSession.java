package de.prob.bmotion;

import com.google.inject.Inject;

public class BMotionStudioEditorSession extends AbstractBMotionStudioSession {

	private boolean initEditor = false;
	
	@Inject
	public BMotionStudioEditorSession() {
	}

	public boolean isInitEditor() {
		return initEditor;
	}

	public void setInitEditor(boolean initEditor) {
		this.initEditor = initEditor;
	}

}