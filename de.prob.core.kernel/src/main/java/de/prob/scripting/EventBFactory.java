package de.prob.scripting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eventb.emf.core.CorePackage;
import org.eventb.emf.core.Project;

import com.google.inject.Provider;

import de.prob.model.eventb.EventBModel;

public class EventBFactory {

	private final Provider<EventBModel> modelProvider;

	public EventBFactory(final Provider<EventBModel> modelProvider) {
		this.modelProvider = modelProvider;
	}

	CorePackage f = CorePackage.eINSTANCE; // As a side effect the EMF stuff is
											// initialized! Hurray

	public EventBModel load(final String s) throws IOException {
		EventBModel eventBModel = modelProvider.get();
		byte[] bytes = Base64.decodeBase64(s.getBytes());
		XMLResourceImpl r2 = new XMLResourceImpl();
		r2.load(new ByteArrayInputStream(bytes), new HashMap<Object, Object>());
		Project p = (Project) r2.getContents().get(0);
		eventBModel.initialize(p);
		return eventBModel;
	}
}
